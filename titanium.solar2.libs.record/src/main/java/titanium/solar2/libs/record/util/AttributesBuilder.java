package titanium.solar2.libs.record.util;

import java.util.ArrayList;

import mirrg.lithium.struct.ImmutableArray;
import titanium.solar2.libs.record.core.Attribute;
import titanium.solar2.libs.record.core.Attributes;

public class AttributesBuilder
{

	private ArrayList<Attribute> attributes = new ArrayList<>();

	public AttributesBuilder add(Attribute attribute)
	{
		attributes.add(attribute);
		return this;
	}

	public AttributesBuilder add(String key, String value)
	{
		return add(new Attribute(key, value));
	}

	public AttributesBuilder add(String key, String format, Object value)
	{
		return add(new Attribute(key, String.format(format, value)));
	}

	public AttributesBuilder add(Attributes attributes)
	{
		attributes.attributes.forEach(this.attributes::add);
		return this;
	}

	public Attributes get()
	{
		return new Attributes(new ImmutableArray<>(attributes));
	}

}
