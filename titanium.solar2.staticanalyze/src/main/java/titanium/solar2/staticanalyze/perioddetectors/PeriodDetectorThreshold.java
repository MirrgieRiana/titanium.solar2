package titanium.solar2.staticanalyze.perioddetectors;

import java.time.LocalDateTime;

import mirrg.lithium.struct.Struct1;
import titanium.solar2.libs.analyze.IFilter;
import titanium.solar2.libs.analyze.ItemDetectorBase;
import titanium.solar2.staticanalyze.Period;

public class PeriodDetectorThreshold extends ItemDetectorBase<Period> implements IFilter
{

	private final double threshold;

	private boolean prevOver = false;
	private long x = 0;
	private long xChunk;
	private LocalDateTime time;
	private long xStart;

	public PeriodDetectorThreshold(double threshold)
	{
		this.threshold = threshold;
	}

	@Override
	public void preChunk(LocalDateTime time)
	{
		xChunk = x;
		this.time = time;
	}

	@Override
	public void processData(double[] buffer, int length, Struct1<Double> sOffset)
	{
		for (int i = 0; i < length; i++) {
			boolean over = buffer[i] >= threshold;

			if (!prevOver && over) {
				// 立ち上がり
				xStart = x;
			}

			if (prevOver && !over) {
				// 立ち下がり
				fireOnPacket(new Period(xStart, x, time, xStart - xChunk, x - xChunk));
			}

			x++;
			prevOver = over;
		}
	}

}
