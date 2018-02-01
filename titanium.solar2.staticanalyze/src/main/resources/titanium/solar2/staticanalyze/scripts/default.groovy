// グラフの表示だけを行う解析機

process(new Analyzer(), { a ->
	a.addListener(filterExtension);
})
