package ru.olamedia.game;

public class Launcher {

	public Launcher() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GameManager manager = new GameManager();
		manager.start();
		manager.dispose();
	}

}
