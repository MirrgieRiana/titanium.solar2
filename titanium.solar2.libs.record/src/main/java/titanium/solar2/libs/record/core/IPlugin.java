package titanium.solar2.libs.record.core;

import titanium.solar2.libs.record.Recorder;

public interface IPlugin
{

	public void apply(Recorder recorder);

	public String getName();

}
