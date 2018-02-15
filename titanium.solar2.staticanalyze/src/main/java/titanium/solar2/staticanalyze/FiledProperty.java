package titanium.solar2.staticanalyze;

public class FiledProperty
{

	private String key;
	private String defaultValue;

	public FiledProperty(String key, String defaultValue)
	{
		this.key = key;
		this.defaultValue = defaultValue;
	}

	public String getKey()
	{
		return key;
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}

}
