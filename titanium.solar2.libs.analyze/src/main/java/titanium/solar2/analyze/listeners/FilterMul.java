package titanium.solar2.analyze.listeners;

import mirrg.lithium.struct.Struct1;
import titanium.solar2.libs.analyze.IFilter;

/**
 * 乗算フィルタ。
 */
public class FilterMul implements IFilter
{

	private final double x;

	public FilterMul(double x)
	{
		this.x = x;
	}

	@Override
	public void processData(double[] buffer, int length, Struct1<Double> sOffset)
	{
		for (int i = 0; i < length; i++) {
			buffer[i] *= x;
		}
	}

}
