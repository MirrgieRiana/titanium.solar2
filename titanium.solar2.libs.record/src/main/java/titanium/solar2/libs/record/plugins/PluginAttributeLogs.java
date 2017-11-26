package titanium.solar2.libs.record.plugins;

import titanium.solar2.libs.record.Recorder;
import titanium.solar2.libs.record.core.IPlugin;
import titanium.solar2.libs.record.core.RecoderEvent;

public class PluginAttributeLogs implements IPlugin
{

	@Override
	public void apply(Recorder recorder)
	{
		recorder.event().register(RecoderEvent.ProcessChunk.Consume.class, e -> {
			e.attributesBuilder.add("Length", "" + e.chunk.length);
			e.attributesBuilder.add(e.chunk.attributes);
			e.attributesBuilder.add(e.chunk.getStatistics().getAttributes());
			e.attributesBuilder.add("Buffers", recorder.buffers.getStringGraph());
		});
	}

	@Override
	public String getName()
	{
		return "attributeLogs";
	}

}
