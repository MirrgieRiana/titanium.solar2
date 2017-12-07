package titanium.solar2.libs.record.plugins;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import titanium.solar2.libs.record.Recorder;
import titanium.solar2.libs.record.core.Chunk;
import titanium.solar2.libs.record.core.IPlugin;
import titanium.solar2.libs.record.core.RecoderEvent;
import titanium.solar2.libs.record.renderer.ChunkRendererImageGraph;
import titanium.solar2.libs.record.renderer.ChunkRendererStringGraph;
import titanium.solar2.libs.time.ITimeRenderer;

public class PluginSaveArchive implements IPlugin
{

	public final File dir;
	public final String patternDir;
	public final String patternZip;
	public final int imageWidth;
	public final int imageHeight;
	public final int stringGraphLength;
	public final double stringGraphZoom;
	public final ITimeRenderer timeRenderer;

	private BufferedImage image;
	private DateTimeFormatter formatterDir;
	private DateTimeFormatter formatterZip;

	private ZipOutputStream zipOutputStream = null;
	private PrintStream stringGraphPrintStream = null;
	private int indexInZip = 0;

	public PluginSaveArchive(
		File dir,
		String patternDir,
		String patternZip,
		int imageWidth,
		int imageHeight,
		int stringGraphLength,
		double stringGraphZoom,
		ITimeRenderer timeRenderer)
	{
		this.dir = dir;
		this.patternDir = patternDir;
		this.patternZip = patternZip;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.stringGraphLength = stringGraphLength;
		this.stringGraphZoom = stringGraphZoom;
		this.timeRenderer = timeRenderer;
	}

	@Override
	public void apply(Recorder recorder)
	{
		image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		formatterDir = DateTimeFormatter.ofPattern(patternDir);
		formatterZip = DateTimeFormatter.ofPattern(patternZip);

		recorder.event().register(RecoderEvent.Ready.class, e -> {
			System.err.println("OutputDir:" + dir);
		});
		recorder.event().registerThrowable(RecoderEvent.ProcessChunk.Consume.class, e -> {

			// prepare stream
			prepareStream(e.chunk.time);

			// write entry
			{
				String entryNameBase = String.format("%05d-%s", indexInZip, timeRenderer.format(e.chunk.time));

				saveData(e.chunk, entryNameBase + ".dat");
				saveImage(e.chunk, entryNameBase + ".png");

				stringGraphPrintStream.println(String.format("%05d %s %s",
					indexInZip,
					ChunkRendererStringGraph.getStringGraph(e.chunk, stringGraphLength, stringGraphZoom),
					e.chunk.getStatistics().noiz));

				e.attributesBuilder.add("Index", "" + indexInZip);
				e.attributesBuilder.add("Name", entryNameBase);

				indexInZip++;
			}

		});
		recorder.event().register(RecoderEvent.Destroy.class, e -> {
			close();
		});
	}

	@Override
	public String getName()
	{
		return "saveArchive";
	}

	//

	private String lastArchiveNameBase = null;

	private void prepareStream(LocalDateTime time) throws FileNotFoundException
	{
		String archiveNameBase = String.format("%s/%s",
			time.format(formatterDir),
			time.format(formatterZip));
		if (lastArchiveNameBase == null || !lastArchiveNameBase.equals(archiveNameBase)) {

			// stop
			close();

			// start
			new File(dir, archiveNameBase).getParentFile().mkdirs();
			zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(new File(dir, archiveNameBase + ".zip"))));
			stringGraphPrintStream = new PrintStream(new FileOutputStream(new File(dir, archiveNameBase + ".txt")));

			// on change archive
			indexInZip = 0;
			System.out.println("Changed Archive: " + archiveNameBase);

		}
		lastArchiveNameBase = archiveNameBase;
	}

	private synchronized void close()
	{
		if (zipOutputStream == null) return;

		try {
			zipOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		zipOutputStream = null;

		stringGraphPrintStream.close();
		System.err.println("Closed Zip");
	}

	private synchronized void saveData(Chunk entry, String entryNameBase) throws IOException
	{
		if (zipOutputStream == null) return;

		zipOutputStream.putNextEntry(new ZipEntry(entryNameBase));
		zipOutputStream.write(entry.buffer.array, 0, entry.length);
	}

	private synchronized void saveImage(Chunk entry, String entryNameBase) throws IOException
	{
		if (zipOutputStream == null) return;

		zipOutputStream.putNextEntry(new ZipEntry(entryNameBase));
		ChunkRendererImageGraph.paint(entry, image, timeRenderer, 1);
		ImageIO.write(image, "png", zipOutputStream);
	}

}
