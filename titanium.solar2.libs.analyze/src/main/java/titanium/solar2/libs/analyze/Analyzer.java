package titanium.solar2.libs.analyze;

import java.time.LocalDateTime;
import java.util.ArrayList;

import mirrg.lithium.struct.Struct1;

public class Analyzer
{

	private ArrayList<IFilter> filters = new ArrayList<>();

	public void addFilter(IFilter filter)
	{
		filters.add(filter);
	}

	public void startChunk(LocalDateTime time)
	{
		for (IFilter filter : filters) {
			filter.startChunk(time);
		}
	}

	public void processData(double[] buffer, int length, Struct1<Double> sOffset)
	{
		for (IFilter filter : filters) {
			filter.processData(buffer, length, sOffset);
		}
	}

}
