package titanium.solar2.libs.record.plugins;

import java.awt.Canvas;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import titanium.solar2.libs.record.Recorder;
import titanium.solar2.libs.record.core.Buffer;
import titanium.solar2.libs.record.core.Chunk;
import titanium.solar2.libs.record.core.IPlugin;
import titanium.solar2.libs.record.core.RecoderEvent;
import titanium.solar2.libs.record.renderer.ChunkRendererImageGraph;

public class PluginGUI implements IPlugin
{

	public final double zoom;

	private JFrame frame;
	private Canvas canvas;

	private Buffer buffer = new Buffer(1);
	private Chunk entry;

	public PluginGUI(double zoom)
	{
		this.zoom = zoom;
	}

	@Override
	public void apply(Recorder recorder)
	{
		recorder.event().register(RecoderEvent.Start.class, e -> {
			frame = new JFrame();
			frame.setLayout(new CardLayout());

			canvas = new Canvas() {

				private BufferedImage image;

				@Override
				public void paint(Graphics g)
				{
					if (image == null || image.getWidth() != getWidth() || image.getHeight() != getHeight()) {
						image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
					}

					synchronized (PluginGUI.this) {
						if (entry != null) ChunkRendererImageGraph.paint(entry, image, zoom);
					}

					g.drawImage(image, 0, 0, null);
				}

			};
			canvas.setPreferredSize(new Dimension(1000, 256));
			frame.add(canvas);

			frame.pack();
			frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			frame.setVisible(true);
		});
		recorder.event().register(RecoderEvent.ProcessChunk.Consume.class, e -> {
			synchronized (PluginGUI.this) {
				if (buffer.array.length < e.chunk.length) {
					buffer.array = new byte[e.chunk.length];
				}
				System.arraycopy(e.chunk.buffer.array, 0, buffer.array, 0, e.chunk.length);
				entry = new Chunk(buffer, e.chunk.length, e.chunk.attributes, e.chunk.time);
			}
			canvas.repaint();
		});
		recorder.event().register(RecoderEvent.Destroy.class, e -> {
			SwingUtilities.invokeLater(() -> frame.dispose());
		});
	}

	@Override
	public String getName()
	{
		return "gui";
	}

}
