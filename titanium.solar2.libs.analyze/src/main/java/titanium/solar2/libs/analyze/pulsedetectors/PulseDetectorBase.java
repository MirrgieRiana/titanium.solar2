package titanium.solar2.libs.analyze.pulsedetectors;

import java.time.LocalDateTime;

import mirrg.lithium.struct.Struct1;
import titanium.solar2.libs.analyze.DetectorBase;
import titanium.solar2.libs.analyze.IFilter;
import titanium.solar2.libs.analyze.IPulseListener;
import titanium.solar2.libs.analyze.Pulse;

public abstract class PulseDetectorBase extends DetectorBase<IPulseListener> implements IFilter
{

	protected final int timeout;

	protected long x = 0;
	protected LocalDateTime time;
	protected long xStart;
	protected long xLastPulse = 0;

	public PulseDetectorBase(int timeout)
	{
		this.timeout = timeout;
	}

	@Override
	public void preChunk(LocalDateTime time)
	{
		this.time = time;
		xStart = x;
	}

	@Override
	public void processData(double[] buffer, int length, Struct1<Double> sOffset)
	{
		for (int i = 0; i < length; i++) {

			// 引き出し
			double y = buffer[i];

			onSample(y, sOffset.x);

			// 経過イベント
			if (xLastPulse + timeout == x) listeners.forEach(l -> l.onTimeout(x));

			x++;

		}
	}

	protected abstract void onSample(double y, double offset);

	protected void fireOnPulse(Pulse pulse)
	{
		listeners.forEach(l -> l.onItem(pulse));
		xLastPulse = x;
	}

}
