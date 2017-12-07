package titanium.solar2.statistics.timearealisteners;

import java.time.LocalDateTime;
import java.util.function.Predicate;

import titanium.solar2.statistics.packet.Packet1;
import titanium.solar2.statistics.packetlisteners.timearea.ITimeAreaListener;

/**
 * TimeAreaによってイベントの発生を制御するTimeAreaListenerです。
 */
public class TimeAreaListenerFilter implements ITimeAreaListener
{

	private Predicate<LocalDateTime> filter;
	private ITimeAreaListener timeAreaListener;

	public TimeAreaListenerFilter(Predicate<LocalDateTime> filter, ITimeAreaListener timeAreaListener)
	{
		this.filter = filter;
		this.timeAreaListener = timeAreaListener;
	}

	@Override
	public void initializeTimeArea(LocalDateTime timeArea)
	{
		if (filter.test(timeArea)) timeAreaListener.initializeTimeArea(timeArea);
	}

	@Override
	public void processPacket(LocalDateTime timeArea, Packet1 packet)
	{
		if (filter.test(timeArea)) timeAreaListener.processPacket(timeArea, packet);
	}

	@Override
	public void terminateTimeArea(LocalDateTime timeArea)
	{
		if (filter.test(timeArea)) timeAreaListener.terminateTimeArea(timeArea);
	}

}
