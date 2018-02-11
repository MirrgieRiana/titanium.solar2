package titanium.solar2.analyze.renderers;

import titanium.solar2.analyze.Period;
import titanium.solar2.libs.time.ITimeRenderer;
import titanium.solar2.libs.time.TimeUtil;

public class PeriodRenderer
{

	public static String toString(Period period, ITimeRenderer timeRenderer, double samplesPerSecond)
	{
		return String.format("%s,%d",
			timeRenderer.format(TimeUtil.getTime(period.chunkTime, period.xInChunkBegin, samplesPerSecond)),
			period.getLength());
	}

}
