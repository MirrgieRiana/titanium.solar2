package titanium.solar2.libs.analyze;

import java.io.IOException;
import java.net.URL;

public class PathResolverClass implements IPathResolver
{

	private Class<?> clazz;

	public PathResolverClass(Class<?> clazz)
	{
		this.clazz = clazz;
	}

	@Override
	public URL getResource(String path) throws IOException
	{
		URL url = clazz.getResource(path);
		if (url == null) throw new IOException("No such resource: " + path + " of " + clazz.getName());
		return url;
	}

}
