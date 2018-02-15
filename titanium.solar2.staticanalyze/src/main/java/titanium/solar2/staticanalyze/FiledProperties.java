package titanium.solar2.staticanalyze;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class FiledProperties
{

	private File file;

	private Properties properties = new Properties();

	public FiledProperties(File file)
	{
		this.file = file;
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

	public String get(FiledProperty property)
	{
		if (!properties.containsKey(property.getKey())) set(property, property.getDefaultValue());
		return properties.getProperty(property.getKey());
	}

	public void set(FiledProperty property, String value)
	{
		properties.setProperty(property.getKey(), value);
		save();
	}

	public void reset(FiledProperty property)
	{
		set(property, property.getDefaultValue());
	}

}
