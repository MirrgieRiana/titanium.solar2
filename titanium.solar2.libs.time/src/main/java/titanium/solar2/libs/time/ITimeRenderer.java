package titanium.solar2.libs.time;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ITimeRenderer
{

	public String format(LocalDateTime time);

	public Optional<LocalDateTime> parse(String string);

}
