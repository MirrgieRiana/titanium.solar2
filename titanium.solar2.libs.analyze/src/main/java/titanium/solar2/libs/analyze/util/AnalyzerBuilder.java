package titanium.solar2.libs.analyze.util;

import titanium.solar2.libs.analyze.Analyzer;
import titanium.solar2.libs.analyze.IFilter;

public class AnalyzerBuilder extends DetectorBuilder<Analyzer, IFilter>
{

	public AnalyzerBuilder(Analyzer analyzer)
	{
		super(analyzer);
	}

	public AnalyzerBuilder()
	{
		this(new Analyzer());
	}

}
