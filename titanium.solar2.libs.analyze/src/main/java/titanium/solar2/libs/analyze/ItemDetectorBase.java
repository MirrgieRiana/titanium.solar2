package titanium.solar2.libs.analyze;

public abstract class ItemDetectorBase<I> extends DetectorBase<IItemListener<I>>
{

	protected void fireOnPacket(I item)
	{
		listeners.forEach(l -> l.onItem(item));
	}

}
