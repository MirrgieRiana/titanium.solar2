package titanium.solar2.libs.record;

import java.util.Optional;

import titanium.solar2.libs.record.plugins.PluginGUI;
import titanium.solar2.libs.time.timerenderers.TimeRendererSimple;

public class SampleGUI
{

	public static void main(String[] args) throws Exception
	{
		Recorder recorder = new Recorder(10, 44100, 8, Optional.empty());
		recorder.addPlugin(new PluginGUI(1, TimeRendererSimple.INSTANCE, true));

		//

		recorder.ready();
		recorder.start();
	}

}
