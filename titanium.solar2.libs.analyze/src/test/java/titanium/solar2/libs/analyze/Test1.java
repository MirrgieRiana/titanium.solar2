package titanium.solar2.libs.analyze;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.Test;

import mirrg.lithium.logging.EnumLogLevel;
import mirrg.lithium.logging.Logger;
import mirrg.lithium.logging.OutputStreamLogging;
import mirrg.lithium.struct.Struct1;
import titanium.solar2.analyze.listeners.FilterDummy;

public class Test1
{

	@Test
	public void test1() throws Exception
	{
		int samplesPerSecond = 44100;

		ArrayList<String> log = new ArrayList<>();
		ArrayList<String> output = new ArrayList<>();

		try (OutputStream out = new OutputStreamLogging(new Logger() {
			@Override
			public void println(String string, Optional<EnumLogLevel> oLogLevel)
			{
				output.add(string);
			}
		})) {
			Analyzer analyzer = (Analyzer) new AnalyzerFactory(
				AnalyzerFactory.RESOURCE_RESOLVER,
				new Logger() {
					@Override
					public void println(String string, Optional<EnumLogLevel> oLogLevel)
					{
						log.add(string);
					}
				},
				samplesPerSecond,
				out,
				new FilterDummy()).eval(Test1.class.getResource("test1.groovy"), "UTF-8");

			doAnalyze(analyzer, samplesPerSecond);

		}

		assertEquals(""
			+ "", String.join("\n", log));
		assertEquals(""
			+ " 45   1426 20170530-120006-116, 21, 22, 48,110, 88\n"
			+ " 35   40e1 20170530-120006-376,34,[1111111110001010100011110100111111]\n"
			+ "  9   4a10 20170530-120006-429,8,[01000011]\n"
			+ " 17   4c48 20170530-120006-442,16,[0101000011101000]\n"
			+ "  4   500b 20170530-120006-464,3,[111]\n"
			+ " 10   51c7 20170530-120006-474,9,[100001111]\n"
			+ " 45   73a5 20170530-120006-671, 11, 19, 45,205,241\n"
			+ " 27   887f 20170530-120006-792,26,[11110011100001001000101101]\n"
			+ " 44   8f00 20170530-120006-830,43,[1111111000010110000101101001111000110110100]\n"
			+ "  2   c7f1 20170530-120007-160,1,[1]\n"
			+ " 43   c88c 20170530-120007-164,42,[111010100001101000000011000111011000011010]\n"
			+ "  1   d2b0 20170530-120007-223,0,[]\n"
			+ " 45   df3e 20170530-120007-295, 60,159, 63,126,174\n"
			+ "  1   f525 20170530-120007-423,0,[]\n"
			+ " 34   f56f 20170530-120007-424,33,[111111111000011010001111010011110]\n"
			+ "  6   fe3f 20170530-120007-475,5,[11010]\n"
			+ "  4   ffa1 20170530-120007-483,3,[011]\n"
			+ "  5  1008f 20170530-120007-489,4,[1010]\n"
			+ "  4  101e2 20170530-120007-497,3,[100]\n"
			+ " 23  102dc 20170530-120007-502,22,[1101001111101100001111]\n"
			+ "  1  127a6 20170530-120007-716,0,[]\n"
			+ " 45  127f1 20170530-120007-717, 11, 19, 45,205,241\n"
			+ "  1  13c4f 20170530-120007-836,0,[]\n"
			+ " 27  13ca8 20170530-120007-838,26,[11110011100001001000101101]\n"
			+ " 14  14331 20170530-120007-876,13,[1111111100001]\n"
			+ " 32  146d9 20170530-120007-897,31,[1000000101101001111000110110100]\n"
			+ " 45  17bbc 20170530-120008-204, 21, 22, 48,110, 88\n"
			+ " 13  1a9fd 20170530-120008-472, 31\n"
			+ " 23  1ad91 20170530-120008-493,22,[0000000001111010011110]\n"
			+ "  1  1b2b7 20170530-120008-523,0,[]\n"
			+ " 13  1b2da 20170530-120008-524,12,[101101111110]\n"
			+ " 28  1b670 20170530-120008-545,27,[000011101001111101100001111]\n"
			+ " 45  1dc3c 20170530-120008-764, 11, 19, 45,205,241\n"
			+ "  2  1ea36 20170530-120008-845,1,[1]\n"
			+ "  3  1ead1 20170530-120008-849,2,[11]\n"
			+ " 26  1ebb7 20170530-120008-854,25,[1011000011010000011010001]\n"
			+ "  1  1f1c0 20170530-120008-889,0,[]\n"
			+ "  7  1f21f 20170530-120008-891,6,[000000]\n"
			+ " 18  1f35f 20170530-120008-898,17,[11010010001011010]\n"
			+ " 45  1f7b3 20170530-120008-923, 15, 21, 45,143, 45\n"
			+ "  5  22f87 20170530-120009-248,\n"
			+ " 40  2310c 20170530-120009-256,39,[010100001101000000011000111011000011010]\n"
			+ " 36  25e8b 20170530-120009-520,35,[11111111100001101000111101001111000]\n"
			+ "  8  267c2 20170530-120009-574,7,[1100011]\n"
			+ "  7  269be 20170530-120009-585,6,[101010]\n"
			+ "  1  26b5c 20170530-120009-595,0,[]\n"
			+ " 25  26b8e 20170530-120009-596,24,[011101001111101100001111]\n"
			+ " 45  29087 20170530-120009-811, 11, 19, 45,205,241\n"
			+ " 29  2a54e 20170530-120009-931, 28, 18, 45\n"
			+ " 45  2ac34 20170530-120009-971, 15, 21, 45,143, 45\n"
			+ " 45  2e352 20170530-120010-291, 21, 22, 48,110, 88\n"
			+ " 35  31319 20170530-120010-569,34,[1111111110000110100011110100111100]\n"
			+ "  1  31c09 20170530-120010-620,0,[]\n"
			+ "  9  31c48 20170530-120010-622,8,[01000011]\n"
			+ " 33  31e56 20170530-120010-634,32,[10101000011101001111101100001111]\n"
			+ " 45  344d1 20170530-120010-857, 11, 19, 45,205,241\n"
			+ " 30  35972 20170530-120010-977,29,[11110011100001001000101101000]\n"
			+ "  1  36074 20170530-120011-018,0,[]\n"
			+ " 45  360b6 20170530-120011-019, 15, 21, 45,143, 45\n"
			+ " 45  3971d 20170530-120011-335, 21, 22, 48,110, 88\n"
			+ " 37  3c7a7 20170530-120011-617, 31, 22, 47,239\n"
			+ "  7  3d153 20170530-120011-673,6,[000011]\n"
			+ " 17  3d2c0 20170530-120011-681,16,[0101010000111010]\n"
			+ " 17  3d6b0 20170530-120011-704,16,[1111101100001111]\n"
			+ " 26  3f91c 20170530-120011-904,25,[1111110100001100100010110]\n"
			+ "  1  3ff98 20170530-120011-941,0,[]\n"
			+ " 18  3ffc0 20170530-120011-942,17,[01011001110001111]\n"
			+ "  1  40d61 20170530-120012-021,0,[]\n"
			+ " 32  40d98 20170530-120012-023,31,[1111001110000100100010110100001]\n"
			+ " 45  41537 20170530-120012-067, 15, 21, 45,143, 45\n"
			+ " 45  44ae8 20170530-120012-379, 21, 22, 48,110, 88\n"
			+ " 39  47c35 20170530-120012-665,38,[11111111100001101000111101001111000110]\n"
			+ " 38  4863a 20170530-120012-723,37,[0011110101000011101001111101100001111]\n"
			+ " 45  4ad58 20170530-120012-950, 11, 19, 45,205,241\n"
			+ " 33  4c1bd 20170530-120013-068,32,[11110011100001001000101101000011]\n"
			+ " 45  4c9b8 20170530-120013-115, 15, 21, 45,143, 45\n"
			+ " 27  4e4ae 20170530-120013-271,26,[11111011100001110000010101]\n"
			+ " 45  4feb3 20170530-120013-422, 21, 22, 48,110, 88\n"
			+ " 35  530c3 20170530-120013-713,34,[1111111110000110100011110100111100]\n"
			+ "  6  539d0 20170530-120013-765,5,[11010]\n"
			+ " 20  53b72 20170530-120013-775,19,[1001010100001110100]\n"
			+ " 15  5407f 20170530-120013-804,14,[11101100001111]\n"
			+ " 45  561b1 20170530-120013-997, 11, 19, 45,205,241\n"
			+ "  1  575aa 20170530-120014-113,0,[]\n"
			+ " 34  575e1 20170530-120014-114,33,[111100111000010010001011010000111]\n"
			+ "  8  57e3a 20170530-120014-163,7,[1111111]\n"
			+ " 37  5808e 20170530-120014-176,36,[010010101000101101001111000110110100]\n"
			+ " 45  5b27e 20170530-120014-466, 21, 22, 48,110, 88\n"
			+ " 17  5c385 20170530-120014-565,16,[1111100100000000]\n"
			+ "  2  5c850 20170530-120014-593,1,[1]\n"
			+ "  4  5cc0d 20170530-120014-614,3,[010]\n"
			+ "  6  5cd08 20170530-120014-620,5,[10100]\n"
			+ "  8  5e2e0 20170530-120014-747,7,[1111111]\n"
			+ " 32  5e551 20170530-120014-761,31,[1111111110000110100011110100111]\n"
			+ " 11  5ed9e 20170530-120014-809,10,[0111110011]\n"
			+ " 12  5f087 20170530-120014-826,11,[01010100000]\n"
			+ "  4  5f300 20170530-120014-840,3,[110]\n"
			+ " 18  5f426 20170530-120014-847,17,[11111101100001111]\n"
			+ " 45  615ed 20170530-120015-043, 11, 19, 45,205,241\n"
			+ " 37  629ca 20170530-120015-159, 13, 21, 43, 46\n"
			+ " 45  632bb 20170530-120015-210, 15, 21, 45,143, 45\n"
			+ " 11  66649 20170530-120015-510,10,[1111101010]\n"
			+ "  6  6691b 20170530-120015-526,5,[11111]\n"
			+ "  5  66ac9 20170530-120015-536,4,[0000]\n"
			+ " 12  66bb0 20170530-120015-541,11,[00101010000]\n"
			+ "  2  66e18 20170530-120015-555,1,[1]\n"
			+ "  3  66eaa 20170530-120015-558,2,[10]\n"
			+ "  7  66f3e 20170530-120015-562,6,[011010]\n"
			+ " 13  67114 20170530-120015-572,12,[010111100011]\n"
			+ " 34  699df 20170530-120015-809,33,[111111111000011010001111010011111]\n"
			+ " 10  6a2e1 20170530-120015-861,9,[001000011]\n"
			+ " 19  6a4f1 20170530-120015-873,18,[001010100001110100]\n"
			+ " 16  6a95e 20170530-120015-899,15,[111101100001111]\n"
			+ "", String.join("\n", output));

	}

