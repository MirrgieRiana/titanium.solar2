package titanium.solar2.staticanalyze;

import java.net.URL;

import mirrg.lithium.logging.Logger;

public interface IAnalyzeContext
{

	public String getResourceAsString(String resourceName);

	public URL getResourceAsURL(String resourceName);

	public Logger getLogger();

}
