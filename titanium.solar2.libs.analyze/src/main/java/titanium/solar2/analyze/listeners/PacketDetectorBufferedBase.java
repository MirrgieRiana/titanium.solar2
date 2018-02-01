package titanium.solar2.analyze.listeners;

import java.util.ArrayDeque;

import titanium.solar2.libs.analyze.Pulse;

/**
 * パルスを一旦キューにバッファリングし、
 * 一定時間が経過したパケットから順に処理を続行するパケットディテクターです。
 * {@link #onPulse2(Pulse)} が呼び出されるとき、
 * 常に {@link #pulseQueue} から指定期間の未来の全パルスを参照可能であることが保証されます。
 * {@link #onPulse2(Pulse)} 内では {@link #pulseQueue} の要素を変更することができます。
 */
public abstract class PacketDetectorBufferedBase extends PacketDetectorBase
{

	private final int term;

	protected ArrayDeque<Pulse> pulseQueue = new ArrayDeque<>();

	public PacketDetectorBufferedBase(int term)
	{
		this.term = term;
	}

	@Override
	public void onItem(Pulse pulse)
	{
		pulseQueue.addLast(pulse);
		spend(pulse.x);
	}

	@Override
	public void onTimeout(long x)
	{
		spend(x);
	}

	private void spend(long x)
	{
		while (!pulseQueue.isEmpty()) {
			Pulse pulse = pulseQueue.getFirst();
			if (pulse.x + term <= x) {

				pulseQueue.removeFirst();
				onPulse2(pulse);

			} else {
				break;
			}
		}
	}

	protected abstract void onPulse2(Pulse pulse);

}
