package titanium.solar2.staticanalyze.sources.filesystem;

import java.time.LocalDateTime;
import java.util.Optional;

public interface IDatEntryNameParser
{

	/**
	 * @param shortEntryName
	 *            親ノードの名称を含まない短いエントリー名。
	 */
	public Optional<LocalDateTime> parse(String shortEntryName);

}
