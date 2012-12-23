package ru.olamedia.game;

public class Launcher {

	public Launcher() {
	}

	private static GameManager manager;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		manager = new GameManager();
		manager.start();
		manager.dispose();
	}

}
