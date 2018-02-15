package titanium.solar2.staticanalyze.sources.filesystem;

import static mirrg.lithium.swing.util.HSwing.*;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import mirrg.lithium.logging.LoggerRelay;
import mirrg.lithium.struct.Struct1;
import titanium.solar2.libs.analyze.Analyzer;
import titanium.solar2.staticanalyze.FiledProperties;
import titanium.solar2.staticanalyze.FiledProperty;
import titanium.solar2.staticanalyze.ISource;

public class SourceFileSystem implements ISource
{

	private FiledProperties p;
	private LoggerRelay logger;

	private JTextField textFieldSearchDirectory;
	private static final FiledProperty PROPERTY_SEARCH_DIRECTORY_PATH = new FiledProperty("searchDirectory.path", "");
	private JButton buttonSearchDirectory;
	private static final FiledProperty PROPERTY_SEARCH_DIRECTORY_CURRENT_DIRECTORY = new FiledProperty("searchDirectory.currentDirectory", ".");

	private JProgressBar progressBarFiles;
	private JLabel labelFile;
	private JProgressBar progressBarEntries;
	private JLabel labelEntry;

	public SourceFileSystem(FiledProperties p, LoggerRelay logger)
	{
		this.p = p;
		this.logger = logger;
	}

	@Override
	public String getTabTitle()
	{
		return "ファイル";
	}

