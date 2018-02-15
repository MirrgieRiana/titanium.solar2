package titanium.solar2.staticanalyze.sources;

import static mirrg.lithium.swing.util.HSwing.*;

import java.awt.Component;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import mirrg.lithium.logging.LoggerRelay;
import mirrg.lithium.struct.Struct1;
import mirrg.lithium.swing.util.NamedSlot;
import titanium.solar2.libs.analyze.Analyzer;
import titanium.solar2.staticanalyze.FiledProperties;
import titanium.solar2.staticanalyze.ISource;

public class SourceRecording implements ISource
{

	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuuMMdd-HHmmss-SSS");

	@SuppressWarnings("unused")
	private FiledProperties p;
	private LoggerRelay logger;

	private JComboBox<NamedSlot<Optional<Info>>> comboBoxRecordSource;
	private JButton buttonRefreshRecordSource;

	private JLabel labelChunk;

	public SourceRecording(FiledProperties p, LoggerRelay logger)
	{
		this.p = p;
		this.logger = logger;
	}

	@Override
	public String getTabTitle()
	{
		return "録音";
	}

	@Override
	public Component getComponent(JFrame frame)
	{
		return createBorderPanelUp(
			createBorderPanelLeft(
				new JLabel("録音ソース"),
				createBorderPanelRight(
					process(comboBoxRecordSource = new JComboBox<>(), c -> {
						refreshRecordSource();
					}),
					buttonRefreshRecordSource = createButton("更新", e -> {
						refreshRecordSource();
					}))),
			new JLabel("44100Hz, 1ch, 16bit"),
			setToolTipText(labelChunk = new JLabel("..."), "現在のチャンクの開始時刻です。"),
			null);
	}

	private void refreshRecordSource()
	{
		comboBoxRecordSource.removeAllItems();
		comboBoxRecordSource.addItem(new NamedSlot<>(Optional.empty(), i -> "Default"));
		for (Info info : AudioSystem.getMixerInfo()) {
			comboBoxRecordSource.addItem(new NamedSlot<>(Optional.of(info), i -> i.get().getName()));
		}
	}

	private Optional<Info> getRecordSource()
	{
		return comboBoxRecordSource.getModel().getElementAt(comboBoxRecordSource.getSelectedIndex()).get();
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		comboBoxRecordSource.setEnabled(enabled);
		buttonRefreshRecordSource.setEnabled(enabled);
	}

	private volatile boolean interrupted = false;

	@Override
	public void doAnalyze(Analyzer analyzer) throws IOException, InterruptedException
	{
		Thread thread = new Thread(() -> {
			try {
				int secondsPerChunk = 10;
				int samplesPerSecond = 44100;
				int readsPerChunk = 100;
				int samplesPerRead = samplesPerSecond * secondsPerChunk / readsPerChunk;

				TargetDataLine targetDataLine;
				try {
					//AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, samplesPerSecond, 8, 1, 1, samplesPerSecond, false);
					AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, samplesPerSecond, 16, 1, 2, samplesPerSecond, false);
					Optional<Info> oRecordSource = getRecordSource();
					if (oRecordSource.isPresent()) {
						targetDataLine = AudioSystem.getTargetDataLine(audioFormat, oRecordSource.get());
					} else {
						targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
					}
					targetDataLine.open(audioFormat);
				} catch (LineUnavailableException | IllegalArgumentException e) {
					logger.error(e);
					return;
				}
				try {
					targetDataLine.start();
					try {
						//byte[] bytes = new byte[samplesPerRead];
						byte[] bytes = new byte[samplesPerRead * 2];
						double[] buffer = new double[samplesPerRead];

						analyzer.preAnalyze();
						try {
							while (true) {
								LocalDateTime time = LocalDateTime.now();
								analyzer.preChunk(time);
								labelChunk.setText("Chunk: " + formatter.format(time));
								try {
									for (int i = 0; i < readsPerChunk; i++) {
										int len = targetDataLine.read(bytes, 0, bytes.length);
										for (int j = 0; j < len / 2; j++) {
											//buffer[j] = bytes[j];
											short value = (short) (((bytes[j * 2 + 1] & 0xff) << 8) | (bytes[j * 2] & 0xff));
											buffer[j] = 1.0 * value / 256;
										}
										analyzer.processData(buffer, len / 2, new Struct1<>(0.0));
										if (interrupted) throw new InterruptedException();
									}
								} finally {
									analyzer.postChunk();
								}
							}
						} finally {
							analyzer.postAnalyze();
						}

					} finally {
						targetDataLine.stop();
					}
				} finally {
					targetDataLine.close();
				}

			} catch (InterruptedException e) {

			}
		});
		interrupted = false;
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			interrupted = true;
			thread.join();
		}
	}

}
