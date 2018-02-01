package titanium.solar2.staticanalyze;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class FiledProperties
{

	private File file;

	private Properties propertiesDefault = new Properties();
	private Properties properties = new Properties();

	public FiledProperties(File file)
	{
		this.file = file;
	}

	public FiledProperties setDefault(String key, String value)
	{
		propertiesDefault.setProperty(key, value);
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

		for (String key : propertiesDefault.stringPropertyNames()) {
			if (!properties.containsKey(key)) {
				System.out.println(key);
				properties.setProperty(key, propertiesDefault.getProperty(key));
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

}
