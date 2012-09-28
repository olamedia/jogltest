package ru.olamedia.olacraft.physics;

import org.ode4j.ode.DBody;
import org.ode4j.ode.DRay;
import org.ode4j.ode.DWorld;
import org.ode4j.ode.OdeHelper;

public class GamePhysicsWorld {
	private DWorld world;

	public GamePhysicsWorld() {
		world = OdeHelper.createWorld();
		world.setGravity(0, -0.98, 0);
	}

	public DWorld getWorld() {
		return world;
	}
	
	public DBody createBody(){
		return OdeHelper.createBody(world);
	}
	
	public DRay createRay(int length){
		return OdeHelper.createRay(length);
	}
}
