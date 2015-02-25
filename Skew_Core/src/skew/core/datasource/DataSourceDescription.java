package skew.core.datasource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataSourceDescription {

	private List<String> extensions;
	private String summary;
	private String title;
	
	public DataSourceDescription(String title, String summary, List<String> extensions) {
		this.title = title;
		this.summary = summary;
		this.extensions = new ArrayList<>(extensions);
	}
	
	public DataSourceDescription(String title, String description, String... extensions) {
		this.title = title;
		this.summary = description;
		this.extensions = new ArrayList<>(Arrays.asList(extensions));
	}

	public List<String> getExtensions() {
		return extensions;
	}

	public String getSummary() {
		return summary;
	}

	public String getTitle() {
		return title;
	}
	
	
	
}
