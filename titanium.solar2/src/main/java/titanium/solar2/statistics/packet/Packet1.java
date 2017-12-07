package titanium.solar2.statistics.packet;

import java.time.LocalDateTime;
import java.util.Optional;

import mirrg.lithium.struct.ImmutableArray;
import titanium.solar2.libs.time.ITimeRenderer;

public class Packet1 extends Packet
{

	public final int id;
	public final int voltage;
	public final int temperature;
	public final int crc01;
	public final int crc02;

	protected Packet1(LocalDateTime time, ImmutableArray<Integer> data, String expression, int id, int voltage, int temperature, int crc01, int crc02)
	{
		super(time, data, expression);

		this.id = id;
		this.voltage = voltage;
		this.temperature = temperature;
		this.crc01 = crc01;
		this.crc02 = crc02;
	}

	public int crc()
	{
		return crc(new byte[] {
			(byte) id, (byte) voltage, (byte) temperature,
		});
	}

	public static int crc(byte[] bytes)
	{
		int crc16 = 0xFFFF;

		for (int i = 0; i < bytes.length; i++) {
			crc16 ^= bytes[i];
			for (int j = 0; j < 8; j++) {
				if ((crc16 & 0x0001) != 0) {
					crc16 = (crc16 >> 1) ^ 0xA001;
				} else {
					crc16 >>= 1;
				}
			}
		}
		return crc16;
	}

	public static Optional<Packet1> parsePacket1(String string, ITimeRenderer timeRenderer)
	{
		return Packet.parsePacket(string, timeRenderer)
			.map(p -> {
				if (p.data.length() == 5) {
					return new Packet1(p.time, p.data, string, p.data.get(0), p.data.get(1), p.data.get(2), p.data.get(3), p.data.get(4));
				} else {
					return null;
				}
			});
	}

	public static Optional<Packet1> parsePacket1ValidatedCrc1(String string, ITimeRenderer timeRenderer)
	{
		return parsePacket1(string, timeRenderer)
			.map(p -> {
				if ((p.crc() & 0xff) == p.crc01) {
					return p;
				} else {
					return null;
				}
			});
	}

}
