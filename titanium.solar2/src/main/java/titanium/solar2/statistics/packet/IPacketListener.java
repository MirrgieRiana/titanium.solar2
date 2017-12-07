package titanium.solar2.statistics.packet;

/**
 * {@link PacketsIterator} から通知されるPackerイベントのリスナです。
 */
public interface IPacketListener
{

	public void onPacket(Packet1 packet);

	public void onFinish();

}
