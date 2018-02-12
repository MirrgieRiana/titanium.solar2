package titanium.solar2.libs.analyze;

import java.io.IOException;
import java.net.URL;

public class PathResolverURL implements IPathResolver
{

	private URL baseURL;

	public PathResolverURL(URL baseURL)
	{
		this.baseURL = baseURL;
	}

	@Override
	public URL getResource(String path) throws IOException
	{
		return new URL(baseURL, path);
	}

}
