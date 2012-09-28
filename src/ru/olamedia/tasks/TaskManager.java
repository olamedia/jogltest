package ru.olamedia.tasks;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
	private static List<Task> tasks = new ArrayList<Task>();

	public static void add(Task task) {
		tasks.add(task);
	}

	public static void stopAll() {
		for (Task task : tasks) {
			task.stop();
		}
	}
}
