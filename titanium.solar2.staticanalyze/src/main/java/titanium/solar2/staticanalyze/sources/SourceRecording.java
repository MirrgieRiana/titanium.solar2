package titanium.solar2.staticanalyze.sources;

import static mirrg.lithium.swing.util.HSwing.*;

import java.awt.Component;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;
import javax.swing.JLabel;

import mirrg.lithium.logging.LoggerRelay;
import mirrg.lithium.struct.Struct1;
import titanium.solar2.libs.analyze.Analyzer;
import titanium.solar2.staticanalyze.FiledProperties;
import titanium.solar2.staticanalyze.ISource;

public class SourceRecording implements ISource
{

	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuuMMdd-HHmmss-SSS");

	@SuppressWarnings("unused")
	private FiledProperties p;
	@SuppressWarnings("unused")
	private LoggerRelay logger;

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
			new JLabel("44100Hz, 1ch, 8bit"),
			labelChunk = new JLabel("..."),
			null);
	}

	@Override
	public void setEnabled(boolean enabled)
	{

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

				try {
					//AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, samplesPerSecond, 8, 1, 1, samplesPerSecond, false);
					AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, samplesPerSecond, 16, 1, 2, samplesPerSecond, false);
					TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
					targetDataLine.open(audioFormat);
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
				} catch (LineUnavailableException e) {
					throw new RuntimeException(e);
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
