package titanium.solar2.libs.analyze.mountainlisteners.chain;

import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

import mirrg.lithium.struct.ImmutableArray;
import titanium.solar2.libs.analyze.filters.mountain.Mountain;

public class Chain
{

	public final ImmutableArray<Mountain> mountains;
	public final String binary;
	public final int length;

	public Chain(ImmutableArray<Mountain> mountains, String binary)
	{
		this.mountains = mountains;
		this.binary = binary;
		this.length = binary.length();
	}

	public boolean isValid()
	{
		if (binary.startsWith("1111")) {
			String s = binary.substring(4);
			if (s.length() % 8 == 0) {
				return true;
			}
		}
		return false;
	}

	public Optional<int[]> getBytes()
	{
		if (binary.startsWith("1111")) {
			String s = binary.substring(4);
			if (s.length() % 8 == 0) {
				return Optional.of(IntStream
					.range(0, s.length() / 8)
					.map(i1 -> i1 * 8)
					.mapToObj(i2 -> s.substring(i2, i2 + 8))
					.map(s21 -> StringUtils.reverse(s21))
					.mapToInt(s22 -> Integer.parseInt(s22, 2))
					.toArray());
			}
		}
		return Optional.empty();
	}

	public Mountain getFirstMountain()
	{
		return mountains.get(0);
	}

}
