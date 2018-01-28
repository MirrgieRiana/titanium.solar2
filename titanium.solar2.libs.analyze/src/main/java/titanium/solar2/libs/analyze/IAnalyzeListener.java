package titanium.solar2.libs.analyze;

/**
 * あらゆる解析アイテムのリスナーの根底。
 */
public interface IAnalyzeListener
{

	/**
	 * 解析の開始時に飛び出されます。
	 */
	public default void preAnalyze()
	{

	}

	/**
	 * 解析の終了時に飛び出されます。
	 */
	public default void postAnalyze()
	{

	}

}
