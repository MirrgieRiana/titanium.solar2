package titanium.solar2.libs.analyze.renderer;

import titanium.solar2.libs.analyze.filters.mountain.Mountain;

public class MountainRenderer
{

	public static String toString(Mountain mountain)
	{
		return String.format("%8d: %s", mountain.x, mountain.y);
	}

}
