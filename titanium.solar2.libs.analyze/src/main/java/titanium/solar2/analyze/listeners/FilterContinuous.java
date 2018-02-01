package titanium.solar2.analyze.listeners;

import mirrg.lithium.struct.Struct1;
import titanium.solar2.libs.analyze.IFilter;

/**
 * 孤立パルス除去：前後指定オフセットにパルスがある場合にのみ通すフィルタ。
 */
public class FilterContinuous implements IFilter
{

	private final int offsetShort;
	private final int offsetLong;

	private double[] cache;
	private int index = 0;

	public FilterContinuous(int offsetShort, int offsetLong)
	{
		this.offsetShort = offsetShort;
		this.offsetLong = offsetLong;

		cache = new double[2 * offsetLong + 1];
	}

	@Override
	public void processData(double[] buffer, int length, Struct1<Double> sOffset)
	{
		sOffset.x += offsetLong;
		for (int i = 0; i < length; i++) {

			cache[index] = buffer[i];

			// long = 3
			// -..0..+
			// 6543210
			int center = index + cache.length - offsetLong;
			double pp = cache[(center - offsetLong) % cache.length];
			double p = cache[(center - offsetShort) % cache.length];
			double c = cache[(center) % cache.length];
			double n = cache[(center + offsetShort) % cache.length];
			double nn = cache[(center + offsetLong) % cache.length];
			buffer[i] = Math.min(c, Math.max(Math.max(pp, p), Math.max(n, nn)));

			index = (index + 1) % cache.length;

		}
	}

}
