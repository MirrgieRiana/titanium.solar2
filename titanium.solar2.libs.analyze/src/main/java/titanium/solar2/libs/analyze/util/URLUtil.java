package titanium.solar2.libs.analyze.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import mirrg.lithium.struct.Tuple;

/**
 * リソース名からデータを得ます。<br>
 * リソース名は次のどれかでなければなりません。<br>
 * <ul>
 * <li>URLの文字列表現（例："file://waveform.csv"）</li>
 * <li>スクリプトファイルを基準とした相対参照（例："waveform.csv"）</li>
 * <li>アプリケーションのビルトインリソース（例："assets://sample.groovy"）</li>
 * </ul>
 */
public class URLUtil
{

	/**
	 * このリソースの内容をストリームで得ます。
	 */
	public static InputStream getStream(URL url) throws IOException
	{
		return url.openStream();
	}

	/**
	 * このリソースの内容をバイト列で得ます。
	 */
	public static byte[] getBytes(URL url) throws IOException
	{
		ArrayList<Tuple<byte[], Integer>> chunks = new ArrayList<>();
		try (InputStream in = getStream(url)) {
			while (true) {
				byte[] bytes = new byte[4096];
				int len = in.read(bytes);
				if (len == -1) break;

				chunks.add(new Tuple<>(bytes, len));
			}
		}

		int len = chunks.stream()
			.mapToInt(t -> t.y)
			.sum();
		byte[] bytes = new byte[len];
		int position = 0;
		for (Tuple<byte[], Integer> chunk : chunks) {
			System.arraycopy(chunk.x, 0, bytes, position, chunk.y);
			position = chunk.y;
		}
		return bytes;
	}

	/**
	 * このリソースの内容を文字列で得ます。
	 */
	public static String getString(URL url) throws IOException
	{
		return new String(getBytes(url));
	}

}
