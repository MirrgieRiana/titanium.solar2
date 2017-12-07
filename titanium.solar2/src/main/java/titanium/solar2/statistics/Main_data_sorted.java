package titanium.solar2.statistics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import titanium.solar2.libs.time.timerenderers.TimeRendererSimple;
import titanium.solar2.statistics.packet.IPacketListener;
import titanium.solar2.statistics.packet.Packet1;
import titanium.solar2.statistics.packet.PacketsIterator;

public class Main_data_sorted
{

	public static void main(String[] args) throws Exception
	{
		File dir = new File("H:\\amyf\\jesqenvina\\xa1\\kenkyuu\\recorder");
		File src = new File(dir, "5_extract\\data.csv");
		File dst = new File(dir, "5_extract\\data_sorted.csv");

		try (PrintStream out = new PrintStream(new FileOutputStream(dst))) {
			new PacketsIterator(src, TimeRendererSimple.INSTANCE).iterate(new IPacketListener() {

				private ArrayList<Packet1> packets = new ArrayList<>();

				@Override
				public void onPacket(Packet1 packet)
				{
					packets.add(packet);
				}

				@Override
				public void onFinish()
				{
					packets.stream()
						.sorted((a, b) -> a.time.compareTo(b.time))
						.forEach(out::println);
				}

			});
		}
	}

}
