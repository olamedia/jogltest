package ru.olamedia.olacraft.world.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import ru.olamedia.math.OpenBitSet;
import ru.olamedia.olacraft.world.blockTypes.AbstractBlockType;
import ru.olamedia.olacraft.world.chunk.Chunk;
import ru.olamedia.olacraft.world.drop.DroppedEntity;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.location.ChunkLocation;
import ru.olamedia.olacraft.world.location.IntLocation;
import ru.olamedia.olacraft.world.provider.WorldProvider;

/**
 * 1 bit for opaque
 * if non-opaque, some left bits will be used for light, else can be used for
 * meta
 * 
 * Opaque blocks:
 * stone/cobblestone/gravel/sand(dust) is a state, 2 bits
 * dirt/peat
 * 
 * Non-opaque:
 * fluids: water, acid, lava
 * glass
 * 
 * trees etc is an entities, not the blocks
 * 
 * 
 * 
 * @author olamedia
 * 
 */
public class ChunkData implements Serializable {
	private static final long serialVersionUID = -5704237444737895501L;
	public ChunkLocation location;
	private static transient int SIZE = 4096;
	// private boolean[] notEmpty = new boolean[SIZE];
	private OpenBitSet emptyBlocks = new OpenBitSet(4096);
	public int visibleCount = 0;
	public OpenBitSet visible = null; // fast precomputed
										// visibility (true if
										// any side is open)
	// public OpenBitSet sunlight = new OpenBitSet(65536);
	public byte[] types = new byte[4096];
	public int notEmptyCount = 0;
	public boolean visibilityPrecomputed = false;
	public boolean voidLightPrecomputed = false;
	public boolean voidLightChanged = false;
	private boolean compressionStarted = false;
	private boolean isCompressed = false;
	private byte[] compressed;
	private ChunkDataPointer pointer = new ChunkDataPointer();
	public HashMap<Integer, AbstractBlockType> timeManagedBlocks = new HashMap<Integer, AbstractBlockType>();
	public ArrayList<DroppedEntity> droppedEntities = new ArrayList<DroppedEntity>();

	public void invalidateComputations() {
		visibilityPrecomputed = false;
		voidLightPrecomputed = false;
	}

	public void detachBlock(short id) {
		emptyBlocks.set(id);
		types[id] = 0;
	}

	public void tick(WorldProvider provider) {

	}

	/**
	 * After populating with blocks, create entities
	 * 
	 * @param provider
	 */
	public void initBlockEntities(WorldProvider provider) {
		pointer.reset();
		while (pointer.hasNext()) {
			if (!isEmpty(pointer.getId()) && types[pointer.getId()] != 0) {
				AbstractBlockType type = provider.getTypeRegistry().getBlockType(types[pointer.getId()]);
				// check if entity-managed block
				// check if time-managed block
				if (type.isTimeManaged()) {
					timeManagedBlocks.put(pointer.getId(), type);
				}
			}
			//
			pointer.next();
		}
	}

	public static short ID(int x, int y, int z) {
		return (short) (x * 256 + y * 16 + z);
	}

	public static short ClampID(BlockLocation location) {
		return location.getId();
	}

	public static short ClampID(int x, int y, int z) {
		return ID(x & 15, y & 15, z & 15);
	}

	public void compress() {
		if (!isCompressed && !compressionStarted) {
			compressionStarted = true;
			try {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				// GZIPOutputStream gzout = new GZIPOutputStream(bout);
				ObjectOutputStream out = new ObjectOutputStream(bout);
				out.writeObject(this);
				compressed = bout.toByteArray();
				types = null;
				emptyBlocks = null;
				out.close();
				// gzout.close();
				bout.close();
				isCompressed = true;
			} catch (IOException e) {
				compressionStarted = false;
				e.printStackTrace();
			}
		}
	}

