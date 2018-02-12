// 従来手法の解析アルゴリズム
import titanium.solar2.libs.util.CRCUtil;

int threshold = 21;

detector(new Analyzer(), { a ->
	a.addListener(filterExtension);
	a.addListener(detector(new PulseDetectorThresholdUp(threshold, 100), { p ->
		p.addListener(detector(new PacketDetectorTraditional(60, 100), { p2 ->
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
					logger.info("Packet Count: " + count);
				}
			});
		}));
	}));
});
