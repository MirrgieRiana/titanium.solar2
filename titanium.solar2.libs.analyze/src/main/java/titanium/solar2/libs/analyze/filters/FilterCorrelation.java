package titanium.solar2.libs.analyze.filters;

import mirrg.lithium.struct.Struct1;
import titanium.solar2.libs.analyze.IFilter;

/**
 * 相関関数を求めるフィルタ。
 */
public class FilterCorrelation implements IFilter
{

	private final double[] waveform;
	private final int hotspot;

	private double[] cache;
	private int index = 0;

	public FilterCorrelation(double[] waveform, int hotspot)
	{
		this.waveform = waveform;
		this.hotspot = hotspot;

		cache = new double[waveform.length];
	}

	@Override
	public void processData(double[] buffer, int length, Struct1<Double> sOffset)
	{
		sOffset.x += (waveform.length - 1) - hotspot;
		for (int i = 0; i < length; i++) {

			// キャッシュに入れる
			cache[index] = buffer[i];

			// キャッシュとサンプルデータの相関を計算
			long sum = 0;
			for (int j = 0; j < waveform.length; j++) {
				sum += waveform[j] * cache[(index + 1 + j) % waveform.length];
			}

			// バッファに戻す
			buffer[i] = sum;

			// キャッシュの使用位置更新
			index = (index + 1) % waveform.length;

		}

	}

}
