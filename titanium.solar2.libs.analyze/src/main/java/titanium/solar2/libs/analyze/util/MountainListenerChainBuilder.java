package titanium.solar2.libs.analyze.util;

import titanium.solar2.libs.analyze.mountainlisteners.chain.IChainListener;
import titanium.solar2.libs.analyze.mountainlisteners.chain.MountainListenerChain;

public class MountainListenerChainBuilder
{

	private final MountainListenerChain mountainListenerChain;

	public MountainListenerChainBuilder(MountainListenerChain mountainListenerChain)
	{
		this.mountainListenerChain = mountainListenerChain;
	}

	public MountainListenerChainBuilder addChainListener(IChainListener chainListener)
	{
		mountainListenerChain.addChainListener(chainListener);
		return this;
	}

	public MountainListenerChain get()
	{
		return mountainListenerChain;
	}

}
