package titanium.solar2.libs.record.core;

import titanium.solar2.libs.record.util.AttributesBuilder;

public class ChunkStatistics
{

	public int min;
	public int max;
	public long variance;
	public long noiz;

	public ChunkStatistics(Chunk entry)
	{
		min = 255;
		max = -255;
		variance = 0;
		noiz = 0;

		int last = 0;
		for (int i = 0; i < entry.length; i++) {
			int v = entry.buffer.array[i];

			if (min > v) min = v;
			if (max < v) max = v;
			variance += v * v;
			if (Math.abs(v - last) > 64) noiz++;

			last = v;
		}

		variance /= entry.length;
	}

	public Attributes getAttributes()
	{
		return new AttributesBuilder()
			.add("Min", "" + min)
			.add("Max", "" + max)
			.add("Variance", "" + variance)
			.add("Noiz", "" + noiz)
			.get();
	}

}
