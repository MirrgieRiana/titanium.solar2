package titanium.solar2.staticanalyze;

import java.awt.CardLayout;
import java.awt.Container;

import javax.swing.JFrame;

public class ContainerSource extends Container
{

	public final ISource source;

	public ContainerSource(ISource source, JFrame frame)
	{
		this.source = source;

		setLayout(new CardLayout());
		add(source.getComponent(frame));
	}

}
