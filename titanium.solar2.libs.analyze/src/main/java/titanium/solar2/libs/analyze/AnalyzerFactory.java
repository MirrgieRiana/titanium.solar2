package titanium.solar2.libs.analyze;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import mirrg.lithium.logging.Logger;
import titanium.solar2.libs.analyze.util.URLUtil;

public class AnalyzerFactory
{

	public static final ResourceResolver RESOURCE_RESOLVER_ANALYZE = new ResourceResolver(new PathResolverClass(AnalyzerFactory.class));

	public static Analyzer createAnalyzer(
		Class<?> clazzAssets,
		URL scriptURL,
		Logger logger,
		int samplesPerSecond,
		OutputStream out,
		IFilter filterExtension) throws Exception
	{
		ResourceResolver resourceResolver = new ResourceResolver(new PathResolverURL(scriptURL));
		resourceResolver.registerAssets("assets", new PathResolverClass(clazzAssets));

		Binding binding = new Binding();
		binding.setVariable("context", resourceResolver);
		binding.setVariable("logger", logger);
		binding.setVariable("samplesPerSecond", samplesPerSecond);
		binding.setVariable("out", new PrintStream(out));
		binding.setVariable("filterExtension", filterExtension);
		String header = RESOURCE_RESOLVER_ANALYZE.getResourceAsString("header.groovy");
		String source = URLUtil.getString(scriptURL);
		return (Analyzer) new GroovyShell(binding).evaluate(header + System.lineSeparator() + source);
	}

}