	@Override
	public Component getComponent(JFrame frame)
	{
		return createBorderPanelUp(
			createBorderPanelLeft(
				new JLabel("検索対象"),
				createBorderPanelRight(
					process(setToolTipText(textFieldSearchDirectory = new JTextField(), "<html>"
						+ "解析対象のdatもしくはZIPファイル、もしくは検索対象のディレクトリを指定します。<br>"
						+ "検索はサブディレクトリに対して再帰的に行われます。<br>"
						+ "<br>"
						+ "datファイルの名称は、例えば「00000-20170101-000000-000.dat」のように<br>"
						+ "正規表現「\\d{5}-(.*)\\.dat」にマッチしなければなりません。<br>"
						+ "datファイルの名称の中央部は次のいずれかのパターンで示される時刻でなければなりません。<br>"
						+ "・uuuuMMdd-HHmmss<br>"
						+ "・uuuuMMdd-HHmmss-SSS<br>"
						+ "<br>"
						+ "datファイルはZIPファイルの中に存在しても構いません。<br>"
						+ "datファイルを含むZIPファイルは名称が「.zip」で終わらなければなりません。<br>"
						+ "ZIPファイル内のZIPファイルに対しては再帰的に探索されません。"), c -> {
							c.setText(p.get(PROPERTY_SEARCH_DIRECTORY_PATH));
							c.addFocusListener(new FocusAdapter() {
								@Override
								public void focusLost(FocusEvent e)
								{
									p.set(PROPERTY_SEARCH_DIRECTORY_PATH, c.getText());
								}
							});
						}),
					buttonSearchDirectory = createButton("参照", e -> {
						JFileChooser fileChooser = new JFileChooser();
						fileChooser.setCurrentDirectory(new File(p.get(PROPERTY_SEARCH_DIRECTORY_CURRENT_DIRECTORY)));
						fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
						fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("titanium.solar2データファイル(*.dat)", "dat"));
						fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("ZIPアーカイブ(*.zip)", "zip"));
						if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
							p.set(PROPERTY_SEARCH_DIRECTORY_PATH, fileChooser.getSelectedFile().getAbsolutePath());
							p.set(PROPERTY_SEARCH_DIRECTORY_CURRENT_DIRECTORY, fileChooser.getCurrentDirectory().getAbsolutePath());
							textFieldSearchDirectory.setText(fileChooser.getSelectedFile().getAbsolutePath());
						}
					}))),
			process(progressBarFiles = new JProgressBar(), c -> {
				c.setValue(0);
				c.setString("...");
				c.setStringPainted(true);
				c.setFont(new Font(Font.MONOSPACED, Font.PLAIN, c.getFont().getSize()));
			}),
			labelFile = new JLabel("..."),
			process(progressBarEntries = new JProgressBar(), c -> {
				c.setValue(0);
				c.setString("...");
				c.setStringPainted(true);
				c.setFont(new Font(Font.MONOSPACED, Font.PLAIN, c.getFont().getSize()));
			}),
			labelEntry = new JLabel("..."),
			null);
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		textFieldSearchDirectory.setEnabled(enabled);
		buttonSearchDirectory.setEnabled(enabled);
	}

	@Override
	public void doAnalyze(Analyzer analyzer) throws IOException, InterruptedException
	{
		File directory = new File(textFieldSearchDirectory.getText());
		int bufferLength = 4096;
		analyzer.preAnalyze();
		try {
			processDirectoryOrFile(
				new byte[bufferLength],
				new IVisitDataListener() {

					double[] buffer2 = new double[bufferLength];

					@Override
					public void preFile(File file, EnumDataFileType dataFileType, int fileIndex, int fileCount)
					{
						SwingUtilities.invokeLater(() -> {
							progressBarFiles.setValue(fileIndex);
							progressBarFiles.setMinimum(0);
							progressBarFiles.setMaximum(fileCount);
							progressBarFiles.setString("ファイル " + fileIndex + " / " + fileCount);
							labelFile.setText("" + file);

							progressBarEntries.setValue(0);
							progressBarEntries.setMinimum(0);
							progressBarEntries.setMaximum(0);
							progressBarEntries.setString("エントリー " + 0 + " / " + 0);
							labelEntry.setText("...");
						});

						logger.info(String.format("File Accepted: [%s] %s",
							dataFileType.name(),
							file.getAbsolutePath()));
					}

					@Override
					public void preEntry(String entryName, LocalDateTime time, int entryIndex, int entryCount)
					{
						SwingUtilities.invokeLater(() -> {
							progressBarEntries.setValue(entryIndex);
							progressBarEntries.setMinimum(0);
							progressBarEntries.setMaximum(entryCount);
							progressBarEntries.setString("エントリー " + entryIndex + " / " + entryCount);
							labelEntry.setText(entryName);
						});

						logger.info("Entry Accepted: " + entryName);
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
					public void ignoreEntry(String entryName, int entryIndex, int entryCount)
					{
						SwingUtilities.invokeLater(() -> {
							progressBarEntries.setValue(entryIndex);
							progressBarEntries.setMinimum(0);
							progressBarEntries.setMaximum(entryCount);
							progressBarEntries.setString("エントリー " + entryIndex + " / " + entryCount);
							labelEntry.setText(entryName);
						});

						logger.debug("Entry Ignored: " + entryName);
					}

					@Override
					public void ignoreFile(File file, int fileIndex, int fileCount)
					{
						SwingUtilities.invokeLater(() -> {
							progressBarFiles.setValue(fileIndex);
							progressBarFiles.setMinimum(0);
							progressBarFiles.setMaximum(fileCount);
							progressBarFiles.setString("ファイル " + fileIndex + " / " + fileCount);
							labelFile.setText("" + file);

							progressBarEntries.setValue(0);
							progressBarEntries.setMinimum(0);
							progressBarEntries.setMaximum(0);
							progressBarEntries.setString("エントリー " + 0 + " / " + 0);
							labelEntry.setText("...");
						});

						logger.debug("File Ignored: " + file.getAbsolutePath());
					}

				},
				directory,
				n -> n.endsWith(".zip"),
				new DatEntryNameParserOr(
					new DatEntryNameParserSimple(Pattern.compile("\\d{5}-(.*)\\.dat"), DateTimeFormatter.ofPattern("uuuuMMdd-HHmmss")),
					new DatEntryNameParserSimple(Pattern.compile("\\d{5}-(.*)\\.dat"), DateTimeFormatter.ofPattern("uuuuMMdd-HHmmss-SSS"))));
		} finally {
			analyzer.postAnalyze();
		}
	}

	//

	/**
	 * ディレクトリを再帰的に検索しデータを読み出して通知する。
	 * ファイルが与えられた場合、そのファイルだけを処理する。
	 */
	public static void processDirectoryOrFile(
		byte[] buffer,
		IVisitDataListener visitDataListener,
		File file,
		Predicate<String> zipFileNamePredicate,
		IDatEntryNameParser datEntryNameParser) throws IOException, InterruptedException
	{
		if (file.isDirectory()) {

			ArrayList<File> files = getFiles(file).stream()
				.sorted(Comparable::compareTo)
				.collect(Collectors.toCollection(ArrayList::new));

			for (int i = 0; i < files.size(); i++) {
				File file2 = files.get(i);

				processFile(
					buffer,
					visitDataListener,
					file2,
					i + 1,
					files.size(),
					zipFileNamePredicate,
					datEntryNameParser);

			}

		} else if (file.isFile()) {

			processFile(
				buffer,
				visitDataListener,
				file,
				1,
				1,
				zipFileNamePredicate,
				datEntryNameParser);

		} else {

		}
	}

	/**
	 * ファイルを処理する。処理できない可能性もある。
	 */
	public static void processFile(
		byte[] buffer,
		IVisitDataListener visitDataListener,
		File file,
		int fileIndex,
		int fileCount,
		Predicate<String> zipFileNamePredicate,
		IDatEntryNameParser datEntryNameParser) throws IOException, InterruptedException
	{

		if (zipFileNamePredicate.test(file.getAbsolutePath())) {

			visitDataListener.preFile(file, EnumDataFileType.ZIP, fileIndex, fileCount);
			try {
				processZipFile(buffer, visitDataListener, file, datEntryNameParser);
			} finally {
				visitDataListener.postFile();
			}

			return;
		}

		{
			Optional<LocalDateTime> oTime = datEntryNameParser.parse(file.getName());
			if (oTime.isPresent()) {

				visitDataListener.preFile(file, EnumDataFileType.DAT, fileIndex, fileCount);
				try {
					processDatFile(buffer, visitDataListener, file, oTime.get());
				} finally {
					visitDataListener.postFile();
				}

				return;
			}
		}

		visitDataListener.ignoreFile(file, fileIndex, fileCount);
	}

	/**
	 * ZIPファイルの中からdatファイルを識別し、データを読み出して通知する。
	 */
	public static void processZipFile(byte[] buffer, IVisitDataListener visitDataListener, File zipFile, IDatEntryNameParser datEntryNameParser) throws IOException, InterruptedException
	{
		ArrayList<String> zipEntryNames = getZipEntryNames(zipFile).stream()
			.sorted(Comparable::compareTo)
			.collect(Collectors.toCollection(ArrayList::new));

		visitZipEntries(zipFile, zipEntryNames, new IVisitZipEntriesListener() {
			private int i = 0;

			@Override
			public void onEntry(ZipEntry zipEntry, InputStream in) throws InterruptedException
			{
				String entryName = zipEntry.getName();

				Optional<LocalDateTime> oTime = datEntryNameParser.parse(getShortEntryName(entryName));
				if (!oTime.isPresent()) {
					visitDataListener.ignoreEntry(entryName, i + 1, zipEntryNames.size());
					i++;
					return;
				}

				visitDataListener.preEntry(entryName, oTime.get(), i + 1, zipEntryNames.size());
				try {
					processInputStream(buffer, visitDataListener, in);
				} finally {
					visitDataListener.postEntry();
				}
				i++;

			}
		});
	}

	/**
	 * datファイルからデータを読み出して通知する。
	 */
	public static void processDatFile(byte[] buffer, IVisitDataListener visitDataListener, File file, LocalDateTime time) throws IOException, InterruptedException
	{
		try (InputStream in = new FileInputStream(file)) {

			visitDataListener.preEntry(file.getAbsolutePath(), time, 1, 1);
			try {
				processInputStream(buffer, visitDataListener, in);
			} finally {
				visitDataListener.postEntry();
			}

		}
	}

	/**
	 * InputStreamの中から読めるだけ全部データを読み出して通知する。
	 */
	public static void processInputStream(byte[] buffer, IVisitDataListener visitDataListener, InputStream in) throws InterruptedException
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

			Thread.sleep(0);
		}
	}

	//

	/**
	 * 指定ZIPファイルから指定の順でエントリーを開く。
	 */
	public static void visitZipEntries(File zipFile, ArrayList<String> zipEntryNames, IVisitZipEntriesListener listener) throws IOException, InterruptedException
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
	 * ZIPエントリーのファイル名部分を取得します。
	 */
	public static String getShortEntryName(String entryName)
	{
		if (entryName.contains("/")) {
			return entryName.substring(entryName.lastIndexOf('/') + 1);
		} else {
			return entryName;
		}
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
