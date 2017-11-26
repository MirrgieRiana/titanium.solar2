package titanium.solar2.libs.record.renderer;

import titanium.solar2.libs.record.core.Attributes;

public class AttributesRenderer
{

	public static String getString(Attributes attributes)
	{
		StringBuilder sb = new StringBuilder();

		attributes.attributes.forEach(a -> {
			if (sb.length() != 0) sb.append(";");
			sb.append(a.key);
			sb.append(":");
			sb.append(a.value);
		});

		return sb.toString();
	}

}
