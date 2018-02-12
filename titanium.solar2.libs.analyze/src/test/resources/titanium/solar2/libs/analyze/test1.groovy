// テスト
detector (new Analyzer()) {
	it.addListener filterExtension
	it.addListener new FilterCorrelation(WaveformUtil.fromCSV(context.getResourceAsURL("waveform.csv")), 5)
	it.addListener new FilterContinuous(45, 80)
	it.addListener new FilterQOM()
	it.addListener new FilterMul(0.02)
	it.addListener new FilterFatten(7)
	it.addListener detector (new PulseDetectorThresholdHighest(10, 100)) {
		it.addListener detector (new PacketDetectorPulseLink(30, 45, 80, 100, 5, 2, 1)) {
			it.addListener ({ packet ->
				out.println String.format("%3d %6x %s",
						packet.pulses.length(),
						packet.getFirstPulse().x,
						PacketRenderer.toString(packet, TimeRendererSimple.INSTANCE, samplesPerSecond))
			} as IItemListener)
		}
	}
}
