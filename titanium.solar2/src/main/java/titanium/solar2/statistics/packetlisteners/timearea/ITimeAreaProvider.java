package titanium.solar2.statistics.packetlisteners.timearea;

import java.time.LocalDateTime;

/**
 * {@link PacketListenerTimeArea} に対して時間の区切り方を与えるインターフェースです。
 */
public interface ITimeAreaProvider
{

	public LocalDateTime getTimeArea(LocalDateTime time);

	public LocalDateTime getNextTimeArea(LocalDateTime timeArea);

}
