// パルスリンク法解析アルゴリズム
import titanium.solar2.libs.util.CRCUtil;
import titanium.solar2.libs.analyze.renderer.PacketRenderer;

double[] waveform = WaveformUtils.normalize(WaveformUtils.fromCSV(Main.getResourceAsURL("scripts/sample_waveform.csv")));
int waveformHotspot = 5;
int threshold = 2000;
int firstThreshold = 3000;
int maxXOffsetError = 5;
double maxYRatioError = 2;
double shortRatio = 1;

process(new Analyzer(), { a ->
	a.addListener(filterExtension);
	a.addListener(new FilterCorrelation(waveform, waveformHotspot));
	a.addListener(process(new PulseDetectorThresholdHighest(threshold, 100), { p ->
		p.addListener(process(new PacketDetectorPulseLink(firstThreshold, 45, 80, 100, maxXOffsetError, maxYRatioError, shortRatio), { p2 ->
			p2.addListener(new IItemListener<Packet>() {
				private int count = 0;

				@Override
				public void onItem(Packet packet)
				{
					packet.getBytes().ifPresent({ bs ->
						if (bs.length == 5) {
							if ((CRCUtil.crc16(
								(byte) bs[0],
								(byte) bs[1],
								(byte) bs[2],
								(byte) bs[3],
								(byte) bs[4]) & 0xff) == bs[3] || true) {

								out.println(PacketRenderer.toString(packet, TimeRendererSimple.INSTANCE, samplesPerSecond));
								count++;

							}
						}
					});
				}

				@Override
				public void postAnalyze()
				{
					AnalyzeUtil.out.info("Packet Count: " + count);
				}
			});
		}));
	}));
});
