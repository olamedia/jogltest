package ru.olamedia.olacraft.world.data;

import ru.olamedia.olacraft.world.provider.WorldProvider;

public class ChunkDataManager {
	private ChunkData data;
	private ChunkDataPointer pointer = new ChunkDataPointer();
	private WorldProvider provider;
	
	public void setProvider(WorldProvider provider){
		this.provider = provider;
	}
	
	public void setData(ChunkData data) {
		this.data = data;
		pointer.reset();
	}
	
	private void loadNeighbors(){
		
	}
}
