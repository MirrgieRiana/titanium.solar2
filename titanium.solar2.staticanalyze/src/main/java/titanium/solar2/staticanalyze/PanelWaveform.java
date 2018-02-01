package titanium.solar2.staticanalyze;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Iterator;

import javax.swing.JPanel;

import mirrg.lithium.struct.Tuple;

public class PanelWaveform extends JPanel
{

	private volatile boolean initialized = false;
	private BufferedImage image;
	private Graphics2D graphics;
	private ArrayDeque<Tuple<Double, Double>> ranges = new ArrayDeque<>(5000);
	private int position = 0;
	private double zoom = 1;

	public PanelWaveform()
	{
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e)
			{
				initImage();
				if (position >= getWidth()) position = 0;
				draw();
				repaint();
			}

			@Override
			public void componentShown(ComponentEvent e)
			{
				initImage();
				if (position >= getWidth()) position = 0;
				draw();
				repaint();
			}
		});
	}

	@Override
	public void paint(Graphics g)
	{
		if (!initialized) return;

		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}
	}

	private void initImage()
	{
		initialized = true;
		image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		graphics = image.createGraphics();
	}

	public void setZoom(double zoom)
	{
		this.zoom = zoom;
		draw();
	}

	public void addEntry(double min, double max)
	{
		synchronized (ranges) {
			ranges.addFirst(new Tuple<>(min, max));
			while (ranges.size() > 5000) {
				ranges.removeLast();
			}
		}

		if (!initialized) return;

		drawGraph(position, min, max);
		position++;
		if (position >= getWidth()) position = 0;
		drawCaret(position);
		repaint();
	}

	private void draw()
	{
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, getWidth(), getHeight());

		synchronized (ranges) {
			int p = position - 1;
			Iterator<Tuple<Double, Double>> iterator = ranges.iterator();
			for (int i = 0; i < getWidth() - 1; i++) {

				if (!iterator.hasNext()) break;
				Tuple<Double, Double> range = iterator.next();
				drawGraphLine(p, range.x, range.y);

				p--;
				if (p == -1) p = getWidth() - 1;
			}
		}

		graphics.setColor(Color.red);
		graphics.fillRect(0, getHeight() / 2, getWidth(), 1);
		drawCaret(position);
	}

	private void drawGraph(int position, double min, double max)
	{
		graphics.setColor(Color.white);
		graphics.fillRect(position, 0, 1, getHeight());
		drawGraphLine(position, min, max);
		graphics.setColor(Color.red);
		graphics.fillRect(position, getHeight() / 2, 1, 1);
	}

	private void drawGraphLine(int position, double min, double max)
	{
		graphics.setColor(Color.black);
		int min2 = (int) (min * zoom * -1 * getHeight() / 256 + getHeight() / 2);
		int max2 = (int) (max * zoom * -1 * getHeight() / 256 + getHeight() / 2);
		graphics.fillRect(position, max2, 1, min2 - max2);
	}

	private void drawCaret(int position)
	{
		graphics.setColor(Color.green);
		graphics.fillRect(position, 0, 1, getHeight());
	}

}
