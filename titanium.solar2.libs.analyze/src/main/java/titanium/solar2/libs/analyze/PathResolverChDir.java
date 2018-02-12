package titanium.solar2.libs.analyze;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class PathResolverChDir implements IPathResolver
{

	@Override
	public URL getResource(String path) throws IOException
	{
		return new URL(new File(".").toURI().toURL(), path);
	}

}
