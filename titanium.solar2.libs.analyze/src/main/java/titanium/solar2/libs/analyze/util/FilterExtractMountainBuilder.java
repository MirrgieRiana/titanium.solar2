package titanium.solar2.libs.analyze.util;

import titanium.solar2.libs.analyze.filters.mountain.FilterExtractMountain;
import titanium.solar2.libs.analyze.filters.mountain.IMountainListener;

public class FilterExtractMountainBuilder
{

	private final FilterExtractMountain filterExtractMountain;

	public FilterExtractMountainBuilder(FilterExtractMountain filterExtractMountain)
	{
		this.filterExtractMountain = filterExtractMountain;
	}

	public FilterExtractMountainBuilder addMountainListener(IMountainListener mountainListener)
	{
		filterExtractMountain.addMountainListener(mountainListener);
		return this;
	}

	public FilterExtractMountain get()
	{
		return filterExtractMountain;
	}

}
