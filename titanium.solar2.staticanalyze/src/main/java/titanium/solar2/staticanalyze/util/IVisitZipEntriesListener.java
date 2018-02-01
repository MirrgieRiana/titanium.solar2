package titanium.solar2.staticanalyze.util;

import java.io.InputStream;
import java.util.zip.ZipEntry;

public interface IVisitZipEntriesListener
{

	public void onEntry(ZipEntry zipEntry, InputStream in) throws InterruptedException;

}