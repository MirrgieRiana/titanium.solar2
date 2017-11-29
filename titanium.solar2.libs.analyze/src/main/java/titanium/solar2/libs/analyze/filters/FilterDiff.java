package titanium.solar2.libs.analyze.filters;

import mirrg.lithium.struct.Struct1;
import titanium.solar2.libs.analyze.IFilter;

public class FilterDiff implements IFilter
{

	private double prevX = 0;

	@Override
	public void processData(double[] buffer, int length, Struct1<Double> sOffset)
	{
		sOffset.x += 1;
		for (int i = 0; i < length; i++) {
			double x = buffer[i];
			buffer[i] = x - prevX;
			prevX = x;
		}
	}

}
