package titanium.solar2.statistics.timearealisteners;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;

import titanium.solar2.statistics.packet.Packet1;
import titanium.solar2.statistics.packetlisteners.timearea.ITimeAreaListener;

/**
 * 区間中のパケットをIDごとに集計するTimeAreaListenerです。
 */
public abstract class TimeAreaListenerPacketStatisticsPerID implements ITimeAreaListener
{

	protected final PrintStream out;
	protected ArrayList<ArrayList<Packet1>> packetTable;

	public TimeAreaListenerPacketStatisticsPerID(PrintStream out)
	{
		this.out = out;
	}

	@Override
	public void initializeTimeArea(LocalDateTime timeArea)
	{
		packetTable = new ArrayList<>();
		packetTable.add(null);
		for (int id = 1; id <= 48; id++) {
			packetTable.add(new ArrayList<>());
		}
	}

	@Override
	public void processPacket(LocalDateTime timeArea, Packet1 packet)
	{
		if (1 <= packet.id && packet.id <= 48) {
			packetTable.get(packet.id).add(packet);
		}
	}

}
