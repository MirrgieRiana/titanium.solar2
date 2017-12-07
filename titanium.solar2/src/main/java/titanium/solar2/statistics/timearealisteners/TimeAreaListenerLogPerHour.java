package titanium.solar2.statistics.timearealisteners;

import java.time.LocalDateTime;

import titanium.solar2.libs.time.timerenderers.TimeRendererSimple;
import titanium.solar2.statistics.packet.Packet1;
import titanium.solar2.statistics.packetlisteners.timearea.ITimeAreaListener;

/**
 * 1時間分の処理ごとにログを出力するTimeAreaListenerです。
 */
public class TimeAreaListenerLogPerHour implements ITimeAreaListener
{

	@Override
	public void initializeTimeArea(LocalDateTime timeArea)
	{

	}

	@Override
	public void processPacket(LocalDateTime timeArea, Packet1 packet)
	{

	}

	@Override
	public void terminateTimeArea(LocalDateTime timeArea)
	{
		if (timeArea.getMinute() == 0 && timeArea.getSecond() == 0) {
			System.out.println(TimeRendererSimple.INSTANCE.format(timeArea));
		}
	}

}
