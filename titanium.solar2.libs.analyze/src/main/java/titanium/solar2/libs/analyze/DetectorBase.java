package titanium.solar2.libs.analyze;

import java.util.ArrayList;

public abstract class DetectorBase<L extends IAnalyzeListener> implements IAnalyzeListener, IDetector<L>
{

	protected ArrayList<L> listeners = new ArrayList<>();

	@Override
	public void addListener(L listener)
	{
		listeners.add(listener);
	}

	@Override
	public void preAnalyze()
	{
		listeners.forEach(l -> l.preAnalyze());
	}

	@Override
	public void postAnalyze()
	{
		listeners.forEach(l -> l.postAnalyze());
	}

}
