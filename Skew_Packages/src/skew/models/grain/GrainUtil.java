package skew.models.grain;

/**
 * MisAnglePointList defines the structure for storing the mis-angles for all scan points in an area scan. 
 * @author Jinhui Qin, 2011
 *
 */
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;


public class GrainUtil
{
		
	public static int grainCount(ISkewGrid<GrainPixel> grainModel) {
		int max = 0;
		for (ISkewPoint<GrainPixel> grainPoint : grainModel) {
			Optional<Integer> index = grainPoint.getData().grainIndex;
			if (!index.isPresent()) continue;
			max = Math.max(max, index.get());
		}
		return max+1;
	}
	
	public static List<Grain> getGrains(ISkewGrid<GrainPixel> grainModel) {
		Set<Grain> grains = new LinkedHashSet<>();
		for (ISkewPoint<GrainPixel> grainPoint : grainModel) {
			Grain grain = grainPoint.getData().grain;
			if (grain == null) continue;
			grains.add(grain);
		}
		return new ArrayList<>(grains);
	}
	
	public static List<ISkewPoint<GrainPixel>> getGrainPoints(ISkewGrid<GrainPixel> grainModel, Grain g) {
		List<ISkewPoint<GrainPixel>> points = new ArrayList<>();
		for (ISkewPoint<GrainPixel> grainPoint : grainModel) {
			Grain other = grainPoint.getData().grain;
			if (other != g) continue;
			points.add(grainPoint);
		}
		return points;
	}
	
	public static Multimap<Grain, ISkewPoint<GrainPixel>> getGrainPointMap(ISkewGrid<GrainPixel> grainModel)
	{
		Multimap<Grain, ISkewPoint<GrainPixel>> map = ArrayListMultimap.create();
		
		for (ISkewPoint<GrainPixel> grainPoint : grainModel) {
			Grain grain = grainPoint.getData().grain;
			if (grain == null) continue;
			map.put(grain, grainPoint);
		}
		
		return map;
	}

		
}
