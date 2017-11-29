package titanium.solar2.libs.analyze.util;

import titanium.solar2.libs.analyze.Analyzer;
import titanium.solar2.libs.analyze.IFilter;

public class AnalyzerBuilder
{

	private final Analyzer analyzer;

	public AnalyzerBuilder(Analyzer analyzer)
	{
		this.analyzer = analyzer;
	}

	public AnalyzerBuilder()
	{
		this(new Analyzer());
	}

	public AnalyzerBuilder addFilter(IFilter filter)
	{
		analyzer.addFilter(filter);
		return this;
	}

	public Analyzer get()
	{
		return analyzer;
	}

}
