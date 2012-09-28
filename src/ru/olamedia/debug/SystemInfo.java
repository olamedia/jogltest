package ru.olamedia.debug;

public class SystemInfo {
	public static void dump() {
		int cpu = Runtime.getRuntime().availableProcessors();
		long maxMemory = Runtime.getRuntime().maxMemory();
		long totalMemory = Runtime.getRuntime().totalMemory();
		long freeMemory = Runtime.getRuntime().freeMemory();
		// String country = System.getProperty("user.country");
		String username = System.getProperty("user.name");
		String os = System.getProperty("os.name");
		String osver = System.getProperty("os.version");
		String arch = System.getProperty("os.arch");
		System.out.println("Hello, " + username + " :)");
		System.out.println("" + os + " " + arch + " " + osver);
		System.out
				.println("Total CPU: " + cpu + " Memory free/total/max: "
						+ ((int) Math.floor(freeMemory / (1024 * 1024))) + "/"
						+ ((int) Math.floor(totalMemory / (1024 * 1024))) + "/"
						+ ((int) Math.floor(maxMemory / (1024 * 1024))));
		System.out.println(System.getProperty("java.vendor") + " " + System.getProperty("java.version"));
		System.out.println(System.getProperty("java.runtime.name") + " " + System.getProperty("java.runtime.version"));
	}
}
