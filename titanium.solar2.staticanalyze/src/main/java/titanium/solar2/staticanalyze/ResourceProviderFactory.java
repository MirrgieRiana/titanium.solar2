package titanium.solar2.staticanalyze;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

import titanium.solar2.libs.analyze.IResourceProvider;
import titanium.solar2.libs.analyze.ResourceProviderFromClass;

public class ResourceProviderFactory
{

	private static final Pattern PATTERN_URL = Pattern.compile(".*://.*");

	private IResourceProvider resourceProviderAssets;

	public ResourceProviderFactory(Class<Main> clazzAssets)
	{
		resourceProviderAssets = new ResourceProviderFromClass(clazzAssets);
	}

	public IResourceProvider getResourceProvider(URL baseURL)
	{
		return new IResourceProvider() {
			@Override
			public URL getResourceAsURL(String resourceName) throws IOException
			{
				if (resourceName.startsWith("assets://")) {
					resourceName = resourceName.substring("assets://".length());
					URL url = resourceProviderAssets.getResourceAsURL(resourceName);
					if (url == null) throw new IOException("No such asset: " + resourceName);
					return url;
				} else if (PATTERN_URL.matcher(resourceName).matches()) {
					return new URL(resourceName);
				} else {
					return new URL(baseURL, resourceName);
				}
			}
		};
	}

}
