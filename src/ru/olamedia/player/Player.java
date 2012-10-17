package ru.olamedia.player;

import ru.olamedia.liveEntity.LiveEntity;
import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.network.packet.LiveEntityLocationUpdatePacket;
import ru.olamedia.olacraft.weapon.Bullet;
import ru.olamedia.olacraft.world.block.Block;
import ru.olamedia.olacraft.world.chunk.Chunk;
import ru.olamedia.olacraft.world.data.ChunkData;

public class Player extends LiveEntity {

	@Override
	public void notifyLocationUpdate() {
		LiveEntityLocationUpdatePacket p = new LiveEntityLocationUpdatePacket();
		p.x = getX();
		p.y = getY();
		p.z = getZ();
		Game.client.send(p);
	}

	public Player() {
	}
	
	public void pickBlock(Block b){
		b.removeFromWorld();
	}

	public void onMouseClick() {
		if (null != Game.client.getScene().nearestBlock){
			pickBlock(Game.client.getScene().nearestBlock);
		}
		// Bullet b = new Bullet();
		// b.velocity.set(Game.instance.camera.getLook());
		// b.velocity.negate();
		// b.velocity.scale(100);
		// b.location.set(getX(), getCameraY(), getZ());
		// Game.client.getScene().addBullet(b);
	}
}
