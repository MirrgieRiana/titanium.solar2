package titanium.solar2.staticanalyze;

import java.io.OutputStream;
import java.net.URL;

import mirrg.lithium.groovy.properties.PathResolverClass;
import mirrg.lithium.groovy.properties.ResourceResolver;
import mirrg.lithium.logging.Logger;
import titanium.solar2.libs.analyze.Analyzer;
import titanium.solar2.libs.analyze.AnalyzerFactory;
import titanium.solar2.libs.analyze.IFilter;

public class AnalyzerFactoryStatic
{

	public static final ResourceResolver RESOURCE_RESOLVER;
	static {
		RESOURCE_RESOLVER = AnalyzerFactory.RESOURCE_RESOLVER;
		RESOURCE_RESOLVER.setPathResolver("staticanalyze", new PathResolverClass(AnalyzerFactoryStatic.class));
	}

	public static Analyzer createAnalyzer(
		String script,
		URL baseURL,
		Logger logger,
		int samplesPerSecond,
		OutputStream out,
		IFilter filterExtension) throws Exception
	{
		return (Analyzer) new AnalyzerFactory(
			RESOURCE_RESOLVER,
			logger,
			samplesPerSecond,
			out,
			filterExtension).eval(script, baseURL);
	}

}
