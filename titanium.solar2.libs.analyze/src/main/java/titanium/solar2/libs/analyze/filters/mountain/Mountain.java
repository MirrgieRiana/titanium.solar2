package titanium.solar2.libs.analyze.filters.mountain;

import java.time.LocalDateTime;

public class Mountain
{

	public final long x;
	public final LocalDateTime time;
	public final long xInChunk;
	public final double y;

	public Mountain(long x, LocalDateTime time, long xInChunk, double y)
	{
		this.x = x;
		this.time = time;
		this.xInChunk = xInChunk;
		this.y = y;
	}

	public LocalDateTime getTime(double samplesPerSecond)
	{
		return time.plusNanos((long) (xInChunk / samplesPerSecond * 1000 * 1000 * 1000));
	}

}
