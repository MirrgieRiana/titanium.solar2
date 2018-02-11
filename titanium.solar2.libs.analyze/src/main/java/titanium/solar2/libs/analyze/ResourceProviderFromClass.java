package titanium.solar2.libs.analyze;

import java.io.IOException;
import java.net.URL;

public class ResourceProviderFromClass implements IResourceProvider
{

	private Class<?> clazz;

	public ResourceProviderFromClass(Class<?> clazz)
	{
		this.clazz = clazz;
	}

	@Override
	public URL getResourceAsURL(String resourceName) throws IOException
	{
		return clazz.getResource(resourceName);
	}

}
