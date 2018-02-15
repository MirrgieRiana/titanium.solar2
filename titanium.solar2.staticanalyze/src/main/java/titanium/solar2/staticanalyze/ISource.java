package titanium.solar2.staticanalyze;

import java.awt.Component;
import java.io.IOException;

import titanium.solar2.libs.analyze.Analyzer;

public interface ISource
{

	public Component getComponent();

	public void setEnabled(boolean enabled);

	public void doAnalyze(Analyzer analyzer) throws IOException, InterruptedException;

}
