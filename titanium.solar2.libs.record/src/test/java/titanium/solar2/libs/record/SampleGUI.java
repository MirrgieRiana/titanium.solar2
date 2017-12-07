package titanium.solar2.libs.record;

import titanium.solar2.libs.record.plugins.PluginGUI;
import titanium.solar2.libs.time.timerenderers.TimeRendererSimple;

public class SampleGUI
{

	public static void main(String[] args) throws Exception
	{
		Recorder recorder = new Recorder(1, 44100, 8);
		recorder.addPlugin(new PluginGUI(1, TimeRendererSimple.INSTANCE, true));

		//

		recorder.ready();
		recorder.start();
	}

}
