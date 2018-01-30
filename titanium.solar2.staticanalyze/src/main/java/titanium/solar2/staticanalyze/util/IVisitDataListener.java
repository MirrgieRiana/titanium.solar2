package titanium.solar2.staticanalyze.util;

import java.io.File;
import java.time.LocalDateTime;

public interface IVisitDataListener
{

	/**
	 * ファイルが処理される前に呼び出される。
	 * ZIPファイルとdatファイルの両方で呼び出される。
	 */
	public default void preFile(File file, EnumDataFileType dataFileType, int fileIndex, int fileCount)
	{

	}

	/**
	 * 処理前にZIPファイルのエントリー名もしくはdatファイルのパス名が通知される。
	 */
	public default void preEntry(String entryName, LocalDateTime time)
	{

	}

	/**
	 * データが渡される。一つのエントリーに対して複数回呼び出されることがある。
	 */
	public default void onData(byte[] buffer, int start, int length)
	{

	}

	/**
	 * エントリー処理の終了時に呼び出される。
	 */
	public default void postEntry()
	{

	}

	/**
	 * 受理できないエントリーを発見した際に呼び出される。
	 */
	public default void ignoreEntry(String entryName)
	{

	}

	/**
	 * ファイル処理の終了時に呼び出される。
	 */
	public default void postFile()
	{

	}

	/**
	 * 受理できないファイルを発見した際に呼び出される。
	 */
	public default void ignoreFile(File file)
	{

	}

}
