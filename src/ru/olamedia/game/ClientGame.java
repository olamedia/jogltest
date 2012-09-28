package ru.olamedia.game;

public class ClientGame extends Game {
	GameManager manager;
	public ClientGame(GameManager manager) {
		this.manager = manager;
	}
	public void pause(){
		// open in-game menu
		super.pause();
		manager.start();
	}
}
