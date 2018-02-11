package titanium.solar2.libs.analyze;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import mirrg.lithium.struct.Tuple;

public interface IResourceProvider
{

	/**
	 * このリソースへの参照をURLで得ます。
	 */
	public URL getResourceAsURL(String resourceName) throws IOException;

	/**
	 * このリソースの内容をストリームで得ます。
	 */
	public default InputStream getResourceAsStream(String resourceName) throws IOException
	{
		return getResourceAsURL(resourceName).openStream();
	}

	/**
	 * このリソースの内容をバイナリで得ます。
	 */
	public default byte[] getResourceAsBytes(String resourceName) throws IOException
	{
		ArrayList<Tuple<byte[], Integer>> chunks = new ArrayList<>();
		try (InputStream in = getResourceAsStream(resourceName)) {
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
	public default String getResourceAsString(String resourceName) throws IOException
	{
		return new String(getResourceAsBytes(resourceName));
	}

}
