package titanium.solar2.staticanalyze;

import static mirrg.lithium.swing.util.HSwing.*;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import mirrg.lithium.lang.HLog;
import mirrg.lithium.logging.LoggerPrintStream;
import mirrg.lithium.logging.LoggerRelay;
import mirrg.lithium.logging.LoggerTextPane;
import mirrg.lithium.logging.OutputStreamLogging;
import mirrg.lithium.struct.Struct1;
import mirrg.lithium.swing.util.HSwing;
import titanium.solar2.libs.analyze.Analyzer;
import titanium.solar2.libs.analyze.IFilter;
import titanium.solar2.staticanalyze.util.AnalyzeUtil;
import titanium.solar2.staticanalyze.util.DatEntryNameParserOr;
import titanium.solar2.staticanalyze.util.DatEntryNameParserSimple;
import titanium.solar2.staticanalyze.util.EnumDataFileType;
import titanium.solar2.staticanalyze.util.IVisitDataListener;

public class Main
{

	private static FiledProperties p;

	private static LoggerRelay logger = new LoggerRelay();
	private static String KEY_LOG_FILE = "log.file";

	private static JTextField textFieldSearchDirectory;
	private static String KEY_SEARCH_DIRECTORY_PATH = "searchDirectory.path";
	private static JButton buttonSearchDirectory;
	private static String KEY_SEARCH_DIRECTORY_CURRENT_DIRECTORY = "searchDirectory.currentDirectory";

	private static JTextField textFieldSaveFile;
	private static String KEY_SAVE_FILE_PATH = "saveFile.path";
	private static JButton buttonSaveFile;
	private static String KEY_SAVE_FILE_CURRENT_DIRECTORY = "saveFile.currentDirectory";

	private static JComboBox<String> comboBoxPresets;
	private static JButton buttonImport;
	private static JButton buttonExport;
	private static String KEY_IMPORT_AND_EXPORT_CURRENT_DIRECTORY = "importAndExport.currentDirectory";
	private static String KEY_PRESETS = "presets";

	private static JTextPane textPaneScript;

	private static JButton buttonValidate;
	private static JFormattedTextField textFieldSamplesPerSecond;
	private static JButton buttonAnalyze;
	private static JButton buttonInterrupt;

	private static JLabel labelAnalyzeTime;
	private static JLabel labelChunkTime;
	private static JProgressBar progressBarFiles;
	private static JProgressBar progressBarEntries;

	private static PanelWaveform panelWaveform;
	private static JCheckBox checkBoxPaintGraph;

	private static JLabel labelOutput;
	private static LoggerTextPane loggerTextPaneOutput;
	private static int outputLineCount;

