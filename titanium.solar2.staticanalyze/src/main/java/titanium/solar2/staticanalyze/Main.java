package titanium.solar2.staticanalyze;

import static mirrg.lithium.swing.util.HSwing.*;

import java.awt.AWTException;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import mirrg.lithium.groovy.properties.URLUtil;
import mirrg.lithium.lang.HFile;
import mirrg.lithium.lang.HLog;
import mirrg.lithium.logging.LoggerPrintStream;
import mirrg.lithium.logging.LoggerRelay;
import mirrg.lithium.logging.LoggerTextPane;
import mirrg.lithium.logging.OutputStreamLogging;
import mirrg.lithium.struct.Struct1;
import mirrg.lithium.swing.util.HSwing;
import titanium.solar2.libs.analyze.Analyzer;
import titanium.solar2.libs.analyze.IFilter;
import titanium.solar2.staticanalyze.sources.filesystem.SourceFileSystem;

public class Main
{

	private static FiledProperties p;
	private static LoggerRelay logger;

	private static final FiledProperty PROPERTY_LOG_FILE = new FiledProperty("log.file", "./staticanalyze.log.txt");

	private static Image imageApplication;

	//

	private static JFrame frame;

	private static ISource sourceFileSystem;

	private static JTextField textFieldSaveFile;
	private static final FiledProperty PROPERTY_SAVE_FILE_PATH = new FiledProperty("saveFile.path", "");
	private static JButton buttonSaveFile;
	private static final FiledProperty PROPERTY_SAVE_FILE_CURRENT_DIRECTORY = new FiledProperty("saveFile.currentDirectory", ".");

	private static final String[] resourceNamesPreset = {
		"staticanalyze://scripts/default.groovy",
		"staticanalyze://scripts/existence.groovy",
		"staticanalyze://scripts/pulseLink.groovy",
		"staticanalyze://scripts/traditional.groovy",
	};
	private static JComboBox<String> comboBoxPresets;
	private static JButton buttonImport;
	private static JButton buttonExport;
	private static final FiledProperty PROPERTY_IMPORT_AND_EXPORT_CURRENT_DIRECTORY = new FiledProperty("importAndExport.currentDirectory", ".");
	private static final FiledProperty PROPERTY_PRESETS = new FiledProperty("presets", "");

	private static JTextField textFieldScriptURL;

	private static RSyntaxTextArea textAreaScript;

	private static JButton buttonValidate;
	private static JFormattedTextField textFieldSamplesPerSecond;
	private static JButton buttonAnalyze;
	private static JButton buttonInterrupt;

	private static JLabel labelAnalyzeTime;
	private static JLabel labelChunkTime;

	private static PanelWaveform panelWaveform;
	private static JCheckBox checkBoxPaintGraph;
	private static double xZoom;

	private static JLabel labelOutput;
	private static LoggerTextPane loggerTextPaneOutput;
	private static int outputLineCount;

	private static TrayIcon trayIcon;

