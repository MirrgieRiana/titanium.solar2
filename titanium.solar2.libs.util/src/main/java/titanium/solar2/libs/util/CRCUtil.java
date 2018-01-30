package titanium.solar2.libs.util;

public class CRCUtil
{

	public static int crc16(byte... bytes)
	{
		int crc16 = 0xFFFF;

		for (int i = 0; i < bytes.length; i++) {
			crc16 ^= bytes[i];
			for (int j = 0; j < 8; j++) {
				if ((crc16 & 0x0001) != 0) {
					crc16 = (crc16 >> 1) ^ 0xA001;
				} else {
					crc16 >>= 1;
				}
			}
		}

		return crc16;
	}

}