	public static void main(String[] args)
	{
		// Swing設定
		HSwing.setWindowsLookAndFeel();
		ToolTipManager.sharedInstance().setInitialDelay(500);
		ToolTipManager.sharedInstance().setDismissDelay(60000);

		p = new FiledProperties(new File("./staticanalyze.properties"))
			.setDefault(KEY_SEARCH_DIRECTORY_PATH, "")
			.setDefault(KEY_SEARCH_DIRECTORY_CURRENT_DIRECTORY, ".")
			.setDefault(KEY_SAVE_FILE_PATH, "")
			.setDefault(KEY_SAVE_FILE_CURRENT_DIRECTORY, ".")
			.setDefault(KEY_LOG_FILE, "./staticanalyze.log.txt")
			.setDefault(KEY_IMPORT_AND_EXPORT_CURRENT_DIRECTORY, ".")
			.setDefault(KEY_PRESETS, ""
				+ "resource://scripts/default.groovy" + ";"
				+ "resource://scripts/existence.groovy" + ";"
				+ "resource://scripts/pulseLink.groovy" + ";"
				+ "resource://scripts/traditional.groovy");
		p.init();

		// ロガー設定
		AnalyzeUtil.out = logger;
		logger.addLogger(new LoggerPrintStream(System.out));
		try {
			PrintStream out = new PrintStream(new FileOutputStream(new File(p.get(KEY_LOG_FILE))));
			logger.addLogger(new LoggerPrintStream(out));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		// ウィンドウ生成
		JFrame frame = new JFrame("Static Analyzer - titanium.solar2");
		frame.setIconImage(new ImageIcon(Main.class.getResource("icon.png")).getImage());
		frame.setLayout(new CardLayout());
		frame.setJMenuBar(createJMenuBar(
			createJMenu("ヘルプ",
				createJMenuItem("ヒント", e -> {
					JOptionPane.showMessageDialog(frame, "GUIコンポーネントにカーソルを合わせるとヒントが表示されます。");
				}))));
		{
			Component mainPane = createBorderPanelUp(
				createBorderPanelLeft(
					new JLabel("検索対象"),
					createBorderPanelRight(
						process(setToolTipText(textFieldSearchDirectory = new JTextField(), "<html>"
							+ "解析対象のdatもしくはZIPファイル、もしくは検索対象のディレクトリを指定します。<br>"
							+ "検索はサブディレクトリに対して再帰的に行われます。<br>"
							+ "<br>"
							+ "datファイルの名称は、例えば「00000-20170101-000000-000.dat」のように<br>"
							+ "正規表現「\\d{5}-(.*)\\.dat」にマッチしなければなりません。<br>"
							+ "datファイルの名称の中央部は次のいずれかパターンで示される時刻でなければなりません。<br>"
							+ "・uuuuMMdd-HHmmss<br>"
							+ "・uuuuMMdd-HHmmss-SSS<br>"
							+ "<br>"
							+ "datファイルはZIPファイルの中に存在しても構いません。<br>"
							+ "datファイルを含むことができるZIPファイルは名称が「.zip」で終わらなければなりません。"), c -> {
								c.setText(p.get(KEY_SEARCH_DIRECTORY_PATH));
								c.addFocusListener(new FocusAdapter() {
									@Override
									public void focusLost(FocusEvent e)
									{
										p.set(KEY_SEARCH_DIRECTORY_PATH, c.getText());
									}
								});
							}),
						buttonSearchDirectory = createButton("参照", e -> {
							JFileChooser fileChooser = new JFileChooser();
							fileChooser.setCurrentDirectory(new File(p.get(KEY_SEARCH_DIRECTORY_CURRENT_DIRECTORY)));
							fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
							fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("titanium.solar2データファイル(*.dat)", "dat"));
							fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("ZIPアーカイブ(*.zip)", "zip"));
							if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
								p.set(KEY_SEARCH_DIRECTORY_PATH, fileChooser.getSelectedFile().getAbsolutePath());
								p.set(KEY_SEARCH_DIRECTORY_CURRENT_DIRECTORY, fileChooser.getCurrentDirectory().getAbsolutePath());
								textFieldSearchDirectory.setText(fileChooser.getSelectedFile().getAbsolutePath());
							}
						}))),
				createBorderPanelLeft(
					new JLabel("出力ファイル"),
					createBorderPanelRight(
						process(setToolTipText(textFieldSaveFile = new JTextField(), "<html>"
							+ "解析内容の出力先ファイルです。"), c -> {
								c.setText(p.get(KEY_SAVE_FILE_PATH));
								c.addFocusListener(new FocusAdapter() {
									@Override
									public void focusLost(FocusEvent e)
									{
										p.set(KEY_SAVE_FILE_PATH, c.getText());
									}
								});
							}),
						buttonSaveFile = createButton("参照", e -> {
							JFileChooser fileChooser = new JFileChooser();
							fileChooser.setCurrentDirectory(new File(p.get(KEY_SAVE_FILE_CURRENT_DIRECTORY)));
							fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
							if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
								p.set(KEY_SAVE_FILE_PATH, fileChooser.getSelectedFile().getAbsolutePath());
								p.set(KEY_SAVE_FILE_CURRENT_DIRECTORY, fileChooser.getCurrentDirectory().getAbsolutePath());
								textFieldSaveFile.setText(fileChooser.getSelectedFile().getAbsolutePath());
							}
						}))),
				createBorderPanelLeft(
					new JLabel("解析スクリプト"),
					createBorderPanelRight(
						process(setToolTipText(comboBoxPresets = new JComboBox<>(new DefaultComboBoxModel<>()), "<html>"
							+ "ここから解析スクリプトのサンプルや読み込んだことのある解析スクリプトを呼び出すことができます。"), c -> {
								frame.addWindowListener(new WindowAdapter() {
									@Override
									public void windowOpened(WindowEvent e)
									{
										updatePresets();
									}
								});
								comboBoxPresets.addActionListener(e -> {
									String preset = comboBoxPresets.getItemAt(comboBoxPresets.getSelectedIndex());

									String source;
									try {
										source = loadPreset(preset);
									} catch (Exception e2) {
										AnalyzeUtil.out.error("Failed to load preset: " + preset);
										AnalyzeUtil.out.error(e2);
										return;
									}

									textPaneScript.setText(source);
								});
							}),
						setToolTipText(buttonImport = createButton("インポート", e -> {
							try {
								JFileChooser fileChooser = new JFileChooser();
								fileChooser.setCurrentDirectory(new File(p.get(KEY_IMPORT_AND_EXPORT_CURRENT_DIRECTORY)));
								fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
								fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("titanium.solar2解析スクリプトファイル(*.groovy)", "groovy"));
								if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
									p.set(KEY_IMPORT_AND_EXPORT_CURRENT_DIRECTORY, fileChooser.getCurrentDirectory().getAbsolutePath());
									addPreset(fileChooser.getSelectedFile().toURI().toURL().toString(), true);
								}
							} catch (Exception e2) {
								AnalyzeUtil.out.error(e2);
								return;
							}
						}), "ファイルから解析スクリプトを読み込みます。"),
						setToolTipText(buttonExport = createButton("エクスポート", e -> {
							try {
								JFileChooser fileChooser = new JFileChooser();
								fileChooser.setCurrentDirectory(new File(p.get(KEY_IMPORT_AND_EXPORT_CURRENT_DIRECTORY)));
								fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
								fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("titanium.solar2解析スクリプトファイル(*.groovy)", "groovy"));
								if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
									p.set(KEY_IMPORT_AND_EXPORT_CURRENT_DIRECTORY, fileChooser.getCurrentDirectory().getAbsolutePath());
									try (PrintStream out = new PrintStream(new FileOutputStream(fileChooser.getSelectedFile()))) {
										out.print(textPaneScript.getText());
									}
									addPreset(fileChooser.getSelectedFile().toURI().toURL().toString(), false);
								}
							} catch (Exception e2) {
								AnalyzeUtil.out.error(e2);
								return;
							}
						}), "解析スクリプトをファイルに保存します。"))),
				createBorderPanelDown(
					createScrollPane(setToolTipText(process(textPaneScript = new JTextPane(), c -> {
						c.setFont(new Font(Font.MONOSPACED, Font.PLAIN, c.getFont().getSize()));
						c.setTransferHandler(new TransferHandler() {
							private File file;

							@Override
							public boolean canImport(TransferSupport support)
							{
								if (!support.isDrop()) return false;
								if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) return false;

								try {
									@SuppressWarnings("unchecked")
									List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
									if (files.size() != 1) return false;
									file = files.get(0);
									if (!file.isFile()) return false;
								} catch (Exception e) {
									return false;
								}
								return true;
							}

							@Override
							public boolean importData(TransferSupport support)
							{
								// TODO D&D読み込み実装
								if (!canImport(support)) return false;
								try {
									addPreset(file.toURI().toURL().toString(), true);
								} catch (MalformedURLException e) {
									AnalyzeUtil.out.error(e);
								}
								return true;
							}
						});
					}), "<html>"
						+ "解析スクリプトです。Groovyとして構文解析されます。<br>"
						+ "スクリプトは非nullのAnalyzerを戻り値として持たなければなりません。<br>"
						+ "以下の組み込み変数が利用できます。<br>"
						+ "<br>"
						+ "・samplesPerSecond - フォーム上で指定されたサンプリングレートです。<br>"
						+ "・out - 解析結果を出力するためのPrintStreamです。<br>"
						+ "・filterExtenstion - グラフの表示などを行う拡張フィルタです。<br>"), 200, 200),
					createBorderPanelLeft(
						setToolTipText(buttonValidate = createButton("スクリプトの検証", e -> {
							Analyzer analyzer;
							try {
								analyzer = createAnalyzer(new OutputStream() {
									@Override
									public void write(int b) throws IOException
									{

						}
								});
							} catch (Exception e2) {
								AnalyzeUtil.out.info(e2);
								return;
							}
							if (analyzer == null) {
								AnalyzeUtil.out.info(new NullPointerException("analyzer"));
								return;
							}
							AnalyzeUtil.out.info("Successfully Compiled: " + analyzer);
						}), "解析スクリプトをコンパイルし、構文を検証します。"),
						createBorderPanelRight(
							null,
							new JLabel("サンプリングレート"),
							process(setToolTipText(textFieldSamplesPerSecond = new JFormattedTextField(new DecimalFormat("0")), "<html>"
								+ "データのサンプリングレートです。解析全体に影響を及ぼします。"), c -> {
									c.setColumns(5);
									c.setHorizontalAlignment(JTextField.RIGHT);
									c.setValue(44100);
								}),
							setToolTipText(buttonAnalyze = createButton("解析", e -> {
								analyze();
							}), "<html>"
								+ "解析を開始します。<br>"
								+ "解析前に解析結果の出力先ファイルが正しいことを確認してください。"),
							setToolTipText(process(buttonInterrupt = createButton("中断", e -> {
								interrupt();
							}), c -> {
								c.setEnabled(false);
							}), "実行中の解析を強制終了します。"))),
					process(labelAnalyzeTime = new JLabel("..."), c -> {
						c.setFont(new Font(Font.MONOSPACED, Font.PLAIN, c.getFont().getSize()));
					}),
					process(labelChunkTime = new JLabel("..."), c -> {
						c.setFont(new Font(Font.MONOSPACED, Font.PLAIN, c.getFont().getSize()));
					}),
					process(progressBarFiles = new JProgressBar(), c -> {
						c.setValue(0);
						c.setString("...");
						c.setStringPainted(true);
						c.setFont(new Font(Font.MONOSPACED, Font.PLAIN, c.getFont().getSize()));
					}),
					process(progressBarEntries = new JProgressBar(), c -> {
						c.setValue(0);
						c.setString("...");
						c.setStringPainted(true);
						c.setFont(new Font(Font.MONOSPACED, Font.PLAIN, c.getFont().getSize()));
					})));
			frame.add(createSplitPaneVertical(0,
				createSplitPaneVertical(1,
					createMargin(4, mainPane),
					createBorderPanelLeft(
						process(setToolTipText(new JSlider(JSlider.VERTICAL, -100, 100, 0), "<html>"
							+ "グラフの縦方向の縮尺を変更します。<br>"
							+ "1目盛りにつき表示の縮尺が10倍になります。"), c -> {
								c.addChangeListener(e -> {
									panelWaveform.setZoom(Math.pow(10, 1.0 * c.getValue() / 20));
									panelWaveform.repaint();
								});
								c.setPreferredSize(new Dimension(32, 64));
								c.setMajorTickSpacing(20);
								c.setPaintTicks(true);
							}),
						createBorderPanelRight(
							setToolTipText(process(panelWaveform = new PanelWaveform(), c -> {
								c.setMinimumSize(new Dimension(0, 64));
								c.setPreferredSize(new Dimension(200, 64));
							}), "1ピクセルにつき100ms分のサンプルが含まれます。"),
							setToolTipText(process(checkBoxPaintGraph = new JCheckBox(), c -> {
								c.setSelected(true);
							}), "ONのとき、グラフを更新します。")))),
				createSplitPaneVertical(1,
					createBorderPanelUp(
						createBorderPanelLeft(
							setToolTipText(createButton("クリア", e -> {
								clearOutput();
							}), "解析結果の画面からすべての行を消去します。"),
							labelOutput = new JLabel("..."),
							null),
						createScrollPane(setToolTipText((process(loggerTextPaneOutput = new LoggerTextPane(100) {
							@Override
							public void printlnDirectly(String string, AttributeSet attributeSet)
							{
								super.printlnDirectly(string, attributeSet);
								outputLineCount++;
								labelOutput.setText("" + outputLineCount);
							}
						}, c -> {
							c.formatter = (string, oLogLevel) -> string;
						})).getTextPane(), "<html>"
							+ "解析結果のデータが出力されます。<br>"
							+ "すべてのログを見るには解析結果の出力先ファイルを参照してください。"), 200, 100)),
					createScrollPane(setToolTipText(process(new LoggerTextPane(1000), l -> {
						logger.addLogger(l);
					}).getTextPane(), "<html>"
						+ "ここには解析結果のデータとは別に実行のログが出力されます。<br>"
						+ "すべてのログを見るにはログファイルを参照してください。"), 200, 100))));
		}
		frame.pack();
		frame.setSize(500, frame.getSize().height);
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				synchronized (lock) {
					if (thread != null) {
						thread.interrupt();
					}
				}
			}
		});
		frame.setVisible(true);
	}

	private static void clearOutput()
	{
		loggerTextPaneOutput.clear();
		outputLineCount = 0;
		labelOutput.setText("" + outputLineCount);
	}

	private static void addPreset(String preset, boolean changePreset)
	{
		String[] presets = p.get(KEY_PRESETS).split(";");
		for (int i = 0; i < presets.length; i++) {
			if (presets[i].equals(preset)) {
				comboBoxPresets.setSelectedIndex(i);
				return;
			}
		}

		// 新規追加

		p.set(KEY_PRESETS, p.get(KEY_PRESETS) + ";" + preset);
		comboBoxPresets.addItem(preset);
		if (changePreset) {
			comboBoxPresets.setSelectedIndex(comboBoxPresets.getItemCount() - 1);
		}
	}

	private static String loadPreset(String preset) throws Exception
	{
		if (preset.startsWith("resource://")) {
			return getResourceAsString(preset.substring("resource://".length()));
		} else {
			return new BufferedReader(new InputStreamReader(new URL(preset).openStream())).lines()
				.collect(Collectors.joining(System.lineSeparator()));
		}
	}

	private static void updatePresets()
	{
		comboBoxPresets.removeAllItems();
		for (String string : p.get(KEY_PRESETS).split(";")) {
			comboBoxPresets.addItem(string);
		}
	}

	private static int getSamplesPerSecond()
	{
		return ((Number) textFieldSamplesPerSecond.getValue()).intValue();
	}

	private static String format(LocalDateTime time)
	{
		return DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss.SSS").format(time);
	}

	// TODO mirrg
	private static FileOutputStream getOutputStreamAndMkdirs(File file) throws FileNotFoundException
	{
		file.getAbsoluteFile().getParentFile().mkdirs();
		return new FileOutputStream(file);
	}

	//

	private static Thread thread = null;
	private static Object lock = new Object();
	private static Timer timer = new Timer(20, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e)
		{
			labelAnalyzeTime.setText(String.format("解析開始: %s; 経過: %.03f秒",
				format(startTime),
				1.0 * (System.nanoTime() - startNanoTime) / 1_000_000_000));
		}
	});
	static {
		timer.setRepeats(true);
	}
	private static LocalDateTime startTime;
	private static long startNanoTime;

	private static void analyze()
	{
		synchronized (lock) {
			if (thread != null) {
				AnalyzeUtil.out.error("前回の解析が終わっていません。");
				return;
			}

			// 解析状態にする

			thread = new Thread(() -> {
				try {
					try (PrintStream out1 = new PrintStream(getOutputStreamAndMkdirs(new File(textFieldSaveFile.getText())));
						OutputStreamLogging out2 = new OutputStreamLogging(loggerTextPaneOutput)) {
						OutputStream out3 = new OutputStream() {
							@Override
							public void write(int b) throws IOException
							{
								out1.write(b);
								out2.write(b);
							}

							@Override
							public void write(byte[] b, int off, int len) throws IOException
							{
								out1.write(b, off, len);
								out2.write(b, off, len);
							}
						};

						//

						AnalyzeUtil.out.info("Analyze Start");

						Analyzer analyzer;
						try {
							analyzer = createAnalyzer(out3);
						} catch (Exception e) {
							AnalyzeUtil.out.error(e);
							return;
						}

						try {
							doAnalyze(new File(textFieldSearchDirectory.getText()), analyzer);
						} catch (InterruptedException e) {
							AnalyzeUtil.out.warn("Analyze Interrupted");
							return;
						} catch (Exception e) {
							AnalyzeUtil.out.error(e);
							return;
						}

						AnalyzeUtil.out.info("Analyze Finished");
					} catch (IOException e1) {
						AnalyzeUtil.out.error(HLog.getStackTrace(e1));
					}
				} finally {
					synchronized (lock) {
						timer.stop();

						thread = null;
						textFieldSearchDirectory.setEnabled(true);
						buttonSearchDirectory.setEnabled(true);
						textFieldSaveFile.setEnabled(true);
						buttonSaveFile.setEnabled(true);
						comboBoxPresets.setEnabled(true);
						buttonImport.setEnabled(true);
						buttonExport.setEnabled(true);
						textPaneScript.setEnabled(true);
						buttonValidate.setEnabled(true);
						textFieldSamplesPerSecond.setEnabled(true);
						buttonAnalyze.setEnabled(true);
						buttonInterrupt.setEnabled(false);
					}
				}
			});
			textFieldSearchDirectory.setEnabled(false);
			buttonSearchDirectory.setEnabled(false);
			textFieldSaveFile.setEnabled(false);
			buttonSaveFile.setEnabled(false);
			comboBoxPresets.setEnabled(false);
			buttonImport.setEnabled(false);
			buttonExport.setEnabled(false);
			textPaneScript.setEnabled(false);
			buttonValidate.setEnabled(false);
			textFieldSamplesPerSecond.setEnabled(false);
			buttonAnalyze.setEnabled(false);
			buttonInterrupt.setEnabled(true);

			clearOutput();

			startTime = LocalDateTime.now();
			startNanoTime = System.nanoTime();
			timer.start();

			thread.start();
		}
	}

	private static Analyzer createAnalyzer(OutputStream out) throws Exception
	{
		Binding binding = new Binding();
		binding.setVariable("samplesPerSecond", getSamplesPerSecond());
		binding.setVariable("out", new PrintStream(out));
		binding.setVariable("filterExtension", new FilterStaticAnalyzeGUIExtension());
		GroovyShell groovyShell = new GroovyShell(binding);

		String header = getResourceAsString("header.groovy");
		String src = textPaneScript.getText();

		return (Analyzer) groovyShell.evaluate(header + System.lineSeparator() + src);
	}

	public static String getResourceAsString(String name)
	{
		try {
			return new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream(name), "UTF-8")).lines()
				.collect(Collectors.joining(System.lineSeparator()));
		} catch (UnsupportedEncodingException e) {
			AnalyzeUtil.out.error(e);
			return new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream(name))).lines()
				.collect(Collectors.joining(System.lineSeparator()));
		}
	}

	public static URL getResourceAsURL(String name)
	{
		return Main.class.getResource(name);
	}

	private static class FilterStaticAnalyzeGUIExtension implements IFilter
	{

		private long x = 0;
		private LocalDateTime chunkTime;
		private long xInChunk;

		private int samples = 0;
		private double min = 0;
		private double max = 0;

		@Override
		public void preChunk(LocalDateTime time)
		{
			chunkTime = time;
			xInChunk = 0;
		}

		@Override
		public void processData(double[] buffer, int length, Struct1<Double> sOffset)
		{
			String string = String.format("波形時刻: %s; サンプル数: %s",
				format(chunkTime.plusNanos((long) (1.0 * xInChunk / getSamplesPerSecond() * 1_000_000_000))),
				NumberFormat.getIntegerInstance().format(x));
			SwingUtilities.invokeLater(() -> labelChunkTime.setText(string));
			x += length;
			xInChunk += length;

			if (checkBoxPaintGraph.isSelected()) {
				for (int i = 0; i < length; i++) {
					if (samples >= getSamplesPerSecond() / 10 - 1) {
						panelWaveform.addEntry(min, max);
						samples = 0;
						min = 0;
						max = 0;
					} else {
						samples++;
					}

					double a = buffer[i];
					if (a < min) min = a;
					if (a > max) max = a;
				}
			}
		}

	}

	private static void doAnalyze(File directory, Analyzer analyzer) throws IOException, InterruptedException
	{
		int bufferLength = 4096;
		analyzer.preAnalyze();
		try {
			AnalyzeUtil.processDirectoryOrFile(
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

							progressBarEntries.setValue(0);
							progressBarEntries.setMinimum(0);
							progressBarEntries.setMaximum(0);
							progressBarEntries.setString("エントリー " + 0 + " / " + 0);
						});

						AnalyzeUtil.out.info(String.format("File Accepted: [%s] %s",
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
						});

						AnalyzeUtil.out.info("Entry Accepted: " + entryName);
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
						});

						AnalyzeUtil.out.debug("Entry Ignored: " + entryName);
					}

					@Override
					public void ignoreFile(File file, int fileIndex, int fileCount)
					{
						SwingUtilities.invokeLater(() -> {
							progressBarFiles.setValue(fileIndex);
							progressBarFiles.setMinimum(0);
							progressBarFiles.setMaximum(fileCount);
							progressBarFiles.setString("ファイル " + fileIndex + " / " + fileCount);

							progressBarEntries.setValue(0);
							progressBarEntries.setMinimum(0);
							progressBarEntries.setMaximum(0);
							progressBarEntries.setString("エントリー " + 0 + " / " + 0);
						});

						AnalyzeUtil.out.debug("File Ignored: " + file.getAbsolutePath());
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

	private static void interrupt()
	{
		synchronized (lock) {
			if (thread == null) {
				AnalyzeUtil.out.error("解析が実行中ではありません。");
				return;
			}

			// 解析を終わらせる。

			thread.interrupt();
		}
	}

	//

	// TODO mirrg lib
	public static JMenuItem createJMenuItem(String text, ActionListener actionListener)
	{
		JMenuItem menuItem = new JMenuItem(text);
		menuItem.addActionListener(actionListener);
		return menuItem;
	}

	// TODO mirrg lib
	public static JMenu createJMenu(String text, Component... components)
	{
		JMenu menu = new JMenu(text);
		for (Component component : components) {
			menu.add(component);
		}
		return menu;
	}

	// TODO mirrg lib
	public static JMenuBar createJMenuBar(Component... components)
	{
		JMenuBar menuBar = new JMenuBar();
		for (Component component : components) {
			menuBar.add(component);
		}
		return menuBar;
	}

	// TODO mirrg lib
	public static JPanel createMargin(int margin, Component createBorderPanelUp)
	{
		return process(createPanel(createBorderPanelUp), c -> {
			c.setLayout(new CardLayout());
			c.setBorder(new EmptyBorder(margin, margin, margin, margin));
		});
	}

	// TODO mirrg lib
	public static JSplitPane createSplitPaneHorizontal(Component c1, Component c2)
	{
		return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, c1, c2);
	}

	// TODO mirrg lib
	public static JSplitPane createSplitPaneHorizontal(double resizeWeight, Component c1, Component c2)
	{
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, c1, c2);
		splitPane.setResizeWeight(resizeWeight);
		return splitPane;
	}

	// TODO mirrg lib
	public static JSplitPane createSplitPaneVertical(Component c1, Component c2)
	{
		return new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, c1, c2);
	}

	// TODO mirrg lib
	public static JSplitPane createSplitPaneVertical(double resizeWeight, Component c1, Component c2)
	{
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, c1, c2);
		splitPane.setResizeWeight(resizeWeight);
		return splitPane;
	}

	// TODO mirrg lib
	private static <T extends JComponent> T setToolTipText(T component, String string)
	{
		component.setToolTipText(string);
		return component;
	}

}
