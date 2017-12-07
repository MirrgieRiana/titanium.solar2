package titanium.solar2.statistics.packet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import titanium.solar2.libs.time.ITimeRenderer;

/**
 * パケットファイルのパケットを先頭から順に処理します。
 */
public class PacketsIterator
{

	private File fileSrc;
	private ITimeRenderer timeRenderer;

	public PacketsIterator(File fileSrc, ITimeRenderer timeRenderer)
	{
		this.fileSrc = fileSrc;
		this.timeRenderer = timeRenderer;
	}

	protected BufferedReader createBufferedReader() throws FileNotFoundException
	{
		return new BufferedReader(new InputStreamReader(new FileInputStream(fileSrc)));
	}

	public void iterate(IPacketListener packetListener) throws IOException
	{
		try (BufferedReader in = createBufferedReader()) {
			while (true) {
				String line = in.readLine();
				if (line == null) break;

				// パケット行解析
				Optional<Packet1> oPacket = Packet1.parsePacket1ValidatedCrc1(line, timeRenderer);
				if (!oPacket.isPresent()) continue;

				// 受理
				packetListener.onPacket(oPacket.get());

			}
			packetListener.onFinish();
		}
	}

}
