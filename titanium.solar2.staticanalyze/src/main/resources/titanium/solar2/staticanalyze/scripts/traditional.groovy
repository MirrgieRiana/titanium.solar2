// 従来手法の解析アルゴリズム
import titanium.solar2.libs.util.CRCUtil;
import titanium.solar2.libs.analyze.renderer.PacketRenderer;

double[] waveform = WaveformUtils.normalize(WaveformUtils.fromCSV(Main.getResourceAsURL("scripts/sample_waveform.csv")));
int waveformHotspot = 5;
int threshold = 21;

process(new Analyzer(), { a ->
	a.addListener(filterExtension);
	a.addListener(process(new PulseDetectorThresholdUp(threshold, 100), { p ->
		p.addListener(process(new PacketDetectorTraditional(60, 100), { p2 ->
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
								(byte) bs[2]) & 0xff) == bs[3]) {

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
