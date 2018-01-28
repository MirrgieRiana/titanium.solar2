package titanium.solar2.libs.analyze;

import java.time.LocalDateTime;

import mirrg.lithium.struct.Struct1;

public class Analyzer extends DetectorBase<IFilter>
{

	public void preChunk(LocalDateTime time)
	{
		listeners.forEach(l -> l.preChunk(time));
	}

	public void processData(double[] buffer, int length, Struct1<Double> sOffset)
	{
		listeners.forEach(l -> l.processData(buffer, length, sOffset));
	}

	public void postChunk()
	{
		listeners.forEach(l -> l.postChunk());
	}

}
