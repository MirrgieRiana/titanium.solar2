package titanium.solar2.analyze.renderers;

import titanium.solar2.analyze.Pulse;

public class PulseRenderer
{

	public static String toString(Pulse pulse)
	{
		return String.format("%8d: %s", pulse.x, pulse.y);
	}

}
