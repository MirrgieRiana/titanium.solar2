package titanium.solar2.libs.record.plugins;

import titanium.solar2.libs.record.Recorder;
import titanium.solar2.libs.record.core.IPlugin;
import titanium.solar2.libs.record.core.RecoderEvent;
import titanium.solar2.libs.record.renderer.ChunkRendererStringGraph;

public class PluginPrintStringGraph implements IPlugin
{

	public final int length;
	public final double zoom;

	public PluginPrintStringGraph(int length, double zoom)
	{
		this.length = length;
		this.zoom = zoom;
	}

	@Override
	public void apply(Recorder recorder)
	{
		recorder.event().register(RecoderEvent.ProcessChunk.Post.class, e -> {
			System.out.println(ChunkRendererStringGraph.getStringGraph(e.chunk, length, zoom));
		});
	}

	@Override
	public String getName()
	{
		return "printStringGraph";
	}

}
