package titanium.solar2.libs.record.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import titanium.solar2.libs.record.core.Chunk;
import titanium.solar2.libs.record.util.AttributesBuilder;

public class ChunkRendererImageGraph
{

	public static void paint(Chunk chunk, BufferedImage image, double zoom)
	{
		Graphics2D g = image.createGraphics();
		g.setBackground(Color.white);
		g.clearRect(0, 0, image.getWidth(), image.getHeight());

		int w = image.getWidth();
		int h = image.getHeight();
		for (int x1 = 0; x1 < w; x1++) {
			int x2 = x1 + 1;

			double rate1 = (double) x1 / w;
			double rate2 = (double) x2 / w;

			int index1 = (int) (chunk.length * rate1);
			int index2 = (int) (chunk.length * rate2);
			if (index1 == index2) index2++;

			int min = 0;
			int max = 0;
			int sum = 0;
			int count = 0;
			for (int i = index1; i < index2; i++) {
				if (i >= chunk.length) break;
				if (min > chunk.buffer.array[i]) min = chunk.buffer.array[i];
				if (max < chunk.buffer.array[i]) max = chunk.buffer.array[i];
				sum += chunk.buffer.array[i];
				count++;
			}

			g.setColor(Color.black);
			{
				int l = (int) ((double) max / 128 * h / 2 * zoom);
				g.fillRect(x1, h / 2 - l, 1, l);
			}

			{
				int l = (int) ((double) min / 128 * h / 2 * zoom);
				g.fillRect(x1, h / 2, 1, -l);
			}

			g.setColor(Color.green);
			{
				double average = count == 0 ? 0 : (double) sum / count;
				int l = (int) (-average / 128 * h / 2 * zoom);
				g.fillRect(x1, h / 2 + l, 1, 1);
			}
		}

		g.setColor(Color.red);
		g.fillRect(0, h / 2, w, 1);

		g.setColor(Color.blue);
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
		g.drawString(AttributesRenderer.getString(new AttributesBuilder()
			.add("Length", "" + chunk.length)
			.add("Time", chunk.time.format(TimeRenderer.FORMATTER))
			.get()),
			2, 2 + (g.getFont().getSize() + 2) * 1);
		g.drawString(AttributesRenderer.getString(chunk.attributes),
			2, 2 + (g.getFont().getSize() + 2) * 2);
		g.drawString(AttributesRenderer.getString(chunk.getStatistics().getAttributes()),
			2, 2 + (g.getFont().getSize() + 2) * 3);
	}

}
