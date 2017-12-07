package titanium.solar2.kisyou;

import java.time.LocalDateTime;

import titanium.solar2.libs.kisyou.HKisyou;
import titanium.solar2.libs.kisyou.Key;
import titanium.solar2.libs.time.ITimeRenderer;
import titanium.solar2.libs.time.timerenderers.TimeRendererSimple;

public class MainKisyou
{

	public static void main(String[] args) throws Exception
	{
		String precNo = "45";
		String blockNo = "0382";
		LocalDateTime from = LocalDateTime.of(2017, 5, 30, 0, 0, 0);
		LocalDateTime to = LocalDateTime.of(2017, 6, 27, 0, 0, 0);
		ITimeRenderer timeRenderer = TimeRendererSimple.INSTANCE;

		for (LocalDateTime time = from; time.compareTo(to) < 0; time = time.plusDays(1)) {
			HKisyou.getKisyouEntries(precNo, blockNo, new Key(time)).stream()
				.map(te -> {
					return String.format("%s,%5s,%5s,%2s",
						timeRenderer.format(te.time),
						te.kousui.isPresent() ? String.format("%5.2f", te.kousui.getAsDouble()) : "-",
						te.temperature.isPresent() ? String.format("%5.2f", te.temperature.getAsDouble()) : "-",
						te.nissyou.isPresent() ? String.format("%2d", te.nissyou.getAsInt()) : "-");
				})
				.forEach(System.out::println);
		}
	}

}
