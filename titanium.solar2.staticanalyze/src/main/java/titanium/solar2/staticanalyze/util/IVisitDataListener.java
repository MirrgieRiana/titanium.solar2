package titanium.solar2.staticanalyze.util;

import java.io.File;
import java.time.LocalDateTime;

public interface IVisitDataListener
{

	/**
	 * ファイルが処理される前に呼び出されます。
	 * このメソッドはZIPファイルとdatファイルの両方で呼び出されます。
	 *
	 * @param fileIndex
	 *            1から始まる解析全体の中でのファイルの番号
	 */
	public default void preFile(File file, EnumDataFileType dataFileType, int fileIndex, int fileCount)
	{

	}

	/**
	 * 処理前にZIPファイルのエントリー名もしくはdatファイルのパス名が通知されます。
	 *
	 * @param entryIndex
	 *            1から始まるファイルの中でのエントリーの番号
	 */
	public default void preEntry(String entryName, LocalDateTime time, int entryIndex, int entryCount)
	{

	}

	/**
	 * データが渡されます。一つのエントリーに対して複数回呼び出されることがあります。
	 */
	public default void onData(byte[] buffer, int start, int length) throws InterruptedException
	{

	}

	/**
	 * エントリー処理の終了時に呼び出されます。
	 * {@link #onData(byte[], int, int)} がスレッドの中断によって終了された場合も呼び出されます。
	 */
	public default void postEntry()
	{

	}

	/**
	 * 受理できないエントリーを発見した際に呼び出されます。
	 */
	public default void ignoreEntry(String entryName, int entryIndex, int entryCount)
	{

	}

	/**
	 * ファイル処理の終了時に呼び出されます。
	 * {@link #onData(byte[], int, int)} がスレッドの中断によって終了された場合も呼び出されます。
	 */
	public default void postFile()
	{

	}

	/**
	 * 受理できないファイルを発見した際に呼び出されます。
	 *
	 * @param fileIndex
	 *            1から始まるファイルの中でのエントリーの番号
	 */
	public default void ignoreFile(File file, int fileIndex, int fileCount)
	{

	}

}
