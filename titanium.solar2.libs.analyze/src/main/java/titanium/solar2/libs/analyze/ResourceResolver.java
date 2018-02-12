package titanium.solar2.libs.analyze;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import titanium.solar2.libs.analyze.util.URLUtil;

/**
 * リソース名をリソースを表すURLに変換します。<br>
 * リソース名を表す文字列は次のどれかでなければなりません。
 * <ul>
 * <li>URLの文字列表現（例："file:/c:/waveform.csv"）</li>
 * <li>プロトコル指定のない絶対もしくは相対パス名（例："waveform.csv"）</li>
 * <li>アプリケーションで定義されたリソース（例："assets://sample.groovy"）</li>
 * </ul>
 */
public class ResourceResolver
{

	private static final Pattern PATTERN_PROTOCOL = Pattern.compile("\\A([^:]*):/");

	private IPathResolver pathResolverDefault;

	private Hashtable<String, IPathResolver> tablePathResolver = new Hashtable<>();

	public ResourceResolver(IPathResolver pathResolverDefault)
	{
		this.pathResolverDefault = pathResolverDefault;
	}

	public void registerAssets(String protocol, IPathResolver pathResolver)
	{
		tablePathResolver.put(protocol, pathResolver);
	}

	/**
	 * このリソースへの参照をURLで得ます。
	 *
	 * @throws IOException
	 *             参照先のリソースが存在しない場合に発生する可能性があります。
	 */
	public URL getResourceAsURL(String resourceName) throws IOException
	{
		Matcher matcher = PATTERN_PROTOCOL.matcher(resourceName);
		if (matcher.find()) {
			// プロトコル指定あり
			String protocol = matcher.group(1);
			IPathResolver pathResolver = tablePathResolver.get(protocol);
			if (pathResolver != null) {
				// 独自プロトコル
				return pathResolver.getResource(resourceName.substring(protocol.length() + 3)); // "protocol" + "://"
			} else {
				// デフォルトプロトコル
				return new URL(resourceName);
			}
		} else {
			// 単純パス
			return pathResolverDefault.getResource(resourceName);
		}
	}

	/**
	 * このリソースの内容をストリームで得ます。
	 *
	 * @throws IOException
	 *             参照先のリソースが存在しない場合に発生する可能性があります。
	 */
	public InputStream getResourceAsStream(String resourceName) throws IOException
	{
		return URLUtil.getStream(getResourceAsURL(resourceName));
	}

	/**
	 * このリソースの内容をバイナリで得ます。
	 *
	 * @throws IOException
	 *             参照先のリソースが存在しない場合に発生する可能性があります。
	 */
	public byte[] getResourceAsBytes(String resourceName) throws IOException
	{
		return URLUtil.getBytes(getResourceAsURL(resourceName));
	}

	/**
	 * このリソースの内容を文字列で得ます。
	 *
	 * @throws IOException
	 *             参照先のリソースが存在しない場合に発生する可能性があります。
	 */
	public String getResourceAsString(String resourceName) throws IOException
	{
		return URLUtil.getString(getResourceAsURL(resourceName));
	}

}
