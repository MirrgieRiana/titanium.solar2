package titanium.solar2.libs.analyze.filters;

import java.util.function.DoubleUnaryOperator;

import mirrg.lithium.struct.Struct1;
import titanium.solar2.libs.analyze.IFilter;

/**
 * {@link DoubleUnaryOperator} を受け取り各サンプルに適用するフィルタ。
 */
public class FilterFunction implements IFilter
{

	private final DoubleUnaryOperator function;

	public FilterFunction(DoubleUnaryOperator function)
	{
		this.function = function;
	}

	@Override
	public void processData(double[] buffer, int length, Struct1<Double> sOffset)
	{
		for (int i = 0; i < length; i++) {
			buffer[i] = function.applyAsDouble(buffer[i]);
		}
	}

}
