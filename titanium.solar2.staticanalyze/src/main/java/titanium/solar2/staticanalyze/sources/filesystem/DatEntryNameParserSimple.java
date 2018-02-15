package titanium.solar2.staticanalyze.sources.filesystem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatEntryNameParserSimple implements IDatEntryNameParser
{

	private Pattern pattern;
	private DateTimeFormatter formatter;

	/**
	 * @param pattern
	 *            例："\\d{5}-(.*)\\.dat"
	 */
	public DatEntryNameParserSimple(Pattern pattern, DateTimeFormatter formatter)
	{
		this.pattern = pattern;
		this.formatter = formatter;
	}

	@Override
	public Optional<LocalDateTime> parse(String shortEntryName)
	{
		Matcher matcher = pattern.matcher(shortEntryName);
		if (matcher.matches()) {
			LocalDateTime time;
			try {
				time = LocalDateTime.parse(matcher.group(1), formatter);
			} catch (DateTimeParseException e) {
				return Optional.empty();
			}
			return Optional.of(time);
		} else {
			return Optional.empty();
		}
	}

}
