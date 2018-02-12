package titanium.solar2.libs.analyze;

import java.io.OutputStream;
import java.io.PrintStream;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import mirrg.lithium.logging.Logger;

public class AnalyzerFactory
{

	public static final ResourceResolver RESOURCE_RESOLVER_ANALYZE = new ResourceResolver(new PathResolverClass(AnalyzerFactory.class));

	public static Analyzer createAnalyzer(
		String script,
		ResourceResolver resourceResolver,
		Logger logger,
		int samplesPerSecond,
		OutputStream out,
		IFilter filterExtension) throws Exception
	{
		Binding binding = new Binding();
		binding.setVariable("context", resourceResolver);
		binding.setVariable("logger", logger);
		binding.setVariable("samplesPerSecond", samplesPerSecond);
		binding.setVariable("out", new PrintStream(out));
		binding.setVariable("filterExtension", filterExtension);
		String header = RESOURCE_RESOLVER_ANALYZE.getResourceAsString("header.groovy");
		String source = script;
		return (Analyzer) new GroovyShell(binding).evaluate(header + System.lineSeparator() + source);
	}

}
