package titanium.solar2.libs.time;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Test;

import titanium.solar2.libs.time.timerenderers.TimeRendererSimple;

public class TestTimeRendererSimple
{

	@Test
	public void test1()
	{
		ITimeRenderer timeRenderer = new TimeRendererSimple();

		assertEquals("20010508-212345-456", timeRenderer.format(LocalDateTime.of(2001, 5, 8, 21, 23, 45, 456_423_234)));
		assertEquals("20010508-212345-000", timeRenderer.format(LocalDateTime.of(2001, 5, 8, 21, 23, 45)));
		assertEquals("00010508-010101-000", timeRenderer.format(LocalDateTime.of(1, 5, 8, 1, 1, 1)));
		assertEquals("20171207-110316-000", timeRenderer.format(LocalDateTime.ofInstant(Instant.ofEpochSecond(1512644596), ZoneOffset.UTC)));

		assertEquals(LocalDateTime.of(2001, 5, 8, 21, 23, 45, 456_000_000), timeRenderer.parse("20010508-212345-456").get());
		assertEquals(LocalDateTime.of(2001, 5, 8, 21, 23, 45), timeRenderer.parse("20010508-212345-000").get());
		assertEquals(LocalDateTime.of(1, 5, 8, 1, 1, 1), timeRenderer.parse("00010508-010101-000").get());

	}

}
