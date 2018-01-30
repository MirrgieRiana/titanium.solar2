package titanium.solar2.staticanalyze;

import java.time.LocalDateTime;

public class Period
{

	public final long xBegin;
	public final long xEnd;
	public final LocalDateTime chunkTime;
	public final long xInChunkBegin;
	public final long xInChunkEnd;

	public Period(long xBegin, long xEnd, LocalDateTime time, long xInChunkBegin, long xInChunkEnd)
	{
		this.xBegin = xBegin;
		this.xEnd = xEnd;
		this.chunkTime = time;
		this.xInChunkBegin = xInChunkBegin;
		this.xInChunkEnd = xInChunkEnd;
	}

	public long getLength()
	{
		return xEnd - xBegin;
	}

}
