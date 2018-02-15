package titanium.solar2.staticanalyze.sources.filesystem;

import java.time.LocalDateTime;
import java.util.Optional;

public class DatEntryNameParserOr implements IDatEntryNameParser
{

	private IDatEntryNameParser[] datEntryNameParsers;

	public DatEntryNameParserOr(IDatEntryNameParser... datEntryNameParsers)
	{
		this.datEntryNameParsers = datEntryNameParsers;
	}

	@Override
	public Optional<LocalDateTime> parse(String shortEntryName)
	{
		for (IDatEntryNameParser datEntryNameParser : datEntryNameParsers) {
			Optional<LocalDateTime> oTime = datEntryNameParser.parse(shortEntryName);
			if (oTime.isPresent()) return oTime;
		}
		return Optional.empty();
	}

}
