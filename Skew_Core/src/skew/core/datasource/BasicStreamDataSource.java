package skew.core.datasource;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import plural.executor.DummyExecutor;
import plural.executor.ExecutorSet;
import plural.executor.PluralExecutor;
import scitypes.Coord;
import skew.core.datasource.DataSource.FileFormatAcceptance;
import skew.core.model.IModel;
import skew.core.model.ISkewDataset;
import skew.core.model.SkewDataset;
import commonenvironment.IOOperations;

public abstract class BasicStreamDataSource implements StreamDataSource
{

	private String ext, desc, title;
	
	public BasicStreamDataSource(String extension, String description, String title)
	{
		this.ext = extension;
		this.desc = description;
		this.title = title;
	}
	
	@Override
	public String extension()
	{
		return ext;
	}

	@Override
	public String description()
	{
		return desc;
	}

	@Override
	public String title()
	{
		return title;
	}


	protected String getDatasetTitle(List<String> filenames)
	{
		return IOOperations.getCommonFileName(filenames);
	}


	public static boolean allWithExtension(List<String> filenames, String ext) {
		return filenames.stream().map((f) -> f.toLowerCase().endsWith(ext)).reduce(true, (a, b) -> a && b);		
	}
	
}
