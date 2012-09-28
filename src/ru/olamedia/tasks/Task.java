package ru.olamedia.tasks;

public abstract class Task implements Runnable {

	public Task(){
		TaskManager.add(this);
	}
	
	protected volatile boolean stopped = false;

	public void setStopped(boolean s){
		this.stopped = s;
	}
	
	public void stop() {
		this.stopped = true;
	}

	protected boolean shouldStop() {
		return this.stopped;
	}

	public abstract void run();
}
