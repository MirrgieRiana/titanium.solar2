package titanium.solar2.staticanalyze;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Properties;

public class FiledProperties
{

	private File file;

	private Hashtable<String, String> propertiesDefault = new Hashtable<>();
	private Properties properties = new Properties();

	public FiledProperties(File file)
	{
		this.file = file;
	}

	public FiledProperties setDefault(String key, String value)
	{
		propertiesDefault.put(key, value);
		return this;
	}

	public void init()
	{
		if (file.exists()) {
			try {
				properties.load(new FileInputStream(file));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		for (Entry<String, String> entry : propertiesDefault.entrySet()) {
			if (!properties.containsKey(entry.getKey())) {
				properties.setProperty(entry.getKey(), entry.getValue());
			}
		}

		save();
	}

	public void save()
	{
		try (FileOutputStream out = new FileOutputStream(file)) {
			properties.store(out, "AutoSave");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String get(String key)
	{
		return properties.getProperty(key);
	}

	public void set(String key, String value)
	{
		properties.setProperty(key, value);
		save();
	}

	public void reset(String key)
	{
		if (propertiesDefault.containsKey(key)) {
			set(key, propertiesDefault.get(key));
		} else {
			properties.remove(key);
			save();
		}
	}

}
