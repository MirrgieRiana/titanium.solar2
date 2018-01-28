package titanium.solar2.libs.analyze.filters;

import mirrg.lithium.struct.Struct1;
import titanium.solar2.libs.analyze.IFilter;

/**
 * 積分する。
 */
public class FilterIntegral implements IFilter
{

	private final double alpha;

	private double prev = 0;

	/**
	 * @param alpha
	 *            減衰率
	 */
	public FilterIntegral(double alpha)
	{
		this.alpha = alpha;
	}

	@Override
	public void processData(double[] buffer, int length, Struct1<Double> sOffset)
	{
		for (int i = 0; i < length; i++) {
			prev = prev * alpha + buffer[i];
			buffer[i] = prev;
		}
	}

}
