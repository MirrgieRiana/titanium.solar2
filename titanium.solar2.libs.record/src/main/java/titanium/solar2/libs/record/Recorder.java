package titanium.solar2.libs.record;

import java.util.Optional;
import java.util.stream.Stream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import mirrg.lithium.event.EventManager;
import mirrg.lithium.event.IEventProvider;
import titanium.solar2.libs.record.core.Buffers;
import titanium.solar2.libs.record.core.ChunkStorage;
import titanium.solar2.libs.record.core.IPlugin;
import titanium.solar2.libs.record.core.RecoderEvent;
import titanium.solar2.libs.record.core.ThreadProcessor;
import titanium.solar2.libs.record.core.ThreadRecorder;

public class Recorder implements IEventProvider<RecoderEvent>
{

	public final double secondsPerChunk;
	public final int samplesPerSecond;
	public final int bitsPerSample;
	public final int channels;
	public final int samplesPerFrame;

	public final int bytesPerSample;
	public final int bytesPerChunk;

	public final AudioFormat audioFormat;
	public final Buffers buffers;
	public final ChunkStorage chunkStorage;
	private Optional<String> oInfoName;

	public TargetDataLine targetDataLine;

	public Recorder(double secondsPerChunk, int samplesPerSecond, int bitsPerSample, Optional<String> oInfoName) throws Exception
	{
		this.secondsPerChunk = secondsPerChunk;
		this.samplesPerSecond = samplesPerSecond;
		this.bitsPerSample = bitsPerSample;
		this.channels = 1;
		this.samplesPerFrame = 1;

		this.bytesPerSample = channels * (bitsPerSample / 8);
		this.bytesPerChunk = (int) (bytesPerSample * samplesPerSecond * secondsPerChunk);

		this.audioFormat = new AudioFormat(
			AudioFormat.Encoding.PCM_SIGNED,
			samplesPerSecond,
			bitsPerSample,
			channels,
			bytesPerSample * samplesPerFrame,
			samplesPerSecond / samplesPerFrame,
			false);
		this.buffers = new Buffers(bytesPerChunk);
		this.chunkStorage = new ChunkStorage();
		this.oInfoName = oInfoName;
	}

	public void ready() throws Exception
	{
		targetDataLine = createTargetDataLine();
		targetDataLine.open(audioFormat);

		System.err.println(String.format("ChunkSize:%sseconds;Sampling:%sHz %sbit %schannels",
			secondsPerChunk,
			samplesPerSecond,
			bitsPerSample,
			channels));

		event().post(new RecoderEvent.Ready());
	}

	protected TargetDataLine createTargetDataLine() throws LineUnavailableException
	{
		if (oInfoName.isPresent()) {
			return AudioSystem.getTargetDataLine(audioFormat, Stream.of(AudioSystem.getMixerInfo())
				.filter(i -> i.toString().contains(oInfoName.get()))
				.findFirst()
				.get());
		} else {
			return AudioSystem.getTargetDataLine(audioFormat);
		}
	}

	public void start()
	{
		targetDataLine.start();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			event().post(new RecoderEvent.Destroy());
		}));
		event().post(new RecoderEvent.Start());

		//

		// 音声データをひたすら読み取る専門のスレッド
		new ThreadRecorder(this).start();

		// 音声データを処理するスレッド
		new ThreadProcessor(this).start();

	}

	//

	public void addPlugin(IPlugin plugin)
	{
		plugin.apply(this);
	}

	//

	private EventManager<RecoderEvent> eventManager = new EventManager<>();

	@Override
	public EventManager<RecoderEvent> event()
	{
		return eventManager;
	}

}
