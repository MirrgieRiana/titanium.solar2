package titanium.solar2.staticanalyze;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public interface IGraphDestination
{

	public BufferedImage getImage();

	public Graphics2D getGraphics();

	public void repaint();

}
