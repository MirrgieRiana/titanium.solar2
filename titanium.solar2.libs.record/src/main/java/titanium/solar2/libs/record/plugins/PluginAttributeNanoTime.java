package titanium.solar2.libs.record.plugins;

import titanium.solar2.libs.record.Recorder;
import titanium.solar2.libs.record.core.IPlugin;
import titanium.solar2.libs.record.core.RecoderEvent;

public class PluginAttributeNanoTime implements IPlugin
{

	private long startNanoTime;
	private long lastNanoTime;
	private long samples;

	@Override
	public void apply(Recorder recorder)
	{
		recorder.event().register(RecoderEvent.Start.class, e -> {
			startNanoTime = System.nanoTime();
			lastNanoTime = System.nanoTime();
		});
		recorder.event().register(RecoderEvent.ProcessChunk.Consume.class, e -> {
			long nowNanoTime = System.nanoTime();

			samples += e.chunk.length;

			double secondsFromStart = (nowNanoTime - startNanoTime) * 1e-9;
			e.attributesBuilder.add("SecondsFromStart", "%.2f", secondsFromStart);
			e.attributesBuilder.add("SecondsFromLast", "%.2f", (nowNanoTime - lastNanoTime) * 1e-9);

			double secondsSamples = (double) samples / recorder.samplesPerSecond;
			e.attributesBuilder.add("SampleLost", "%.2f%%", (1 - secondsSamples / secondsFromStart) * 100);

			lastNanoTime = nowNanoTime;
		});
	}

	@Override
	public String getName()
	{
		return "attributeNanoTime";
	}

}
