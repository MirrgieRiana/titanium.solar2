// 存在検知を行う解析アルゴリズム

double[] waveform = WaveformUtil.normalize(WaveformUtil.fromCSV(context.getResourceAsURL("sample_waveform.csv")));
int waveformHotspot = 5;
int width = 200;
int threshold = 1900;
int minLength = 80 * 4 + 45 * 40;
int maxLength = 80 * 4 + 80 * 40 + 45 + width;

detector(new Analyzer(), { a ->
	a.addListener(filterExtension);
	a.addListener(new FilterCorrelation(waveform, waveformHotspot));
	a.addListener(new FilterFatten(width));
	a.addListener(detector(new PeriodDetectorThreshold(threshold), { p ->
		p.addListener(new IItemListener<Period>() {
			private int count = 0;

			@Override
			public void onItem(Period period)
			{
				if (minLength <= period.getLength() && period.getLength() <= maxLength) {
					out.println(PeriodRenderer.toString(period, TimeRendererSimple.INSTANCE, samplesPerSecond));
					count++;
				}
			}

			@Override
			public void postAnalyze()
			{
				logger.info("Period Count: " + count);
			}
		});
	}));
});
