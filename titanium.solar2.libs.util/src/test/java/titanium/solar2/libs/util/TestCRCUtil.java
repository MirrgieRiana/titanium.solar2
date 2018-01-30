package titanium.solar2.libs.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestCRCUtil
{

	@Test
	public void test_crc16()
	{
		assertEquals("4b37", String.format("%04x", CRCUtil.crc16("123456789".getBytes())));
	}

}
