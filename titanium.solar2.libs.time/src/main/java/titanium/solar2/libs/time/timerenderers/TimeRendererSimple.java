package titanium.solar2.libs.time.timerenderers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import titanium.solar2.libs.time.ITimeRenderer;

/**
 * 2007年12月7日11時3分16秒234ミリ秒を"20171207-110316-234"のように変換するレンダラーです。
 */
public class TimeRendererSimple implements ITimeRenderer
{

	public static final TimeRendererSimple INSTANCE = new TimeRendererSimple();

	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd-HHmmss-SSS");

	@Override
	public String format(LocalDateTime time)
	{
		return FORMATTER.format(time);
	}

	@Override
	public Optional<LocalDateTime> parse(String string)
	{
		try {
			return Optional.of(LocalDateTime.parse(string, FORMATTER));
		} catch (DateTimeParseException e) {
			return Optional.empty();
		}
	}

}
