package titanium.solar2.libs.analyze;

import java.io.OutputStream;
import java.io.PrintStream;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import mirrg.lithium.logging.Logger;

public class AnalyzerFactory
{

	public static final IResourceProvider RESOURCE_PROVIDER = new ResourceProviderFromClass(AnalyzerFactory.class);

	public static Analyzer createAnalyzer(
		String groovySrc,
		IResourceProvider context,
		Logger logger,
		int samplesPerSecond,
		OutputStream out,
		IFilter filterExtension) throws Exception
	{
		Binding binding = new Binding();
		binding.setVariable("context", context);
		binding.setVariable("logger", logger);
		binding.setVariable("samplesPerSecond", samplesPerSecond);
		binding.setVariable("out", new PrintStream(out));
		binding.setVariable("filterExtension", filterExtension);
		String header = RESOURCE_PROVIDER.getResourceAsString("header.groovy");
		return (Analyzer) new GroovyShell(binding).evaluate(header + System.lineSeparator() + groovySrc);
	}

}
