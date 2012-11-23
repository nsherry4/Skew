package skew.core.model.impl;

import java.util.HashMap;
import java.util.Map;

public class HashMapSkewPoint<T> extends SkewPoint
{

	private Map<String, T> values;
	
	public HashMapSkewPoint(int x, int y, int index)
	{
		super(x, y, index);
		values = new HashMap<String, T>();
	}
	
	public void set(String key, T value)
	{
		values.put(key, value);
	}
	
	public T get(String key)
	{
		return values.get(key);
	}

}