	private void doAnalyze(Analyzer analyzer, int samplesPerSecond) throws Exception
	{
		int bufferLength = samplesPerSecond;

		LocalDateTime time = LocalDateTime.of(2017, 5, 30, 12, 0, 6);
		int second = 0;
		new File("backup").mkdirs();
		try (InputStream in = Test1.class.getResourceAsStream("00000-20170530-120006.dat");
			OutputStream out = new FileOutputStream(new File("backup/test.dat"))) {
			byte[] bytes = new byte[bufferLength];
			byte[] bytes2 = new byte[bufferLength * 2];
			double[] buffer = new double[bufferLength];

			analyzer.preAnalyze();
			while (true) {
				int len = in.read(bytes);
				if (len == -1) break;

				for (int i = 0; i < len; i++) {
					buffer[i] = bytes[i];
				}

				analyzer.preChunk(time.plusSeconds(second));
				analyzer.processData(buffer, len, new Struct1<>(0.0));
				analyzer.postChunk();

				for (int i = 0; i < len; i++) {
					int v = (int) buffer[i];
					bytes2[i * 2] = (byte) ((v & 0xff00) >> 8);
					bytes2[i * 2 + 1] = (byte) (v & 0xff);
				}

				out.write(bytes2, 0, len * 2);

				second++;
			}
			analyzer.postAnalyze();
		}

	}

}
