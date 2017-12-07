package titanium.solar2.statistics.packetlisteners.timearea;

import java.time.LocalDateTime;

import titanium.solar2.statistics.packet.Packet1;

/**
 * {@link PacketListenerTimeArea} から通知されるTimeAreaイベントのリスナです。
 */
public interface ITimeAreaListener
{

	public void initializeTimeArea(LocalDateTime timeArea);

	public void processPacket(LocalDateTime timeArea, Packet1 packet);

	public void terminateTimeArea(LocalDateTime timeArea);

}
