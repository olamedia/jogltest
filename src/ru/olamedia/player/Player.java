package ru.olamedia.player;

import ru.olamedia.liveEntity.LiveEntity;
import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.network.packet.LiveEntityLocationUpdatePacket;
import ru.olamedia.olacraft.weapon.Bullet;

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

	public void onMouseClick() {
		Bullet b = new Bullet();
		b.velocity.set(Game.instance.camera.getLook());
		b.velocity.negate();
		b.velocity.scale(100);
		b.location.set(getX(), getCameraY(), getZ());
		Game.client.getScene().addBullet(b);
	}
}
