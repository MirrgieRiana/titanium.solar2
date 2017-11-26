package titanium.solar2.libs.record.core;

import titanium.solar2.libs.record.Recorder;
import titanium.solar2.libs.record.renderer.AttributesRenderer;
import titanium.solar2.libs.record.util.AttributesBuilder;

public class ThreadProcessor extends Thread
{

	private Recorder recorder;

	public ThreadProcessor(Recorder recorder)
	{
		this.recorder = recorder;
	}

	@Override
	public void run()
	{
		while (true) {
			recorder.chunkStorage.dispatch(c -> {
				recorder.event().post(new RecoderEvent.ProcessChunk.Pre(c));

				AttributesBuilder attributesBuilder = new AttributesBuilder();
				recorder.event().post(new RecoderEvent.ProcessChunk.Consume(c, attributesBuilder));

				// 表示
				String string = AttributesRenderer.getString(attributesBuilder.get());
				if (!string.isEmpty()) System.out.println("[Entry] " + string);
				if (c.getStatistics().noiz > 1000) System.err.println("Abnormal data!");

				recorder.event().post(new RecoderEvent.ProcessChunk.Post(c));
			});
		}
	}

}
