package titanium.solar2.libs.analyze.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.stream.DoubleStream;

public class WaveformUtils
{

	public static double[] fromCSV(InputStream inputStream) throws IOException
	{
		try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
			ArrayList<Double> waveform = new ArrayList<>();
			while (true) {
				String line = in.readLine();
				if (line != null) {
					if (!line.isEmpty()) {
						waveform.add(Double.parseDouble(line));
					}
				} else {
					return waveform.stream()
						.mapToDouble(d -> d)
						.toArray();
				}
			}
		}
	}

	public static double[] fromCSV(File file) throws IOException
	{
		return fromCSV(new FileInputStream(file));
	}

	public static double[] fromCSV(URL url) throws IOException
	{
		return fromCSV(url.openStream());
	}

	/**
	 * 全サンプルの合計が0になるようにゲインを調整した配列を生成します。
	 */
	public static double[] normalize(double[] waveform)
	{
		if (waveform.length == 0) return waveform;
		double average = DoubleStream.of(waveform)
			.average()
			.getAsDouble();
		return DoubleStream.of(waveform)
			.map(d -> d - average)
			.toArray();
	}

}
