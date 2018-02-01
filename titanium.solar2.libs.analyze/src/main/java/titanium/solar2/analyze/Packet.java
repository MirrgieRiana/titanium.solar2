package titanium.solar2.analyze;

import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

import mirrg.lithium.struct.ImmutableArray;

public class Packet
{

	public final ImmutableArray<Pulse> pulses;
	public final String binary;
	public final int length;

	public Packet(ImmutableArray<Pulse> pulses, String binary)
	{
		this.pulses = pulses;
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

	public Pulse getFirstPulse()
	{
		return pulses.get(0);
	}

}
