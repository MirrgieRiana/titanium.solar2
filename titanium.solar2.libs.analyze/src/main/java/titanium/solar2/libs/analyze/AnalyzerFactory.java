package titanium.solar2.libs.analyze;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import groovy.lang.Binding;
import mirrg.lithium.groovy.properties.GroovyProperties;
import mirrg.lithium.groovy.properties.PathResolverClass;
import mirrg.lithium.groovy.properties.ResourceResolver;
import mirrg.lithium.logging.Logger;

public class AnalyzerFactory extends GroovyProperties
{

	public static Analyzer createAnalyzer(
		String resourceName,
		Logger logger,
		int samplesPerSecond,
		OutputStream out,
		IFilter filterExtension) throws Exception
	{
		return (Analyzer) new AnalyzerFactory(
			logger,
			samplesPerSecond,
			out,
			filterExtension).eval(resourceName);
	}

	protected Logger logger;
	protected int samplesPerSecond;
	protected OutputStream out;
	protected IFilter filterExtension;

	public AnalyzerFactory(
		Logger logger,
		int samplesPerSecond,
		OutputStream out,
		IFilter filterExtension)
	{
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
	protected void registerProtocols(ResourceResolver resourceResolver)
	{
		resourceResolver.registerProtocol("analyze", new PathResolverClass(AnalyzerFactory.class));
	}

	@Override
	protected String convertScript(String script) throws IOException
	{
		return resourceResolver.getResourceAsString("analyze://header.groovy") + System.lineSeparator() + script;
	}

}
