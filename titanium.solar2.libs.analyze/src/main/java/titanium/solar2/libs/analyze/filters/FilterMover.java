package titanium.solar2.libs.analyze.filters;

import mirrg.lithium.struct.Struct1;
import titanium.solar2.libs.analyze.IFilter;

/**
 * 内部に出力用のレベルを持ち、サンプルに向かって移動させるフィルタ。
 */
public class FilterMover implements IFilter
{

	private final double speedUp;
	private final double speedDown;

	private double now = 0;

	public FilterMover(double speedUp, double speedDown)
	{
		this.speedUp = speedUp;
		this.speedDown = speedDown;
	}

	@Override
	public void processData(double[] buffer, int length, Struct1<Double> sOffset)
	{
		for (int i = 0; i < length; i++) {
			if (now > buffer[i] - speedUp && now < buffer[i] + speedDown) {
				now = buffer[i];
			} else if (now < buffer[i]) {
				now += speedUp;
			} else if (now > buffer[i]) {
				now -= speedDown;
			}
			buffer[i] = now;
		}
	}

}
