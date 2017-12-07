package titanium.solar2.statistics;

import java.io.File;
import java.time.LocalDateTime;

import titanium.solar2.libs.time.timerenderers.TimeRendererSimple;
import titanium.solar2.statistics.packet.IPacketListener;
import titanium.solar2.statistics.packet.Packet1;
import titanium.solar2.statistics.packet.PacketsIterator;

public class MainPrintTimeSlippingPackets
{

	public static void main(String[] args) throws Exception
	{
		File src = new File(BaseDir.baseDir, "5_extract\\data_sorted.csv");

		new PacketsIterator(src, TimeRendererSimple.INSTANCE).iterate(new IPacketListener() {

			private int i = 0;
			private int count = 0;
			private LocalDateTime time;

			@Override
			public void onPacket(Packet1 packet)
			{
				if (time == null) {
					time = packet.time;
				} else {
					if (packet.time.isBefore(time)) {
						System.out.println(String.format("%8d %s %s %s",
							i,
							time,
							packet.time,
							packet));
						count++;
					}
					time = packet.time;
				}
				i++;
			}

			@Override
			public void onFinish()
			{
				System.out.println("Time slipping packets: " + count);
			}

		});
	}

}