	public ChunkData decompress() {
		if (isCompressed) {
			try {
				ByteArrayInputStream bin = new ByteArrayInputStream(compressed);
				// GZIPInputStream gzin = new GZIPInputStream(new
				// BufferedInputStream(bin));
				ObjectInputStream in = new ObjectInputStream(bin);
				ChunkData data = (ChunkData) in.readObject();
				in.close();
				// gzin.close();
				bin.close();
				return data;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	// public transient int[] type = new int[SIZE];

	public ChunkData() {
	}

	private void setVisible(int x, int y, int z) {
		final short id = IntLocation.id(x, y, z);
		if (!visible.get(id)) {
			visibleCount++;
		}
		visible.set(id);
	}

	private void setInvisible(int x, int y, int z) {
		final short id = IntLocation.id(x, y, z);
		if (visible.get(id)) {
			visibleCount--;
		}
		visible.clear(id);
	}

	public void computeVisibility(WorldProvider provider) {
		visible = new OpenBitSet(4096);
		visibleCount = 0;
		precomputeVisibility(provider);
		computeVisibility(provider, provider.getChunk(location.getLeft()), provider.getChunk(location.getRight()),
				provider.getChunk(location.getTop()), provider.getChunk(location.getBottom()),
				provider.getChunk(location.getFront()), provider.getChunk(location.getBack()));
	}

	public void computeVisibility(WorldProvider provider, ChunkData left, ChunkData right, ChunkData top,
			ChunkData bottom, ChunkData front, ChunkData back) {
		// compute left/right
		int x, y, z;
		short id;
		for (y = 0; y <= 15; y++) {
			for (z = 0; z <= 15; z++) {
				x = 15;
				id = IntLocation.id(x, y, z);
				if (!left.isOpaque(provider, id)) {
					x = 0;
					setVisible(x, y, z);
				}
				x = 0;
				id = IntLocation.id(x, y, z);
				if (!right.isOpaque(provider, id)) {
					x = 15;
					setVisible(x, y, z);
				}
			}
		}
		// top/bottom
		for (x = 0; x <= 15; x++) {
			for (z = 0; z <= 15; z++) {
				y = 15;
				id = IntLocation.id(x, y, z);
				if (!bottom.isOpaque(provider, id)) {
					y = 0;
					setVisible(x, y, z);
				}
				y = 0;
				id = IntLocation.id(x, y, z);
				if (!top.isOpaque(provider, id)) {
					y = 15;
					setVisible(x, y, z);
				}
			}
		}
		// front/back
		for (x = 0; x <= 15; x++) {
			for (y = 0; y <= 15; y++) {
				z = 15;
				id = IntLocation.id(x, y, z);
				if (!back.isOpaque(provider, id)) {
					z = 0;
					setVisible(x, y, z);
				}
				z = 0;
				id = IntLocation.id(x, y, z);
				if (!front.isOpaque(provider, id)) {
					z = 15;
					setVisible(x, y, z);
				}
			}
		}
	}

	/**
	 * Compute visibility ("visible" if any side have non-opaque neighbor)
	 * Leaving side blocks invisible. Use computeVisibility(WorldProvider) to
	 * compute visibility of side blocks
	 */
	public void precomputeVisibility(WorldProvider provider) {
		// first pass, make all blocks invisible, except side blocks
		for (int x = 0; x <= 15; x++) {
			for (int y = 0; y <= 15; y++) {
				for (int z = 0; z <= 15; z++) {
					// if (x == 0 || x == 15 || y == 0 || y == 15 || z == 0 || z
					// == 15) {
					// setVisible(x, y, z);
					// } else {
					// setInvisible(x, y, z);
					// }
					setInvisible(x, y, z);
				}
			}
		}
		// second pass, make some blocks visible
		for (int x = 0; x <= 15; x++) {
			for (int y = 0; y <= 15; y++) {
				for (int z = 0; z <= 15; z++) {
					int id = x * 16 * 16 + y * 16 + z;
					if (!isOpaque(provider, id)) {
						setVisible(x - 1, y, z);
						setVisible(x + 1, y, z);
						setVisible(x, y - 1, z);
						setVisible(x, y + 1, z);
						setVisible(x, y, z - 1);
						setVisible(x, y, z + 1);
					}
				}
			}
		}
		visibilityPrecomputed = true;
	}

	public boolean isOpaque(WorldProvider provider, int id) {
		return !isEmpty(id) && provider.getTypeRegistry().isOpaque(types[id]);
	}

	public void compact() {
		if (notEmptyCount == 0) {
			// emptyBlocks = null;
		}
	}

	public AbstractBlockType getType(BlockLocation blockLocation, WorldProvider provider) {
		if (emptyBlocks == null) {
			return null;
		}
		int id = Chunk.in(blockLocation.x) * 16 * 16 + Chunk.in(blockLocation.y) * 16 + Chunk.in(blockLocation.z);
		if (isEmpty(id)) {
			return null;
		}
		return provider.getBlockTypeById(types[id]);
	}

	public boolean isEmpty(BlockLocation blockLocation) {
		if (emptyBlocks == null) {
			return true;
		}
		int id = Chunk.in(blockLocation.x) * 16 * 16 + Chunk.in(blockLocation.y) * 16 + Chunk.in(blockLocation.z);
		return isEmpty(id);
	}

	public boolean isEmpty(int id) {
		if (emptyBlocks == null) {
			return true;
		}
		return emptyBlocks.get(id);
	}

	public void setEmpty(int id, boolean isEmpty) {
		if (isEmpty(id) != isEmpty) {
			if (!isEmpty) {
				notEmptyCount++;
			} else {
				notEmptyCount--;
			}
		}
		if (isEmpty) {
			emptyBlocks.set(id);
		} else {
			emptyBlocks.clear(id);
		}
	}

	public boolean isEmpty() {
		return emptyBlocks == null || emptyBlocks.cardinality() == 0;
	}

	public void setEmpty(int inChunkX, int inChunkY, int inChunkZ, boolean isEmpty) {
		int id = inChunkX * 16 * 16 + inChunkY * 16 + inChunkZ;
		setEmpty(id, isEmpty);
	}

	public void setEmpty(BlockLocation blockLocation, boolean isEmpty) {
		int id = Chunk.in(blockLocation.x) * 16 * 16 + Chunk.in(blockLocation.y) * 16 + Chunk.in(blockLocation.z);
		setEmpty(id, isEmpty);
	}

	public void freeRenderData() {
		visible = null;
		// light = null;
		// voidLightPrecomputed = false;
	}

	public void addDroppedEntity(DroppedEntity droppedEntity) {
		droppedEntities.add(droppedEntity);
	}

}
