// サンプル
import java.time.LocalDateTime
import mirrg.lithium.struct.Struct1

//このスクリプトでは以下のパッケージが暗黙にインポートされます。
//import titanium.solar2.analyze.*
//import titanium.solar2.analyze.listeners.*
//import titanium.solar2.libs.analyze.*
//import titanium.solar2.libs.analyze.util.*
//import titanium.solar2.libs.time.*
//import titanium.solar2.libs.time.timerenderers.*

// processは第一引数を返すメソッドです。
// 第一引数のオブジェクトを第二引数のクロージャに対して与えて実行します。
process(new Analyzer(), { a ->

	// グラフの表示やGUIの画面更新などを行います。
	// CUIで動作する場合、このフィルタは何も行いません。
	a.addListener(filterExtension);

	// 匿名クラスによりフィルタを自作することができます。
	// ただし、Groovyによる各サンプルへの演算は低速なため非推奨です。
	a.addListener(new IFilter() {

		/**
		 * 解析の開始時に飛び出されます。
		 */
		@Override
		public void preAnalyze()
		{
			// 解析結果とは別にログ出力を行います。
			context.getLogger().info("Start");
		}

		/**
		 * 新しいチャンクの処理の開始を宣言します。
		 *
		 * @param time
		 *            このチャンクの開始位置の時刻を設定します。
		 */
		@Override
		public void preChunk(LocalDateTime time)
		{

			// サンプルの時刻を取得する例です。
			// int型組み込み変数のsamplesPerSecondにより
			// フォームなどによって指定されたサンプリングレートを取得できます。
			LocalDateTime time2 = TimeUtil.getTime(time, 0, samplesPerSecond);

			// 時刻の文字列表現を取得する例です。
			String time3 = TimeRendererSimple.INSTANCE.format(time2);

			// 解析結果はPrintStream型組み込み変数のoutに対して出力します。
			out.println(time3 + "," + x);

		}

		private long x = 0;

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
		@Override
		public void processData(double[] buffer, int length, Struct1<Double> sOffset)
		{
			// ここでbufferの各要素に対して変更を加えることができますが、
			// Groovyでの実装は低速となるため非推奨です。
			for (int i = 0; i < length; i++) {
				//buffer[i] = -buffer[i];
			}
			x += length;
		}

		/**
		 * チャンクの終了時に呼ばれます。
		 */
		@Override
		public void postChunk()
		{

		}

		/**
		 * 解析の終了時に飛び出されます。
		 */
		@Override
		public void postAnalyze()
		{
			context.getLogger().info("Finish");
		}

	});

})
