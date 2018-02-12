package titanium.solar2.libs.analyze.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.stream.DoubleStream;

public class WaveformUtil
{

	/**
	 * ストリームは自動的に閉じられません。
	 */
	public static double[] fromCSV(Reader reader) throws IOException
	{
		return new BufferedReader(reader).lines()
			.filter(l -> !l.isEmpty()) // 空行無視
			.filter(l -> !l.startsWith("#")) // コメントアウト無視
			.mapToDouble(Double::parseDouble)
			.toArray();
	}

	public static double[] fromCSV(String string) throws IOException
	{
		try (StringReader in = new StringReader(string)) {
			return fromCSV(in);
		}
	}

	/**
	 * ストリームは自動的に閉じられません。
	 */
	public static double[] fromCSV(InputStream inputStream) throws IOException
	{
		return fromCSV(new InputStreamReader(inputStream));
	}

	public static double[] fromCSV(File file) throws IOException
	{
		try (FileInputStream in = new FileInputStream(file)) {
			return fromCSV(in);
		}
	}

	public static double[] fromCSV(URL url) throws IOException
	{
		try (InputStream in = url.openStream()) {
			return fromCSV(in);
		}
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
