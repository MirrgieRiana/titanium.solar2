package titanium.solar2.analyze.listeners;

import titanium.solar2.libs.analyze.Pulse;

/**
 * 閾値を超えたときに出力。Yは閾値を超えたときの速度。
 */
public class PulseDetectorThresholdUp extends PulseDetectorBase
{

	private final double threshold;

	private boolean overThresholdPrev = false;
	private double yPrev;

	public PulseDetectorThresholdUp(double threshold, int timeout)
	{
		super(timeout);
		this.threshold = threshold;
	}

	@Override
	protected void onSample(double y, double offset)
	{

		if (y >= threshold) {
			// 閾値以上
			if (!overThresholdPrev) {
				// 今閾値を超えた

				// パルスとして出力
				fireOnPulse(new Pulse(
					x - (long) offset,
					time,
					x - xStart - (long) offset,
					y - yPrev));

			}
		}

		overThresholdPrev = y >= threshold;
		yPrev = y;

	}

}
