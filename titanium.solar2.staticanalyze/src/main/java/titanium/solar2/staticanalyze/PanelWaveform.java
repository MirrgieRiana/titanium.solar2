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
import mirrg.lithium.swing.util.SwingThreadUnsafe;

public class PanelWaveform extends JPanel
{

	private volatile boolean initialized = false;
	private BufferedImage image;
	private Graphics2D graphics;
	private ArrayDeque<Tuple<Double, Double>> ranges = new ArrayDeque<>(5000);
	private int position = 0;
	private double zoom = 1;

	private Object lock = new Object();

	public PanelWaveform()
	{
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e)
			{
				resetImage();
				drawAll();
			}

			@Override
			public void componentShown(ComponentEvent e)
			{
				resetImage();
				drawAll();
			}

			private void resetImage()
			{
				synchronized (lock) {
					initialized = true;
					image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
					graphics = image.createGraphics();
					if (position >= getWidth()) position = 0;
				}
			}
		});
	}

	//

	public void setZoom(double zoom)
	{
		synchronized (lock) {
			this.zoom = zoom;

			if (initialized) {
				drawAllImpl();
			}
		}

		repaint();
	}

	public void addEntry(double min, double max)
	{
		synchronized (lock) {
			synchronized (ranges) {
				ranges.addFirst(new Tuple<>(min, max));
				while (ranges.size() > 5000) {
					ranges.removeLast();
				}
			}

			if (initialized) {
				drawGraph(position, min, max);
				position++;
				if (position >= getWidth()) position = 0;
				drawCaret(position);
			}
		}

		repaint();
	}

	@Override
	public void paint(Graphics g)
	{
		synchronized (lock) {
			if (initialized) {
				g.drawImage(image, 0, 0, null);
			}
		}
	}

	public void drawAll()
	{
		synchronized (lock) {
			if (initialized) {
				drawAllImpl();
			}
		}

		repaint();
	}

	//

	private double minPrev = 0;
	private double maxPrev = 0;

	@SwingThreadUnsafe
	private void drawAllImpl()
	{
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, getWidth(), getHeight());

		minPrev = 0;
		maxPrev = 0;

		int p = position - 1;
		Iterator<Tuple<Double, Double>> iterator = ranges.iterator();
		for (int i = 0; i < getWidth() - 1; i++) {

			if (!iterator.hasNext()) break;
			Tuple<Double, Double> range = iterator.next();
			drawGraphLine(p, range.x, range.y);

			p--;
			if (p == -1) p = getWidth() - 1;
		}

		graphics.setColor(Color.red);
		graphics.fillRect(0, getHeight() / 2, getWidth(), 1);
		drawCaret(position);
	}

	@SwingThreadUnsafe
	private void drawGraph(int position, double min, double max)
	{
		graphics.setColor(Color.white);
		graphics.fillRect(position, 0, 1, getHeight());
		drawGraphLine(position, min, max);
		graphics.setColor(Color.red);
		graphics.fillRect(position, getHeight() / 2, 1, 1);
	}

	@SwingThreadUnsafe
	private void drawGraphLine(int position, double min, double max)
	{
		graphics.setColor(Color.black);
		int min2 = (int) (min * zoom * -1 * getHeight() / 256 + getHeight() / 2);
		int max2 = (int) (max * zoom * -1 * getHeight() / 256 + getHeight() / 2);
		int minPrev2 = (int) (minPrev * zoom * -1 * getHeight() / 256 + getHeight() / 2);
		int maxPrev2 = (int) (maxPrev * zoom * -1 * getHeight() / 256 + getHeight() / 2);
		if (min2 < maxPrev2) min2 = maxPrev2;
		if (max2 > minPrev2) max2 = minPrev2;
		graphics.fillRect(position, max2, 1, min2 - max2 + 1);

		minPrev = min;
		maxPrev = max;
	}

	@SwingThreadUnsafe
	private void drawCaret(int position)
	{
		graphics.setColor(Color.green);
		graphics.fillRect(position, 0, 1, getHeight());
	}

}
