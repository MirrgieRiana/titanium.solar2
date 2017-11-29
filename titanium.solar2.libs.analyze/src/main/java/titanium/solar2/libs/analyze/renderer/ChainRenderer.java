package titanium.solar2.libs.analyze.renderer;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

import titanium.solar2.libs.analyze.mountainlisteners.chain.Chain;

public class ChainRenderer
{

	public static String toString(Chain chain, double samplesPerSecond)
	{
		String time = TimeRenderer.format(chain.mountains.get(0).getTime(samplesPerSecond));

		if (chain.binary.startsWith("1111")) {
			String s = chain.binary.substring(4);
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
		return String.format("%s,%s,[%s]", time, chain.length, chain.binary);
	}

}
