package titanium.solar2.libs.time;

import java.time.LocalDateTime;

public class TimeUtil
{

	public static LocalDateTime getTime(LocalDateTime baseTime, long sample, double samplesPerSecond)
	{
		return baseTime.plusNanos((long) (sample / samplesPerSecond * 1000 * 1000 * 1000));
	}

}
