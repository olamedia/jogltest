package ru.olamedia.game;

public class Game {
	public boolean isRunning = false;
	public boolean isPaused = false;
	public void dispose(){
		
	}
	public void start(){
		isRunning = true;
		init();
	}
	public void pause(){
		isPaused = true;
	}
	public void resume(){
		isPaused = false;
	}
	public void finish(){
		isRunning = false;
	}
	public void init(){
		
	}
}
