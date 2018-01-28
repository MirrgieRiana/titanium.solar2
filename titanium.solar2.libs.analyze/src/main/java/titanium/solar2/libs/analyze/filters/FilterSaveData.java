package titanium.solar2.libs.analyze.filters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import mirrg.lithium.struct.Struct1;
import titanium.solar2.libs.analyze.IFilter;
import titanium.solar2.libs.time.ITimeRenderer;

/**
 * データをビッグエンディアンで保存する。
 */
public class FilterSaveData implements IFilter
{

	private File dir;
	private ITimeRenderer timeRenderer;
	private String fileNameformat;

	private FileOutputStream out;

	/**
	 * @param dir
	 * @param timeRenderer
	 * @param fileNameformat
	 *            例："%s.dat"
	 */
	public FilterSaveData(File dir, ITimeRenderer timeRenderer, String fileNameformat)
	{
		this.dir = dir;
		this.timeRenderer = timeRenderer;
		this.fileNameformat = fileNameformat;
	}

	@Override
	public void preChunk(LocalDateTime time)
	{
		try {
			File file = new File(dir, String.format(fileNameformat, timeRenderer.format(time)));
			file.getAbsoluteFile().getParentFile().mkdirs();
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void processData(double[] buffer, int length, Struct1<Double> sOffset)
	{
		for (int i = 0; i < length; i++) {
			try {
				short j = (short) (buffer[i]);
				out.write((j >> 8) & 0xff);
				out.write(j & 0xff);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void postChunk()
	{
		try {
			out.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
