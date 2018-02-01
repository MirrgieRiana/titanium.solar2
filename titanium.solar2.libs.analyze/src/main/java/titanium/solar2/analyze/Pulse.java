package titanium.solar2.analyze;

import java.time.LocalDateTime;

import titanium.solar2.libs.time.TimeUtil;

public class Pulse
{

	public final long x;
	public final LocalDateTime time;
	public final long xInChunk;
	public final double y;

	public Pulse(long x, LocalDateTime time, long xInChunk, double y)
	{
		this.x = x;
		this.time = time;
		this.xInChunk = xInChunk;
		this.y = y;
	}

	public LocalDateTime getTime(double samplesPerSecond)
	{
		return TimeUtil.getTime(time, xInChunk, samplesPerSecond);
	}

}
