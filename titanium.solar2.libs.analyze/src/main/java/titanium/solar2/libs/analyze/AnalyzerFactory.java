package titanium.solar2.libs.analyze;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import groovy.lang.Binding;
import mirrg.lithium.groovy.properties.GroovyProperties;
import mirrg.lithium.groovy.properties.PathResolverClass;
import mirrg.lithium.groovy.properties.PathResolverFileSystem;
import mirrg.lithium.groovy.properties.ResourceResolver;
import mirrg.lithium.groovy.properties.URLUtil;
import mirrg.lithium.logging.Logger;

public class AnalyzerFactory extends GroovyProperties
{

	public static final ResourceResolver RESOURCE_RESOLVER;
	static {
		RESOURCE_RESOLVER = new ResourceResolver(new PathResolverFileSystem(new File(".")));
		RESOURCE_RESOLVER.setPathResolver("analyze", new PathResolverClass(AnalyzerFactory.class));
	}

	public static Analyzer createAnalyzer(
		String resourceName,
		String charset,
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
			filterExtension).eval(resourceName, charset);
	}

	protected Logger logger;
	protected int samplesPerSecond;
	protected OutputStream out;
	protected IFilter filterExtension;

	public AnalyzerFactory(
		ResourceResolver resourceResolver,
		Logger logger,
		int samplesPerSecond,
		OutputStream out,
		IFilter filterExtension)
	{
		super(resourceResolver);
		this.logger = logger;
		this.samplesPerSecond = samplesPerSecond;
		this.out = out;
		this.filterExtension = filterExtension;
	}

	@Override
	protected void bindVariables(Binding binding)
	{
		binding.setVariable("logger", logger);
		binding.setVariable("samplesPerSecond", samplesPerSecond);
		binding.setVariable("out", new PrintStream(out));
		binding.setVariable("filterExtension", filterExtension);
	}

	@Override
	protected String convertScript(String script) throws IOException
	{
		return URLUtil.getString(getResourceResolver().getResource("analyze://header.groovy"), "UTF-8") + System.lineSeparator() + script;
	}

}
