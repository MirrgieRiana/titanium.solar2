package titanium.solar2.libs.analyze.util;

import titanium.solar2.libs.analyze.IDetector;

public class DetectorBuilder<D extends IDetector<L>, L>
{

	private final D detector;

	public DetectorBuilder(D detector)
	{
		this.detector = detector;
	}

	public DetectorBuilder<D, L> addListener(L listener)
	{
		detector.addListener(listener);
		return this;
	}

	public D get()
	{
		return detector;
	}

}
