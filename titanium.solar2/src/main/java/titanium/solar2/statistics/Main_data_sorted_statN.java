package titanium.solar2.statistics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.StatUtils;

import titanium.solar2.libs.time.timerenderers.TimeRendererSimple;
import titanium.solar2.statistics.packet.PacketsIterator;
import titanium.solar2.statistics.packetlisteners.timearea.PacketListenerTimeArea;
import titanium.solar2.statistics.timearealisteners.TimeAreaListenerFilter;
import titanium.solar2.statistics.timearealisteners.TimeAreaListenerLogPerHour;
import titanium.solar2.statistics.timearealisteners.TimeAreaListenerPacketStatisticsPerID;
import titanium.solar2.statistics.timeareaproviders.TimeAreaProviderPer30s;

public class Main_data_sorted_statN
{

	public static void main(String[] args) throws Exception
	{
		File dir = new File("H:\\amyf\\jesqenvina\\xa0\\kenkyuu\\data\\recorder_kisarazu_20170530");
		File src = new File(dir, "5_extract\\data_sorted.csv");
		File dst1 = new File(dir, "5_extract\\data_sorted_stat1.csv");
		File dst2 = new File(dir, "5_extract\\data_sorted_stat2.csv");
		File dst3 = new File(dir, "5_extract\\data_sorted_stat3.csv");

		try (PrintStream out1 = new PrintStream(new FileOutputStream(dst1));
			PrintStream out2 = new PrintStream(new FileOutputStream(dst2));
			PrintStream out3 = new PrintStream(new FileOutputStream(dst3))) {
			PacketListenerTimeArea packetListener = new PacketListenerTimeArea(new TimeAreaProviderPer30s());
			packetListener.addTimeAreaListener(new TimeAreaListenerFilter(
				ta -> 5 <= ta.getHour() && ta.getHour() < 20,
				new TimeAreaListenerVoltageStatistics(out1)));
			packetListener.addTimeAreaListener(new TimeAreaListenerVoltageStatistics(out2));
			packetListener.addTimeAreaListener(new TimeAreaListenerVoltageStatisticsExtra(out3));
			packetListener.addTimeAreaListener(new TimeAreaListenerLogPerHour());
			new PacketsIterator(src, TimeRendererSimple.INSTANCE).iterate(packetListener);
		}
	}

	private static class TimeAreaListenerVoltageStatistics extends TimeAreaListenerPacketStatisticsPerID
	{

		public TimeAreaListenerVoltageStatistics(PrintStream out)
		{
			super(out);
		}

		@Override
		public void terminateTimeArea(LocalDateTime timeArea)
		{
			for (int id = 1; id <= 48; id++) {
				double[] voltages = packetTable.get(id).stream()
					.mapToDouble(p -> p.voltage)
					.toArray();

				out.println(getString(id, timeArea, voltages));
			}
		}

		protected String getString(int id, LocalDateTime timeArea, double[] voltages)
		{
			return String.format("%2d,%s,%3d,%3d",
				id,
				TimeRendererSimple.INSTANCE.format(timeArea),
				(int) StatUtils.percentile(voltages, 50),
				packetTable.get(id).size());
		}

	}

	private static class TimeAreaListenerVoltageStatisticsExtra extends TimeAreaListenerVoltageStatistics
	{

		public TimeAreaListenerVoltageStatisticsExtra(PrintStream out)
		{
			super(out);
		}

		@Override
		protected String getString(int id, LocalDateTime timeArea, double[] voltages)
		{
			return super.getString(id, timeArea, voltages) + "," + String.format("%5s,%5s,%s",
				toString(StatUtils.mean(voltages)),
				toString(StatUtils.variance(voltages)),
				ranking(packetTable.get(id).stream()
					.mapToInt(p -> p.voltage)
					.toArray()));
		}

		private String toString(double value)
		{
			return Double.isNaN(value) ? "-" : String.format("%5.2f", value);
		}

		private String ranking(int[] voltages)
		{
			Hashtable<Integer, Integer> table = new Hashtable<>();
			for (int i = 0; i < voltages.length; i++) {
				int voltage = voltages[i];

				if (!table.containsKey(voltage)) table.put(voltage, 0);

				table.put(voltage, table.get(voltage) + 1);
			}
			return table.entrySet().stream()
				.sorted((a, b) -> b.getValue().compareTo(a.getValue()))
				.map(e -> e.getKey() + "x" + e.getValue())
				.collect(Collectors.joining(";"));
		}

	}

}
