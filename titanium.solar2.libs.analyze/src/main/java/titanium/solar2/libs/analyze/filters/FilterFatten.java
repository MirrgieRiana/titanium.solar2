package titanium.solar2.libs.analyze.filters;

import mirrg.lithium.struct.Struct1;
import titanium.solar2.libs.analyze.IFilter;

/**
 * 直近指定個数サンプルのうちの最大値を得るフィルタ。
 */
public class FilterFatten implements IFilter
{

	private final double[] cache;

	private int indexInCache = 0;

	public FilterFatten(int width)
	{
		this.cache = new double[width];
	}

	@Override
	public void processData(double[] buffer, int length, Struct1<Double> sOffset)
	{
		for (int i = 0; i < length; i++) {

			// キャッシュに入れる
			cache[indexInCache] = buffer[i];
			indexInCache = (indexInCache + 1) % cache.length;

			// 直近指定個数の最大値を出力
			buffer[i] = getMax();

		}
	}

	private double getMax()
	{
		double max = cache[0];
		for (int j = 1; j < cache.length; j++) {
			if (max < cache[j]) max = cache[j];
		}
		return max;
	}

}
