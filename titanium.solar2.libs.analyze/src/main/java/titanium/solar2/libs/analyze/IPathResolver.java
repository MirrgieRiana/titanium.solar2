package titanium.solar2.libs.analyze;

import java.io.IOException;
import java.net.URL;

public interface IPathResolver
{

	/**
	 * パスを実際のリソースの場所を表すURLにします。
	 *
	 * @param path
	 *            絶対または相対パス文字列
	 * @throws IOException
	 *             URLが無効か参照先のリソースが存在しない場合に発生する可能性があります。
	 */
	public URL getResource(String path) throws IOException;

}
