package titanium.solar2.libs.record.core;

import java.time.LocalDateTime;

public class Chunk
{

	public Buffer buffer;
	public int length;
	public Attributes attributes;
	public LocalDateTime time;

	public Chunk(Buffer buffer, int length, Attributes attributes, LocalDateTime time)
	{
		this.buffer = buffer;
		this.length = length;
		this.attributes = attributes;
		this.time = time;
	}

	private ChunkStatistics chunkStatistics;

	public ChunkStatistics getStatistics()
	{
		if (chunkStatistics == null) chunkStatistics = new ChunkStatistics(this);
		return chunkStatistics;
	}

}
