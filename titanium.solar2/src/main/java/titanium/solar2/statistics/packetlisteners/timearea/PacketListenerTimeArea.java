package titanium.solar2.statistics.packetlisteners.timearea;

import java.time.LocalDateTime;
import java.util.ArrayList;

import titanium.solar2.statistics.packet.IPacketListener;
import titanium.solar2.statistics.packet.Packet1;

/**
 * 解析を時間領域で区切り、TimeAreaイベントを発生させるPacketListenerです。
 * パケットの時間がいきなり飛んだ場合、その間の何もない領域もイベントが通知されます。
 */
public class PacketListenerTimeArea implements IPacketListener
{

	private ArrayList<ITimeAreaListener> timeAreaListeners = new ArrayList<>();
	private ITimeAreaProvider timeAreaProvider;

	public PacketListenerTimeArea(ITimeAreaProvider timeAreaProvider)
	{
		this.timeAreaProvider = timeAreaProvider;
	}

	public void addTimeAreaListener(ITimeAreaListener timeAreaListener)
	{
		timeAreaListeners.add(timeAreaListener);
	}

	private LocalDateTime timeArea;

	@Override
	public void onPacket(Packet1 packet)
	{
		LocalDateTime timeArea2 = timeAreaProvider.getTimeArea(packet.time);
		if (timeArea == null) {
			timeArea = timeArea2;
			for (ITimeAreaListener timeAreaListener : timeAreaListeners) {
				timeAreaListener.initializeTimeArea(timeArea);
			}
		} else {
			while (timeArea2.isAfter(timeArea)) {
				for (ITimeAreaListener timeAreaListener : timeAreaListeners) {
					timeAreaListener.terminateTimeArea(timeArea);
				}
				timeArea = timeAreaProvider.getNextTimeArea(timeArea);
				for (ITimeAreaListener timeAreaListener : timeAreaListeners) {
					timeAreaListener.initializeTimeArea(timeArea);
				}
			}
		}
		for (ITimeAreaListener timeAreaListener : timeAreaListeners) {
			timeAreaListener.processPacket(timeArea, packet);
		}
	}

	@Override
	public void onFinish()
	{
		for (ITimeAreaListener timeAreaListener : timeAreaListeners) {
			timeAreaListener.terminateTimeArea(timeArea);
		}
	}

}
