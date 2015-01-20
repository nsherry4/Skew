package skew.core.viewer.modes.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Summary
{

	private List<String> canonicalKeys = new ArrayList<>();
	private Map<String, String> values = new LinkedHashMap<>();
	private String name;
	
	public Summary(String name) {
		this.name = name;
	}
	
	public Summary(String name, String... canonicalKeys) {
		this(name);
		addCanonicalKeys(canonicalKeys);
	}
	
	/**
	 * Registers a key as canonical, meaning that it will be included (even when absent) when this data is printed in tabular form.
	 * @param key
	 */
	public void addCanonicalKey(String key) {
		canonicalKeys.add(key);
	}
	
	/**
	 * Convenience function for {@link #addCanonicalKey(String)}
	 * @param keys
	 */
	public void addCanonicalKeys(String... keys) {
		canonicalKeys.addAll(Arrays.asList(keys));
		
	}
	
	public void addCanonicalValue(String key, String value) {
		addValue(key, value);
		addCanonicalKey(key);
	}
	
	public void addValue(String key, String value) {
		values.put(key, value);
	}
	
	public String getName(){
		return name;
	}
	
	public List<String> getCanonicalKeys() {
		return canonicalKeys;
	}
	
	public Map<String, String> getValues() {
		return values;
	}
	
}
