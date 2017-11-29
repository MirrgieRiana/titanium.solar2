package titanium.solar2.libs.analyze.filters.mountain;

import java.time.LocalDateTime;
import java.util.ArrayList;

import mirrg.lithium.struct.Struct1;
import titanium.solar2.libs.analyze.IFilter;

/**
 * 山クオリティ関数から山をリストアップする
 */
public class FilterExtractMountain implements IFilter
{

	private final double threshold;
	private final int timeout;

	private double[] cache;
	private ArrayList<IMountainListener> mountainListeners = new ArrayList<>();

	private int xInCache = 0;

	private long lastX;

	private long x = 0;

	private long topX = 0;
	private double topY = 0;
	private double maxPrev = 0;

	private LocalDateTime time;
	private long startX;

	public FilterExtractMountain(int width, double threshold, int timeout)
	{
		this.threshold = threshold;
		this.timeout = timeout;

		cache = new double[width];
	}

	public void addMountainListener(IMountainListener mountainListener)
	{
		mountainListeners.add(mountainListener);
	}

	@Override
	public void startChunk(LocalDateTime time)
	{
		this.time = time;
		startX = x;
	}

	@Override
	public void processData(double[] buffer, int length, Struct1<Double> sOffset)
	{
		for (int i = 0; i < length; i++) {

			// 引き出し
			double a = buffer[i];

			// キャッシュ更新
			cache[xInCache] = a;
			xInCache = (xInCache + 1) % cache.length;

			// 直近数個の最大値
			double max = 0;
			for (int j = 0; j < cache.length; j++) {
				if (cache[j] > max) max = cache[j];
			}

			if (max > topY) {
				// 登りつつある
				topX = x;
				topY = a;
			}
			if (maxPrev >= threshold && max < threshold) {
				// 下山した
				//System.out.println("#" + xTop + " " + yTop); // TODO
				Mountain mountain = new Mountain(
					topX - (long) (double) sOffset.x,
					time,
					topX - startX - (long) (double) sOffset.x,
					topY);
				mountainListeners.forEach(l -> l.onMountain(mountain));
				lastX = x;
				topX = 0;
				topY = 0;
			}
			maxPrev = max;

			// 経過イベント
			if (lastX + timeout == x) mountainListeners.forEach(l -> l.onTimeout(x));

			// 代入
			buffer[i] = max;

			x++;
		}
	}

}
