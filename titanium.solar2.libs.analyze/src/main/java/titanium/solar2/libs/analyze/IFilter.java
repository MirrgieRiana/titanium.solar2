package titanium.solar2.libs.analyze;

import java.time.LocalDateTime;

import mirrg.lithium.struct.Struct1;

public interface IFilter
{

	/**
	 * 新しいチャンクの処理の開始を宣言します。
	 *
	 * @param time
	 *            このチャンクの開始位置の時刻を設定します。
	 */
	public default void startChunk(LocalDateTime time)
	{

	}

	/**
	 * 指定の長さの入力データを受理します。
	 * チャンクのデータは複数の断片に分割されている可能性があり、
	 * 各サンプルの時刻を得るにはチャンクの先頭の時刻・チャンク内の位置・サンプリングレートから
	 * 求める必要があります。
	 *
	 * @param buffer
	 *            入力データが入ったバッファです。
	 *            この引数は破壊的であり、データは別のフィルタによって既に変換済みである可能性があります。
	 * @param length
	 *            bufferの有効データ長です。
	 * @param sOffset
	 *            このフィルタの適用によって生じるデータの遅延を加算するためのバッファです。
	 *            加算する数値は正でなければなりません。
	 */
	public void processData(double[] buffer, int length, Struct1<Double> sOffset);

}