	public static void main(String[] args)
	{
		// Swing設定
		HSwing.setWindowsLookAndFeel();
		ToolTipManager.sharedInstance().setInitialDelay(500);
		ToolTipManager.sharedInstance().setDismissDelay(60000);

		p = new FiledProperties(new File("./staticanalyze.properties"));
		p.init();

		// ロガー設定
		logger = new LoggerRelay();

		// ロガー登録
		logger.addLogger(new LoggerPrintStream(System.out));
		try {
			PrintStream out = new PrintStream(new FileOutputStream(new File(p.get(PROPERTY_LOG_FILE))));
			logger.addLogger(new LoggerPrintStream(out));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		// アプリケーションイメージの取得
		try {
			imageApplication = ImageIO.read(Main.class.getResource("icon.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// ウィンドウ生成
		frame = new JFrame("Static Analyzer - titanium.solar2");
		frame.setIconImage(imageApplication);
		frame.setLayout(new CardLayout());
		frame.setJMenuBar(createJMenuBar(
			createJMenu("設定",
				setToolTipText(createJMenuItem("プリセットの初期化", e -> {
					p.reset(PROPERTY_PRESETS);
					updatePresets();
				}), "解析スクリプトのプリセット欄を初期化し、デフォルトの解析スクリプトを読み込みます。")),
			createJMenu("ヘルプ",
				createJMenuItem("ヒント", e -> {
					JOptionPane.showMessageDialog(frame, "GUIコンポーネントにカーソルを合わせるとヒントが表示されます。");
				}),
				createJMenuItem("更新履歴", e -> {
					JOptionPane.showMessageDialog(frame, "<html>"
						+ "<h3>0.0.1</h3>"
						+ "・最初のリリース"
						+ "<h3>0.0.2</h3>"
						+ "・解析スクリプト入力欄を豪華（RSyntaxTextArea）に<br>"
						+ "・解析スクリプト入力欄にファイルをドロップするとインポートするように<br>"
						+ "・<b>【破壊的】解析スクリプトのデフォルトインポートを変更</b><br>"
						+ "・解析スクリプトの組み込み変数context追加<br>"
						+ "・更新履歴欄の追加"
						+ "<h3>0.0.3</h3>"
						+ "・解析スクリプト入力欄でコピー・ペーストできない不具合を修正<br>"
						+ "・ファイルのドロップ機能をインポートボタンに移動<br>"
						+ "・グラフのX軸方向の縮尺変更追加<br>"
						+ "・タスクトレイアイコン追加<br>"
						+ "・処理完了時、ウィンドウにフォーカスがなければ通知を表示<br>"
						+ "・現在処理中のファイルとエントリーをウィンドウ上に表示<br>"
						+ "・ログ出力欄の最下部に空行が表示されないように<br>"
						+ "・解析スクリプトでrendererをデフォルトインポート"
						+ "<h3>0.0.4</h3>"
						+ "・<b>【破壊的】解析スクリプトでprocessをdetectorに変更</b><br>"
						+ "・<b>【破壊的】解析スクリプトでcontext.getLogger()をloggerに変更</b><br>"
						+ "・<b>【破壊的】解析スクリプトでのリソースの指定をスクリプトファイルからの相対参照に変更</b><br>"
						+ "・<b>【破壊的】ビルトインスクリプトファイルの参照文字列の変更</b><br>"
						+ "・<b>【破壊的】プリセットをpropertiesではなく内部的に与えるように変更</b><br>"
						+ "　・propertiesの初期化が必要<br>"
						+ "・<b>【破壊的】WaveformUtilsをWaveformUtilに変更</b><br>"
						+ "・WaveformUtil.fromCSVで\"#\"によるコメントアウトが可能に<br>"
						+ "・プリセットのプロトコル名変更<br>"
						+ "・解析スクリプト履歴の初期化機能の追加<br>"
						+ "・解析スクリプトURLの指定欄の追加"
						+ "");
				}))));
		{
			Component mainPane = createBorderPanelUp(
				(sourceFileSystem = new SourceFileSystem(p, logger, frame)).getComponent(),
				createBorderPanelLeft(
					new JLabel("出力ファイル"),
					createBorderPanelRight(
						process(setToolTipText(textFieldSaveFile = new JTextField(), "<html>"
							+ "解析内容の出力先ファイルです。"), c -> {
								c.setText(p.get(PROPERTY_SAVE_FILE_PATH));
								c.addFocusListener(new FocusAdapter() {
									@Override
									public void focusLost(FocusEvent e)
									{
										p.set(PROPERTY_SAVE_FILE_PATH, c.getText());
									}
								});
							}),
						buttonSaveFile = createButton("参照", e -> {
							JFileChooser fileChooser = new JFileChooser();
							fileChooser.setCurrentDirectory(new File(p.get(PROPERTY_SAVE_FILE_CURRENT_DIRECTORY)));
							fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
							if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
								p.set(PROPERTY_SAVE_FILE_PATH, fileChooser.getSelectedFile().getAbsolutePath());
								p.set(PROPERTY_SAVE_FILE_CURRENT_DIRECTORY, fileChooser.getCurrentDirectory().getAbsolutePath());
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
									if (preset != null) {

										String source;
										try {
											source = URLUtil.getString(AnalyzerFactoryStatic.RESOURCE_RESOLVER.getResource(preset));
										} catch (Exception e2) {
											logger.error("Failed to load preset: " + preset);
											logger.error(e2);
											return;
										}

										textFieldScriptURL.setText(preset);

										textAreaScript.setText(source);
										textAreaScript.setCaretPosition(0);
									}
								});
							}),
						setToolTipText(process(buttonImport = createButton("インポート", e -> {
							try {
								JFileChooser fileChooser = new JFileChooser();
								fileChooser.setCurrentDirectory(new File(p.get(PROPERTY_IMPORT_AND_EXPORT_CURRENT_DIRECTORY)));
								fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
								fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("titanium.solar2解析スクリプトファイル(*.groovy)", "groovy"));
								if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
									p.set(PROPERTY_IMPORT_AND_EXPORT_CURRENT_DIRECTORY, fileChooser.getCurrentDirectory().getAbsolutePath());
									addPreset(fileChooser.getSelectedFile().toURI().toURL().toString(), true);
								}
							} catch (Exception e2) {
								logger.error(e2);
								return;
							}
						}), c -> {
							c.setTransferHandler(new TransferHandler() {
								@Override
								public boolean canImport(TransferSupport transferSupport)
								{
									if (transferSupport.isDrop()) {
										if (transferSupport.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
											return true;
										}
									}
									return false;
								}

								@Override
								public boolean importData(TransferSupport transferSupport)
								{
									if (canImport(transferSupport)) {
										try {
											@SuppressWarnings("unchecked")
											List<File> files = (List<File>) transferSupport.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
											if (files.size() == 1) {
												addPreset(files.get(0).toURI().toURL().toString(), true);
												return true;
											}
										} catch (Exception e) {
											logger.error(e);
										}
										return true;
									}
									return false;
								}
							});
						}), "<html>"
							+ "ファイルから解析スクリプトを読み込みます。<br>"
							+ "このボタンにはファイルをドロップできます。"),
						setToolTipText(buttonExport = createButton("エクスポート", e -> {
							try {
								JFileChooser fileChooser = new JFileChooser();
								fileChooser.setCurrentDirectory(new File(p.get(PROPERTY_IMPORT_AND_EXPORT_CURRENT_DIRECTORY)));
								fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
								fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("titanium.solar2解析スクリプトファイル(*.groovy)", "groovy"));
								if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
									p.set(PROPERTY_IMPORT_AND_EXPORT_CURRENT_DIRECTORY, fileChooser.getCurrentDirectory().getAbsolutePath());
									try (PrintStream out = new PrintStream(new FileOutputStream(fileChooser.getSelectedFile()))) {
										out.print(textAreaScript.getText());
									}
									String preset = fileChooser.getSelectedFile().toURI().toURL().toString();
									addPreset(preset, false);
									textFieldScriptURL.setText(preset);
								}
							} catch (Exception e2) {
								logger.error(e2);
								return;
							}
						}), "解析スクリプトをファイルに保存します。"))),
				createBorderPanelLeft(
					new JLabel("解析スクリプトURL"),
					setToolTipText(textFieldScriptURL = new JTextField(), "解析スクリプト内での相対パスの基底です。")),
				createBorderPanelDown(
					createScrollPane(setToolTipText(process(textAreaScript = new RSyntaxTextArea(), c -> {
						c.setDocument(new RSyntaxDocument(SyntaxConstants.SYNTAX_STYLE_GROOVY));
						c.setFont(new Font(Font.MONOSPACED, Font.PLAIN, c.getFont().getSize()));
						c.setTextAntiAliasHint("VALUE_TEXT_ANTIALIAS_ON");
						c.setCurrentLineHighlightColor(Color.decode("#eeeeff"));
						c.setAnimateBracketMatching(false);
						c.setBackground(Color.decode("#ffffee"));
						c.setTabSize(4);
					}), "<html>"
						+ "解析スクリプトの入力欄です。Groovyスクリプトとして実行されます。<br>"
						+ "スクリプトは非nullのAnalyzerを戻り値として持たなければなりません。<br>"
						+ "以下の組み込み変数が利用できます。<br>"
						+ "<br>"
						+ "・context - 外部ファイルの読み込みなどを行うオブジェクトです。<br>"
						+ "　・context.getResourceAsURL(リソース名) - リソースをURLで取得します。<br>"
						+ "　　・存在しない場合は例外になります。<br>"
						+ "　　・リソース名の指定方法<br>"
						+ "　　　・「assets://」で始まる場合：ビルトインのファイルを参照<br>"
						+ "　　　・「プロトコル://」で始まる場合：URLとして解釈<br>"
						+ "　　　・それ以外の場合：スクリプトファイルからの相対参照<br>"
						+ "　・context.getResourceAsString(リソース名) - リソースを文字列で取得します。<br>"
						+ "・logger - ログ出力のためのオブジェクトです。<br>"
						+ "・samplesPerSecond - フォーム上で指定されたサンプリングレートです。<br>"
						+ "・out - 解析結果を出力するためのPrintStreamです。<br>"
						+ "・filterExtenstion - グラフの表示などを行う拡張フィルタです。<br>"
						+ "<br>"
						+ "この入力欄の上側のインポートボタンにはファイルをロドップできます。"), 200, 200),
					createBorderPanelLeft(
						setToolTipText(buttonValidate = createButton("スクリプトの検証", e -> {
							Analyzer analyzer;
							try {
								analyzer = createAnalyzer(new OutputStreamLogging(logger));
							} catch (Exception e2) {
								logger.info(e2);
								return;
							}
							if (analyzer == null) {
								logger.info(new NullPointerException("analyzer"));
								return;
							}
							logger.info("Successfully Compiled: " + analyzer);
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
								});
								c.setPreferredSize(new Dimension(32, 64));
								c.setMajorTickSpacing(20);
								c.setPaintTicks(true);
							}),
						process(setToolTipText(new JSlider(JSlider.VERTICAL, -100, 100, 0), "<html>"
							+ "グラフの横方向の縮尺を変更します。<br>"
							+ "1目盛りにつき表示の縮尺が10倍になります。"), c -> {
								c.addChangeListener(e -> {
									xZoom = Math.pow(10, 1.0 * c.getValue() / 20);
								});
								xZoom = 1;
								c.setPreferredSize(new Dimension(32, 64));
								c.setMajorTickSpacing(20);
								c.setPaintTicks(true);
							}),
						createBorderPanelRight(
							setToolTipText(process(panelWaveform = new PanelWaveform(), c -> {
								c.setMinimumSize(new Dimension(0, 64));
								c.setPreferredSize(new Dimension(200, 64));
							}), "標準の拡大率では1ピクセルにつき100ms分のサンプルが含まれます。"),
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
		{
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowOpened(WindowEvent e)
				{
					trayIcon = new TrayIcon(imageApplication, "Static Analyzer - titanium.solar2");
					trayIcon.setImageAutoSize(true);
					trayIcon.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(java.awt.event.MouseEvent e)
						{
							frame.toFront();
						};
					});
					try {
						SystemTray.getSystemTray().add(trayIcon);
					} catch (AWTException e1) {
						logger.error(e1);
					}
				}

				@Override
				public void windowClosed(WindowEvent e)
				{
					SystemTray.getSystemTray().remove(trayIcon);
				}
			});
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
		// 既存
		{
			String[] presets = p.get(PROPERTY_PRESETS).split(";");
			for (int i = 0; i < presets.length; i++) {
				if (presets[i].equals(preset)) {
					comboBoxPresets.setSelectedIndex(i);
					return;
				}
			}
		}

