package ru.olamedia.olacraft.world.dataProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.esotericsoftware.kryonet.Connection;

import ru.olamedia.olacraft.network.GameClient;
import ru.olamedia.olacraft.network.packet.GetRegionPacket;
import ru.olamedia.olacraft.network.packet.IPacket;
import ru.olamedia.olacraft.network.packet.IPacketListener;
import ru.olamedia.olacraft.network.packet.RegionDataPacket;
import ru.olamedia.olacraft.world.data.RegionData;
import ru.olamedia.olacraft.world.location.RegionLocation;

public class RemoteChunkDataProvider extends AbstractChunkDataProvider implements IPacketListener {

	private GameClient client;
	private HashMap<RegionLocation, RegionData> map = new HashMap<RegionLocation, RegionData>();
	private List<String> loading = new ArrayList<String>();
	private List<RegionLocation> queue = new ArrayList<RegionLocation>();

	public RemoteChunkDataProvider(GameClient client) {
		this.client = client;
		this.client.addPacketListener(this);
	}

	private static boolean DEBUG = true;

	private void debug(String s) {
		if (DEBUG) {
			System.out.println("[RemoteChunkDataProvider] " + s);
		}
	}

	@Override
	public void loadRegion(RegionLocation regionLocation) {
		String key = regionLocation.toString();
		if (loading.isEmpty() && !loading.contains(key)) {
			debug("loadRegion(" + key + ")");
			loading.add(key);
			client.send(new GetRegionPacket(regionLocation));
			debug("sent packet: GetRegionPacket");
		}
	}

	@Override
	public boolean isRegionAvailable(RegionLocation regionLocation) {
		String key = regionLocation.toString();
		if (loading.contains(key)) {
			return false;
		}
		return map.containsKey(regionLocation);
	}

	@Override
	public RegionData getRegion(RegionLocation regionLocation) {
		RegionData data = map.get(regionLocation);
		map.remove(regionLocation);
		return data;
	}

	@Override
	public void onPacket(Connection connection, IPacket p) {
		if (p instanceof RegionDataPacket) {
			debug("received packet [conn " + connection.getID() + "]: RegionDataPacket");
			RegionData data = ((RegionDataPacket) p).data;
			data.decompress();
			//System.out.println(data.sectorData[0][0].get(15).isEmpty(0) + "");
			map.put(data.location, data);
			loading.remove(data.location);
		}
	}

}
