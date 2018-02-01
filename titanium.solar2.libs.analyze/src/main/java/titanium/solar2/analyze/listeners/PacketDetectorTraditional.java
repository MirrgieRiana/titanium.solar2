package titanium.solar2.analyze.listeners;

import java.util.ArrayList;

import mirrg.lithium.struct.ImmutableArray;
import titanium.solar2.analyze.Packet;
import titanium.solar2.analyze.Pulse;

/**
 * 受け取ったパルスを順番にすべて受理するパケットディテクターです。
 * 最後にパルスを受け取ってから一定時間が経過すると、最後のパケットを出力します。
 */
public class PacketDetectorTraditional extends PacketDetectorBase
{

	private final int threshold;
	private final int timeout;

	private ArrayList<Pulse> pulses;
	private StringBuilder binary;
	{
		reset();
	}

	public PacketDetectorTraditional(int threshold, int timeout)
	{
		this.threshold = threshold;
		this.timeout = timeout;
	}

	private void reset()
	{
		pulses = new ArrayList<>();
		binary = new StringBuilder();
	}

	@Override
	public void onItem(Pulse pulse)
	{
		spend(pulse.x);

		if (!pulses.isEmpty()) {
			if (pulse.x - pulses.get(pulses.size() - 1).x > threshold) {
				// 長間隔
				binary.append("1");
			} else {
				// 短間隔
				binary.append("0");
			}
		}
		pulses.add(pulse);
	}

	@Override
	public void onTimeout(long x)
	{
		spend(x);
	}

	private void spend(long x)
	{
		if (!pulses.isEmpty()) {
			if (x - pulses.get(pulses.size() - 1).x > timeout) {
				fireOnPacket(new Packet(new ImmutableArray<>(pulses), binary.toString()));
				reset();
			}
		}
	}

}