		// 新規追加
		String presets = p.get(PROPERTY_PRESETS);
		if (!presets.isEmpty()) presets = presets + ";";
		p.set(PROPERTY_PRESETS, presets + ";" + preset);
		comboBoxPresets.addItem(preset);
		if (changePreset) {
			comboBoxPresets.setSelectedIndex(comboBoxPresets.getItemCount() - 1);
		}
	}

	private static void updatePresets()
	{
		comboBoxPresets.removeAllItems();
		for (String resourceName : resourceNamesPreset) {
			comboBoxPresets.addItem(resourceName);
		}
		for (String string : p.get(PROPERTY_PRESETS).split(";")) {
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
				logger.error("前回の解析が終わっていません。");
				return;
			}

			// 解析状態にする

			thread = new Thread(() -> {
				try {
					try (PrintStream out1 = new PrintStream(HFile.getOutputStreamAndMkdirs(new File(textFieldSaveFile.getText())));
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

						logger.info("Analyze Start");

						Analyzer analyzer;
						try {
							analyzer = createAnalyzer(out3);
						} catch (Exception e) {
							logger.error(e);
							return;
						}

						try {
							sourceFileSystem.doAnalyze(analyzer);
						} catch (InterruptedException e) {
							logger.warn("Analyze Interrupted");
							return;
						} catch (Exception e) {
							logger.error(e);
							return;
						}

						logger.info("Analyze Finished");
					} catch (IOException e1) {
						logger.error(HLog.getStackTrace(e1));
					}
				} finally {
					synchronized (lock) {
						timer.stop();

						thread = null;
						sourceFileSystem.setEnabled(true);
						textFieldSaveFile.setEnabled(true);
						buttonSaveFile.setEnabled(true);
						comboBoxPresets.setEnabled(true);
						buttonImport.setEnabled(true);
						buttonExport.setEnabled(true);
						textFieldScriptURL.setEnabled(true);
						textAreaScript.setEnabled(true);
						buttonValidate.setEnabled(true);
						textFieldSamplesPerSecond.setEnabled(true);
						buttonAnalyze.setEnabled(true);
						buttonInterrupt.setEnabled(false);

						if (!frame.isActive()) {
							trayIcon.displayMessage("解析完了", "解析が完了しました。", MessageType.INFO);
						}
					}
				}
			});
			sourceFileSystem.setEnabled(false);
			textFieldSaveFile.setEnabled(false);
			buttonSaveFile.setEnabled(false);
			comboBoxPresets.setEnabled(false);
			buttonImport.setEnabled(false);
			buttonExport.setEnabled(false);
			textFieldScriptURL.setEnabled(false);
			textAreaScript.setEnabled(false);
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
		return AnalyzerFactoryStatic.createAnalyzer(
			textAreaScript.getText(),
			AnalyzerFactoryStatic.RESOURCE_RESOLVER.getResource(textFieldScriptURL.getText()),
			logger,
			getSamplesPerSecond(),
			out,
			new FilterStaticAnalyzeGUIExtension());
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
					int samplesPerPixel = getSamplesPerSecond() / 10;
					if (samples >= (int) (samplesPerPixel / xZoom) - 1) {
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

	private static void interrupt()
	{
		synchronized (lock) {
			if (thread == null) {
				logger.error("解析が実行中ではありません。");
				return;
			}

			// 解析を終わらせる。

			thread.interrupt();
		}
	}

}
