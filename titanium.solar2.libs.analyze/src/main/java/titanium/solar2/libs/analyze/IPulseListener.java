package titanium.solar2.libs.analyze;

import titanium.solar2.analyze.Pulse;

public interface IPulseListener extends IItemListener<Pulse>
{

	/**
	 * 開始からもしくは最後のパルスを出力してから一定時間経過したときに呼び出される。
	 *
	 * @param x
	 *            現在のサンプリング位置
	 */
	public void onTimeout(long x);

}
