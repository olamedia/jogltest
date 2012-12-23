package ru.olamedia.olacraft.world.dataProvider;

import java.util.ArrayList;
import java.util.HashMap;

import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.world.block.BlockRegistry;
import ru.olamedia.olacraft.world.data.RegionData;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.location.ChunkLocation;
import ru.olamedia.olacraft.world.location.RegionLocation;

public class CachedChunkDataProvider extends AbstractChunkDataProvider {
	private AbstractChunkDataProvider provider;
	private HashMap<RegionLocation, RegionData> regionMap = new HashMap<RegionLocation, RegionData>();

	private RegionData current;

	public CachedChunkDataProvider(AbstractChunkDataProvider provider) {
		this.provider = provider;
		this.provider.setTypeRegistry(types);
	}

	@Override
	public void setTypeRegistry(BlockRegistry types) {
		super.setTypeRegistry(types);
		this.provider.setTypeRegistry(types);
	}

	private static boolean DEBUG = true;

	private void debug(String s) {
		if (DEBUG) {
			System.err.println("[CachedChunkDataProvider] " + s);
		}
	}

	private boolean containsKey(RegionLocation l) {
		return regionMap.containsKey(l);
	}

	// private boolean containsKey(int x, int y, int z) {
	// return regionMap.containsKey(x) && regionMap.get(x).containsKey(y) &&
	// regionMap.get(x).get(y).containsKey(z);
	// }

	private void put(RegionLocation l, RegionData data) {
		regionMap.put(l, data);
	}

	private RegionData get(RegionLocation l) {
		return regionMap.get(l);
	}

	@Override
	public boolean isRegionAvailable(RegionLocation regionLocation) {
		if (regionMap.containsKey(regionLocation)) {
			return true;
		}
		debug("regions: " + regionMap.size() + "");
		return provider.isRegionAvailable(regionLocation);
	}

	@Override
	public void loadRegion(RegionLocation regionLocation) {
		if (!containsKey(regionLocation)) {
			provider.loadRegion(regionLocation);
		} else {
			debug("error: loadRegion(" + regionLocation + ") already in regionMap");
		}
	}

	@Override
	public RegionData getRegion(RegionLocation regionLocation) {
		if (containsKey(regionLocation)) {
			return get(regionLocation);
		} else {
			put(regionLocation, provider.getRegion(regionLocation));
			gc();
			return get(regionLocation);
		}
	}

	public void unloadRegion(RegionLocation regionLocation) {
		regionMap.remove(regionLocation);
	}

	private ArrayList<RegionLocation> gcKeys = new ArrayList<RegionLocation>();

	public void gc() {
		if (Game.instance.player != null && regionMap.size() > 4) {
			final BlockLocation b = Game.instance.player.getCameraBlockLocation();
			final RegionLocation r = b.getRegionLocation();
			for (RegionLocation l : regionMap.keySet()) {
				if ((l.x > r.x + 1) || (l.x < r.x - 1)) {
					if ((l.z > r.z + 1) || (l.z < r.z - 1)) {
						gcKeys.add(l);
					}
				}
			}
			for (RegionLocation l : gcKeys) {
				unloadRegion(l);
			}
			gcKeys.clear();
			regionMap.clear();
		}
	}
}
