package titanium.solar2.libs.analyze.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayList;

public class WaveformUtils
{

	public static double[] fromCSV(InputStream inputStream)
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
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static double[] fromCSV(File file) throws FileNotFoundException
	{
		return fromCSV(new FileInputStream(file));
	}

	public static double[] fromCSV(URL url) throws IOException
	{
		return fromCSV(url.openStream());
	}

}
