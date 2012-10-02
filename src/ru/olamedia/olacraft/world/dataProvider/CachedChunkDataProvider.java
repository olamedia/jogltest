package ru.olamedia.olacraft.world.dataProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.olamedia.olacraft.world.data.RegionData;
import ru.olamedia.olacraft.world.location.RegionLocation;

public class CachedChunkDataProvider extends AbstractChunkDataProvider {
	private AbstractChunkDataProvider provider;
	private HashMap<String, RegionData> regionMap = new HashMap<String, RegionData>();
	private List<String> loading = new ArrayList<String>();

	public CachedChunkDataProvider(AbstractChunkDataProvider provider) {
		this.provider = provider;
	}

	private static boolean DEBUG = true;

	private void debug(String s) {
		if (DEBUG) {
			System.out.println("[CachedChunkDataProvider] " + s);
		}
	}

	@Override
	public boolean isRegionAvailable(RegionLocation regionLocation) {
		String key = regionLocation.toString();// regionLocation.x + "-" +
		if (regionMap.containsKey(key)) {
			return true;
		}
		return provider.isRegionAvailable(regionLocation);
	}

	@Override
	public void loadRegion(RegionLocation regionLocation) {
		String key = regionLocation.toString();
		// debug("loadRegion(" + regionLocation + ")");
		if (!regionMap.containsKey(key)) {
			provider.loadRegion(regionLocation);
		} else {
			debug("error: loadRegion(" + regionLocation + ") already in regionMap");
		}
	}

	@Override
	public RegionData getRegion(RegionLocation regionLocation) {
		String key = regionLocation.toString();
		if (regionMap.containsKey(key)) {
			return regionMap.get(key);
		} else {
			RegionData data = provider.getRegion(regionLocation);
			regionMap.put(key, data);
			return data;
		}
	}

}
