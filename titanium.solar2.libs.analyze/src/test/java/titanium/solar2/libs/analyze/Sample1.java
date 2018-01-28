package titanium.solar2.libs.analyze;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;

import mirrg.lithium.struct.Struct1;
import titanium.solar2.libs.analyze.filters.FilterContinuous;
import titanium.solar2.libs.analyze.filters.FilterCorrelation;
import titanium.solar2.libs.analyze.filters.FilterFatten;
import titanium.solar2.libs.analyze.filters.FilterMul;
import titanium.solar2.libs.analyze.filters.FilterQOM;
import titanium.solar2.libs.analyze.packetdetectors.PacketDetectorPulseLink;
import titanium.solar2.libs.analyze.pulsedetectors.PulseDetectorThresholdHighest;
import titanium.solar2.libs.analyze.renderer.PacketRenderer;
import titanium.solar2.libs.analyze.util.AnalyzerBuilder;
import titanium.solar2.libs.analyze.util.DetectorBuilder;
import titanium.solar2.libs.analyze.util.WaveformUtils;
import titanium.solar2.libs.time.timerenderers.TimeRendererSimple;

public class Sample1
{

	public static void main(String[] args) throws Exception
	{
		int samplesPerSecond = 44100;
		int bufferLength = samplesPerSecond;

		Analyzer analyzer = new AnalyzerBuilder()
			.addListener(new FilterCorrelation(WaveformUtils.fromCSV(Sample1.class.getResource("waveform.csv")), 5))
			.addListener(new FilterContinuous(45, 80))
			.addListener(new FilterQOM())
			.addListener(new FilterMul(0.02))
			.addListener(new FilterFatten(7))
			.addListener(new DetectorBuilder<>(new PulseDetectorThresholdHighest(10, 100))
				.addListener(new DetectorBuilder<>(new PacketDetectorPulseLink(30, 45, 80, 100, 5, 2, 1))
					.addListener(packet -> System.out.println(String.format("%3d %6x %s",
						packet.pulses.length(),
						packet.getFirstPulse().x,
						PacketRenderer.toString(packet, TimeRendererSimple.INSTANCE, samplesPerSecond))))
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
