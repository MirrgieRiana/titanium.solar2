package titanium.solar2.statistics.timeareaproviders;

import java.time.LocalDateTime;

import titanium.solar2.statistics.packetlisteners.timearea.ITimeAreaProvider;

/**
 * 30秒ごとの解析を表すTimeAreaProviderです。
 */
public class TimeAreaProviderPer30s implements ITimeAreaProvider
{

	@Override
	public LocalDateTime getTimeArea(LocalDateTime time)
	{
		return time.withSecond(time.getSecond() / 30 * 30).withNano(0);
	}

	@Override
	public LocalDateTime getNextTimeArea(LocalDateTime timeArea)
	{
		if (timeArea.getSecond() == 30) {
			return timeArea.plusMinutes(1).withSecond(0); // 閏秒対策
		} else {
			return timeArea.plusSeconds(30);
		}
	}

}
