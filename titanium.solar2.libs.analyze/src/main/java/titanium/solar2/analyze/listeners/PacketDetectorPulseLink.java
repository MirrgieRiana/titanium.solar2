package titanium.solar2.analyze.listeners;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.math3.util.FastMath;

import mirrg.lithium.struct.ImmutableArray;
import mirrg.lithium.struct.Tuple3;
import titanium.solar2.libs.analyze.Packet;
import titanium.solar2.libs.analyze.Pulse;

/**
 * <b>パルスリンク法</b><br>
 * 閾値以上のパルスの到来でパケット構築モードに入る。
 * パケット構築モードではタイムアウトまで一旦待ち、
 * その間のパルスの中でジャンプできるパルスにどんどん飛んでいく。
 * ジャンプできるパルスが無い場合、そこでパケットを完成させて、
 * 貯まっているパルスをキューに戻す。
 */
public class PacketDetectorPulseLink extends PacketDetectorBufferedBase
{

	private final int firstThreshold;
	private final int offsetShort;
	private final int offsetLong;
	private final int maxXOffsetError;
	private final double maxYRatioError;
	private final double shortRatio;

	private ArrayList<Pulse> pulses;
	private StringBuilder binary;
	{
		reset();
	}

	/**
	 * @param firstThreshold
	 *            最初のパルスに必要なYの大きさ
	 * @param offsetShort
	 *            短間隔のサンプル数
	 * @param offsetLong
	 *            長間隔のサンプル数
	 * @param timeout
	 *            バッファリング期間
	 * @param maxXOffsetError
	 *            最大許容サンプル誤差。これに近づくにつれて重みが線形に減少する。正の値を指定。
	 * @param maxYRatioError
	 *            最大許容Y比率。これに近づくにつれて重みが対数的に減少する。1超えの値を指定。
	 * @param shortRatio
	 *            短間隔パルスの優先度。短間隔パルスの重みに乗算される。
	 */
	public PacketDetectorPulseLink(int firstThreshold, int offsetShort, int offsetLong, int timeout, int maxXOffsetError, double maxYRatioError, double shortRatio)
	{
		super(timeout);
		this.firstThreshold = firstThreshold;
		this.offsetShort = offsetShort;
		this.offsetLong = offsetLong;
		this.maxXOffsetError = maxXOffsetError;
		this.maxYRatioError = maxYRatioError;
		this.shortRatio = shortRatio;
	}

	private void reset()
	{
		pulses = new ArrayList<>();
		binary = new StringBuilder();
	}

	@Override
	protected void onPulse2(Pulse pulse)
	{
		if (pulses.isEmpty()) {
			// 最初のパルス待ち

			if (pulse.y >= firstThreshold) {
				// 最初のパルス受理
				pulses.add(pulse);
			} else {
				// ゴミが来た
				return;
			}

		}

		// この時点でパケット構築中

		if (pulses.get(pulses.size() - 1) == pulse) {
			// 末尾パルスが来たので次のパルスへのジャンプを試みる

			Optional<Tuple3<Pulse, Double, String>> oNextPulse = getNextPulse(pulse);
			if (oNextPulse.isPresent()) {
				// 次のパルスが有ったので飛ぶ

				pulses.add(oNextPulse.get().x);
				binary.append(oNextPulse.get().z);

			} else {
				// 次のパルスはなかった。パケット完成

				fireOnPacket(new Packet(new ImmutableArray<>(pulses), binary.toString()));
				reset();

			}

		}

	}

	private Optional<Tuple3<Pulse, Double, String>> getNextPulse(Pulse pulse)
	{
		return Stream.concat(
			getNearPulses(pulse.x + offsetShort, pulse.y, shortRatio, "0").stream(),
			getNearPulses(pulse.x + offsetLong, pulse.y, 1, "1").stream())
			.max((a, b) -> (int) Math.signum(a.y - b.y));
	}

	private ArrayList<Tuple3<Pulse, Double, String>> getNearPulses(long x, double y, double weightRatio, String bit)
	{
		ArrayList<Tuple3<Pulse, Double, String>> pulsesShort = new ArrayList<>();
		for (Pulse pulse2 : pulseQueue) {

			int xOffset = (int) (pulse2.x - x);
			double weightXOffset = 1 - 1.0 * Math.abs(xOffset) / maxXOffsetError;
			if (weightXOffset > 0) {

				double yRatio = pulse2.y / y;
				double weightYRatio = 1 - Math.abs(FastMath.log(maxYRatioError, yRatio));
				if (weightYRatio > 0) {

					pulsesShort.add(new Tuple3<>(pulse2, weightXOffset * weightYRatio + weightRatio, bit));

				}

			}

		}
		return pulsesShort;
	}

}
