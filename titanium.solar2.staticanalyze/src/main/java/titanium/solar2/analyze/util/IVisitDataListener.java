package titanium.solar2.analyze.util;

import java.io.File;
import java.util.zip.ZipEntry;

public interface IVisitDataListener
{

	public default void preFile(File zipFile, int i, int count)
	{

	}

	/**
	 * @return このエントリーを処理するか否か
	 */
	public default boolean preEntry(ZipEntry zipEntry)
	{
		return true;
	}

	public default void onData(byte[] buffer, int start, int length)
	{

	}

	public default void postEntry(ZipEntry zipEntry)
	{

	}

	public default void postFile(File zipFile, int i, int count)
	{

	}

}
