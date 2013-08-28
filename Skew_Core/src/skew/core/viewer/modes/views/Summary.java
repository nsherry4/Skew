package skew.core.viewer.modes.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Summary
{

	private List<String> headers = new ArrayList<>();
	private Map<String, String> values = new LinkedHashMap<>();
	private String title;
	
	public Summary(String title) {
		this.title = title;
	}
	
	public void addHeader(String header) {
		headers.add(header);
	}
	
	public void addHeader(String... hs) {
		headers.addAll(Arrays.asList(hs));
		
	}
	
	public void addValue(String header, String value) {
		values.put(header, value);
	}
	
	public String getTitle(){
		return title;
	}
	
	public List<String> getHeaders() {
		return headers;
	}
	
	public Map<String, String> getValues() {
		return values;
	}
	
}
