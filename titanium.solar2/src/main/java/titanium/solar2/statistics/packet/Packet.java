package titanium.solar2.statistics.packet;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

import mirrg.lithium.struct.ImmutableArray;
import titanium.solar2.libs.time.ITimeRenderer;

public class Packet
{

	public final LocalDateTime time;
	public final ImmutableArray<Integer> data;
	public final String expression;

	protected Packet(LocalDateTime time, ImmutableArray<Integer> data, String expression)
	{
		this.time = time;
		this.data = data;
		this.expression = expression;
	}

	@Override
	public String toString()
	{
		return expression;
	}

	public static final Pattern PATTERN = Pattern.compile("([^,]*),\\s*(\\d+(?:,\\s*\\d+)*)");

	public static Optional<Packet> parsePacket(String string, ITimeRenderer timeRenderer)
	{

		// カット
		String[] record = string.split(",");
		for (int i = 0; i < record.length; i++) {
			record[i] = record[i].trim();
		}

		// タイムスタンプは無いといけない
		if (record.length < 1) return Optional.empty();

		// タイムスタンプ解析
		Optional<LocalDateTime> oTime = timeRenderer.parse(record[0]);
		if (!oTime.isPresent()) return Optional.empty();

		// データ部解析
		Integer[] data = new Integer[record.length - 1];
		try {
			for (int i = 1; i < record.length; i++) {
				data[i - 1] = Integer.parseInt(record[i], 10);
			}
		} catch (NumberFormatException e) {
			return Optional.empty();
		}

		return Optional.of(new Packet(oTime.get(), new ImmutableArray<>(data), string));
	}

}
