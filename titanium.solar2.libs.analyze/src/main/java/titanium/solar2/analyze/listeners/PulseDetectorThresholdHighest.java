package titanium.solar2.analyze.listeners;

import titanium.solar2.analyze.Pulse;

/**
 * 閾値以上である間、最大地点が更新されるごとにその地点を記録。
 * 閾値を下回ったらリセットして最大地点を出力する。
 */
public class PulseDetectorThresholdHighest extends PulseDetectorBase
{

	private final double threshold;

	private boolean overThresholdPrev = false;
	private long xMax;
	private double yMax;

	public PulseDetectorThresholdHighest(double threshold, int timeout)
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

				// 無条件で更新
				xMax = x;
				yMax = y;

			} else {
				// 閾値を超えていた

				// 高さが上回る場合のみ更新
				if (y > yMax) {
					xMax = x;
					yMax = y;
				}

			}
		} else {
			// 閾値未満
			if (overThresholdPrev) {
				// 今閾値を下回った

				// 直前の山の分のパルスを出力
				fireOnPulse(new Pulse(
					xMax - (long) offset,
					time,
					xMax - xStart - (long) offset,
					yMax));

			}
		}

		overThresholdPrev = y >= threshold;

	}

}
