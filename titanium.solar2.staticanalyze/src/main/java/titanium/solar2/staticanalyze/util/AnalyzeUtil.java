package titanium.solar2.staticanalyze.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import mirrg.lithium.struct.Struct1;
import titanium.solar2.libs.analyze.Analyzer;
import titanium.solar2.staticanalyze.ILinePrinter;

/**
 * {@link Analyzer} をZIPファイル内のデータに対して静的に適用するユーティリティ。
 */
public class AnalyzeUtil
{

	public static ILinePrinter out = System.out::println;

	public static void doAnalyze(File directory, Analyzer analyzer) throws IOException
	{
		int bufferLength = 4096;
		String zipExtension = ".zip";
		Pattern pattern = Pattern.compile("\\d{5}-(.*)\\.dat");
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("uuuuMMdd-HHmmss");
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("uuuuMMdd-HHmmss-SSS");

		analyzer.preAnalyze();
		processDirectory(
			new byte[bufferLength],
			new IVisitDataListener() {

				double[] buffer2 = new double[bufferLength];

				@Override
				public void preFile(File file, EnumDataFileType dataFileType, int fileIndex, int fileCount)
				{
					out.println(MessageFormat.format("File Accepted: %s/%s [%s] %s",
						fileIndex,
						fileCount,
						dataFileType.name(),
						file.getAbsolutePath()));
				}

				@Override
				public void preEntry(String entryName, LocalDateTime time)
				{
					out.println("Entry Accepted: " + entryName);
					analyzer.preChunk(time);
				}

				@Override
				public void onData(byte[] buffer, int start, int length)
				{
					for (int i = 0; i < length; i++) {
						buffer2[i] = buffer[i + start];
					}
					analyzer.processData(buffer2, length, new Struct1<>(0.0));
				}

				@Override
				public void postEntry()
				{
					analyzer.postChunk();
				}

				@Override
				public void ignoreEntry(String entryName)
				{
					out.println("Entry Ignored: " + entryName);
				}

				@Override
				public void ignoreFile(File file)
				{
					out.println("File Ignored: " + file.getAbsolutePath());
				}

			},
			directory,
			n -> n.endsWith(zipExtension),
			new DatEntryNameParserOr(
				new DatEntryNameParserSimple(pattern, formatter1),
				new DatEntryNameParserSimple(pattern, formatter2)));
		analyzer.postAnalyze();
	}

	//

	/**
	 * ディレクトリを再帰的に検索しデータを読み出して通知する。
	 */
	public static void processDirectory(byte[] buffer, IVisitDataListener visitDataListener, File directory, Predicate<String> zipFileNamePredicate, IDatEntryNameParser datEntryNameParser)
		throws IOException
	{
		ArrayList<File> files = getFiles(directory).stream()
			.sorted(Comparable::compareTo)
			.collect(Collectors.toCollection(ArrayList::new));

		for (int i = 0; i < files.size(); i++) {
			File file = files.get(i);

			if (zipFileNamePredicate.test(file.getAbsolutePath())) {

				visitDataListener.preFile(file, EnumDataFileType.ZIP, i, files.size());
				processZipFile(buffer, visitDataListener, file, datEntryNameParser);
				visitDataListener.postFile();

				continue;
			}

			{
				Optional<LocalDateTime> oTime = datEntryNameParser.parse(file.getName());
				if (oTime.isPresent()) {

					visitDataListener.preFile(file, EnumDataFileType.DAT, i, files.size());
					processDatFile(buffer, visitDataListener, file, oTime.get());
					visitDataListener.postFile();

					continue;
				}
			}

			visitDataListener.ignoreFile(file);
		}
	}

	/**
	 * ZIPファイルの中からdatファイルを識別し、データを読み出して通知する。
	 */
	public static void processZipFile(byte[] buffer, IVisitDataListener visitDataListener, File zipFile, IDatEntryNameParser datEntryNameParser) throws IOException
	{
		visitZipEntries(zipFile, getZipEntryNames(zipFile).stream()
			.sorted(Comparable::compareTo)
			.collect(Collectors.toCollection(ArrayList::new)), (zipEntry, in) -> {
				String entryName = zipEntry.getName();

				String shortEntryName;
				if (entryName.contains("/")) {
					shortEntryName = entryName.substring(entryName.lastIndexOf('/') + 1);
				} else {
					shortEntryName = entryName;
				}

				Optional<LocalDateTime> oTime = datEntryNameParser.parse(shortEntryName);
				if (!oTime.isPresent()) {
					visitDataListener.ignoreEntry(entryName);
					return;
				}

				visitDataListener.preEntry(entryName, oTime.get());
				processInputStream(buffer, visitDataListener, in);
				visitDataListener.postEntry();

			});
	}

	/**
	 * datファイルからデータを読み出して通知する。
	 */
	public static void processDatFile(byte[] buffer, IVisitDataListener visitDataListener, File file, LocalDateTime time) throws IOException
	{
		try (InputStream in = new FileInputStream(file)) {

			visitDataListener.preEntry(file.getAbsolutePath(), time);
			processInputStream(buffer, visitDataListener, in);
			visitDataListener.postEntry();

		}
	}

	/**
	 * InputStreamの中から読めるだけ全部データを読み出して通知する。
	 */
	public static void processInputStream(byte[] buffer, IVisitDataListener visitDataListener, InputStream in)
	{
		while (true) {
			int length;
			try {
				length = in.read(buffer);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			if (length == -1) break;

			visitDataListener.onData(buffer, 0, length);
		}
	}

	//

	/**
	 * 指定ZIPファイルから指定の順でエントリーを開く。
	 */
	public static void visitZipEntries(File zipFile, ArrayList<String> zipEntryNames, IVisitZipEntriesListener listener) throws IOException
	{
		int i = 0;
		if (i >= zipEntryNames.size()) return;

		while (true) {
			try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
				while (true) {
					ZipEntry zipEntry = zipInputStream.getNextEntry();
					if (zipEntry == null) break;

					if (zipEntry.getName().equals(zipEntryNames.get(i))) {

						listener.onEntry(zipEntry, zipInputStream);

						i++;
						if (i >= zipEntryNames.size()) return;
					}

				}
			}
		}
	}

	/**
	 * 指定ZIPファイルのエントリー名をすべて列挙する。
	 */
	public static ArrayList<String> getZipEntryNames(File zipFile) throws IOException
	{
		ArrayList<String> zipEntries = new ArrayList<>();

		try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
			while (true) {
				ZipEntry zipEntry = zipInputStream.getNextEntry();
				if (zipEntry == null) break;

				zipEntries.add(zipEntry.getName());
			}
		}

		return zipEntries;
	}

	/**
	 * 指定ディレクトリ内のファイルを再帰的にすべて列挙する。
	 */
	public static ArrayList<File> getFiles(File dir) throws IOException
	{
		ArrayList<File> pathes = new ArrayList<>();

		Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			{
				pathes.add(file.toFile());
				return FileVisitResult.CONTINUE;
			}
		});

		return pathes;
	}

}
