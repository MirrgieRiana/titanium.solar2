package titanium.solar2.analyze.util;

import java.io.InputStream;
import java.util.zip.ZipEntry;

public interface IVisitZipEntriesListener
{

	public void onEntry(ZipEntry zipEntry, InputStream in);

}
