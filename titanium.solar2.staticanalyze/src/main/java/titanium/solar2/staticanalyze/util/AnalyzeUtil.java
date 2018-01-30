package titanium.solar2.staticanalyze.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import mirrg.lithium.struct.Struct1;
import titanium.solar2.libs.analyze.Analyzer;

/**
 * {@link Analyzer} をZIPファイル内のデータに対して静的に適用するユーティリティ。
 */
public class AnalyzeUtil
{

	public static void doAnalyze(File dir, String zipExtension, Analyzer analyzer) throws IOException
	{
		Pattern pattern = Pattern.compile("(?:.*/)?\\d{5}-(.*)\\.dat");
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("uuuuMMdd-HHmmss");
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("uuuuMMdd-HHmmss-SSS");
		double[] buffer2 = new double[4096];

		analyzer.preAnalyze();
		AnalyzeUtil.visitData(
			buffer2.length,
			dir,
			array -> array.stream()
				.filter(f -> f.getName().endsWith(zipExtension))
				.sorted(Comparable::compareTo)
				.collect(Collectors.toCollection(ArrayList::new)),
			array -> array.stream()
				.sorted(Comparable::compareTo)
				.collect(Collectors.toCollection(ArrayList::new)),
			new IVisitDataListener() {
				@Override
				public void preFile(File zipFile, int i, int count)
				{
					System.out.println(i + "/" + count + ": " + zipFile.getAbsolutePath());
				}

				@Override
				public boolean preEntry(ZipEntry zipEntry)
				{
					Matcher matcher = pattern.matcher(zipEntry.getName());
					if (matcher.matches()) {
						try {
							analyzer.preChunk(LocalDateTime.parse(matcher.group(1), formatter1));
							System.out.println("Entry Accepted: " + zipEntry.getName());
							return true;
						} catch (DateTimeParseException e) {

			}
						try {
							analyzer.preChunk(LocalDateTime.parse(matcher.group(1), formatter2));
							System.out.println("Entry Accepted: " + zipEntry.getName());
							return true;
						} catch (DateTimeParseException e) {

			}
					}

					System.out.println("Entry Ignored: " + zipEntry.getName());
					return false;
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
				public void postEntry(ZipEntry zipEntry)
				{
					analyzer.postChunk();
				}
			});
		analyzer.postAnalyze();
	}

	/**
	 * 指定ディレクトリ内の全ファイルから内のエントリーを辞書順に巡回し、バイナリデータを処理する。
	 */
	public static void visitData(
		int bufferLength,
		File dir,
		Function<ArrayList<File>, ArrayList<File>> filterZipFile,
		Function<ArrayList<String>, ArrayList<String>> filterDataZipEntryName,
		IVisitDataListener visitDataListener) throws IOException
	{
		byte[] buffer = new byte[bufferLength];
		ArrayList<File> zipFiles = filterZipFile.apply(getFiles(dir));

		for (int i = 0; i < zipFiles.size(); i++) {
			File zipFile = zipFiles.get(i);

			visitDataListener.preFile(zipFile, i, zipFiles.size());

			visitZipEntries(zipFile, filterDataZipEntryName.apply(getZipEntryNames(zipFile)), (zipEntry, in) -> {

				if (visitDataListener.preEntry(zipEntry)) {

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

					visitDataListener.postEntry(zipEntry);
				}

			});

			visitDataListener.postFile(zipFile, i, zipFiles.size());

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
	 * 指定ZIPファイルのエントリー名を列挙する。
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
	 * 指定ディレクトリ内のファイルを列挙する。
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
