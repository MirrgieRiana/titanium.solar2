package titanium.solar2.libs.analyze.filters.mountain;

public interface IMountainListener
{

	public void onMountain(Mountain mountain);

	/**
	 * 最後の山からx時間経過したときに呼び出される。
	 */
	public void onTimeout(long x);

}
