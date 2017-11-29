package titanium.solar2.libs.analyze;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;

import mirrg.lithium.struct.Struct1;
import titanium.solar2.libs.analyze.filters.FilterContinuous;
import titanium.solar2.libs.analyze.filters.FilterCorrelation;
import titanium.solar2.libs.analyze.filters.FilterMul;
import titanium.solar2.libs.analyze.filters.FilterQOM;
import titanium.solar2.libs.analyze.filters.mountain.FilterExtractMountain;
import titanium.solar2.libs.analyze.mountainlisteners.chain.MountainListenerChain;
import titanium.solar2.libs.analyze.renderer.ChainRenderer;
import titanium.solar2.libs.analyze.util.AnalyzerBuilder;
import titanium.solar2.libs.analyze.util.FilterExtractMountainBuilder;
import titanium.solar2.libs.analyze.util.MountainListenerChainBuilder;
import titanium.solar2.libs.analyze.util.WaveformUtils;

public class Sample1
{

	public static void main(String[] args) throws Exception
	{
		int samplesPerSecond = 44100;
		int bufferLength = samplesPerSecond;

		Analyzer analyzer = new AnalyzerBuilder()
			.addFilter(new FilterCorrelation(WaveformUtils.fromCSV(Sample1.class.getResource("waveform.csv")), 5))
			.addFilter(new FilterContinuous(45, 80))
			.addFilter(new FilterQOM())
			.addFilter(new FilterMul(0.02))
			.addFilter(new FilterExtractMountainBuilder(new FilterExtractMountain(7, 10, 100))
				.addMountainListener(new MountainListenerChainBuilder(new MountainListenerChain(45, 80, 30, 100, 3))
					.addChainListener(chain -> System.out.println(String.format("%3d %6x %s",
						chain.mountains.length(),
						chain.getFirstMountain().x,
						ChainRenderer.toString(chain, samplesPerSecond))))
					.get())
				.get())
			.get();

		LocalDateTime time = LocalDateTime.of(2017, 5, 30, 12, 0, 6);
		int second = 0;
		new File("backup").mkdirs();
		try (InputStream in = Sample1.class.getResourceAsStream("00000-20170530-120006.dat");
			OutputStream out = new FileOutputStream(new File("backup/test.dat"))) {
			byte[] bytes = new byte[bufferLength];
			byte[] bytes2 = new byte[bufferLength * 2];
			double[] buffer = new double[bufferLength];
			while (true) {
				int len = in.read(bytes);
				if (len == -1) break;

				for (int i = 0; i < len; i++) {
					buffer[i] = bytes[i];
				}

				analyzer.startChunk(time.plusSeconds(second));
				analyzer.processData(buffer, len, new Struct1<>(0.0));

				for (int i = 0; i < len; i++) {
					int v = (int) buffer[i];
					bytes2[i * 2] = (byte) ((v & 0xff00) >> 8);
					bytes2[i * 2 + 1] = (byte) (v & 0xff);
				}

				out.write(bytes2, 0, len * 2);

				second++;
			}
		}

	}

}
