package titanium.solar2.analyze.listeners;

import org.apache.commons.math3.util.FastMath;

import mirrg.lithium.struct.Struct1;
import titanium.solar2.libs.analyze.IFilter;

/**
 * Quality Of Mountain。√(x * a)
 */
public class FilterQOM implements IFilter
{

	private double prevX = 0;
	private double prevPrevX = 0;

	@Override
	public void processData(double[] buffer, int length, Struct1<Double> sOffset)
	{
		sOffset.x += 1;
		for (int i = 0; i < length; i++) {
			double x = buffer[i];
			double dd = (x - prevX) - (prevX - prevPrevX);
			buffer[i] = Math.sqrt(FastMath.max(prevX, 0) * -FastMath.min(dd, 0));
			prevPrevX = prevX;
			prevX = x;
		}
	}

}
