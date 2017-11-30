package titanium.solar2.libs.kisyou;

import java.time.LocalDateTime;

public class Sample1
{

	public static void main(String[] args) throws Exception
	{
		LocalDateTime from = LocalDateTime.of(2017, 4, 1, 0, 0, 0);
		LocalDateTime to = LocalDateTime.of(2017, 4, 5, 0, 0, 0);
		for (LocalDateTime time = from; time.compareTo(to) < 0; time = time.plusDays(1)) {
			HKisyou.getKisyouEntries("45", "0382", new Key(time)).stream()
				.map(te -> String.format("%s,%5s,%5s,%2s",
					te.time,
					te.kousui.isPresent() ? String.format("%5.2f", te.kousui.getAsDouble()) : "-",
					te.temperature.isPresent() ? String.format("%5.2f", te.temperature.getAsDouble()) : "-",
					te.nissyou.isPresent() ? String.format("%2d", te.nissyou.getAsInt()) : "-"))
				.forEach(System.out::println);
		}
	}

}
