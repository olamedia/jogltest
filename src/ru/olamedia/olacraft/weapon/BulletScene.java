package ru.olamedia.olacraft.weapon;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GLAutoDrawable;

public class BulletScene {
	private List<Bullet> bullets = new ArrayList<Bullet>();

	public void add(Bullet b) {
		bullets.add(b);
	}
	
	public int getCount(){
		return bullets.size();
	}

	public void update(float deltas) {
		for (int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			b.update(deltas);
			if (b.toRemove) {
				bullets.remove(b);
				i--;
			}
		}
	}

	public void render(GLAutoDrawable drawable) {
		for (Bullet b : bullets) {
			b.render(drawable);
		}
	}
}
