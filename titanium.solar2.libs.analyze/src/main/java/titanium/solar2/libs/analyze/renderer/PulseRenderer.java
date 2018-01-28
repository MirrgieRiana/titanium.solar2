package titanium.solar2.libs.analyze.renderer;

import titanium.solar2.libs.analyze.Pulse;

public class PulseRenderer
{

	public static String toString(Pulse pulse)
	{
		return String.format("%8d: %s", pulse.x, pulse.y);
	}

}
