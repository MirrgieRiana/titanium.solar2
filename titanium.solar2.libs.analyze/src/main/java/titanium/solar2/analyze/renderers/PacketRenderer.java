package titanium.solar2.analyze.renderers;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

import titanium.solar2.analyze.Packet;
import titanium.solar2.libs.time.ITimeRenderer;

public class PacketRenderer
{

	public static String toString(Packet packet, ITimeRenderer timeRenderer, double samplesPerSecond)
	{
		String time = timeRenderer.format(packet.pulses.get(0).getTime(samplesPerSecond));

		if (packet.binary.startsWith("1111")) {
			String s = packet.binary.substring(4);
			if (s.length() % 8 == 0) {
				return String.format("%s,%s",
					time,
					IntStream
						.range(0, s.length() / 8)
						.map(i1 -> i1 * 8)
						.mapToObj(i2 -> s.substring(i2, i2 + 8))
						.map(s21 -> StringUtils.reverse(s21))
						.mapToInt(s22 -> Integer.parseInt(s22, 2))
						.mapToObj(i3 -> String.format("%3d", i3))
						.collect(Collectors.joining(",")));
			}
		}
		return String.format("%s,%s,[%s]", time, packet.length, packet.binary);
	}

}
