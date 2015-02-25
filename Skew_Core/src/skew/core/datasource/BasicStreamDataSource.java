package skew.core.datasource;

import java.util.List;

import commonenvironment.IOOperations;

public abstract class BasicStreamDataSource implements StreamDataSource
{

	private DataSourceDescription desc;
	
	public BasicStreamDataSource(String extension, String description, String title)
	{
		desc = new DataSourceDescription(title, description, extension);
	}
	
	@Override
	public DataSourceDescription getDescription() {
		return desc;
	}


	protected String getDatasetTitle(List<String> filenames)
	{
		return IOOperations.getCommonFileName(filenames);
	}


	public static boolean allWithExtension(List<String> filenames, String ext) {
		return filenames.stream().map((f) -> f.toLowerCase().endsWith(ext)).reduce(true, (a, b) -> a && b);		
	}
	
}
