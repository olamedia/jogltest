package ru.olamedia.game;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import ru.olamedia.astronomy.SunCalendar;

public class GameTime {
	public static class SunCalc {
		private GameTime time;
		private int r = 450;

		public SunCalc(GameTime gameTime) {
			this.time = gameTime;
		}

		public float getX() {
			return (float) (Math.sin(Math.PI * time.getSunHA() / 180) * r);
		}

		public float getY() {
			return (float) (Math.cos(Math.PI * time.getSunHA() / 180) * r);
		}

		public float getZ() {
			return (float) (Math.sin(Math.PI * time.sunCalendar.getDeclination() / 180) * r);
		}

	}

	public double longitude = 56.803698;
	public double latitude = 60.635262;
	public double gameSpeed = 60;// 1 real second is 60 seconds at game
	private static double daysInYear = 365.242;
	private static double minute = 60;
	@SuppressWarnings("unused")
	private static double hour = 3600;
	private static double day = 24 * 3600;
	@SuppressWarnings("unused")
	private static double month = (daysInYear / 12) * 24 * 3600;

	public GregorianCalendar earthCalendar;
	public SunCalendar sunCalendar;
	public double gameStart;
	public double yearStart;
	public double gameTime;
	public double gameYearTime;
	public double sunlightFactor = 0f;
	public int lastSunLight = 0;
	public float[] clearColors = new float[] { 0.0f, 0.0f, 0.0f };
	public boolean spaceLightIsInvalid = true;
	public boolean receivedLightIsInvalid = true;
	public double sunrise;
	public double sunset; //
	public double sunHA; //
	public double sunEA; //
	public double sunTC; //
	public SunCalc sun = new SunCalc(this);

	public static double getSystemTime() {
		return System.currentTimeMillis() / (double) (1000);
	}

	public void init() {
		earthCalendar = new GregorianCalendar();
		earthCalendar.setTimeZone(TimeZone.getTimeZone("Asia/Yekaterinburg")); // FIX
																				// Timezone
																				// for
																				// latlon
		sunCalendar = new SunCalendar(earthCalendar, longitude, latitude);
		gameSpeed = day / (1 * minute);

		gameStart = getTime();

		yearStart = gameStart - (gameStart % daysInYear);
		updateGameTime();
	}

	public double getTime() {
		return getSystemTime() / gameSpeed;
	}

	public Date getGameDate() {
		return new Date((long) gameTime);
	}

	public void updateGameTime() {
		gameTime = (getTime() - gameStart) * gameSpeed * gameSpeed;
	}

	public String getDateTimeString() {
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date((long) (gameTime * 1000)));
	}

	private static float addComponent(float a, float b) {
		return a + b;
	}

	private static float mulComponent(float a, float b) {
		return a * b;
	}

	public float[] getClearColor() {
		float sunAngularDiameter = 32;
		float sunRenderDistance = 700;
		@SuppressWarnings("unused")
		float sunRenderDiameter = (float) ((float) 2 * sunRenderDistance * Math.tan(sunAngularDiameter / 2));

		sunRenderDiameter /= 15;

		float[] spaceColors = new float[] { 0.03f, 0.03f, 0.05f }; // a little
																	// blue

		clearColors = new float[] { 0.0f, 0.0f, 0.0f };

		double crossAngle = sunCalendar.getHourAngle();
		@SuppressWarnings("unused")
		int elevationAngle = (int) sunCalendar.radToDeg(sunCalendar.getElevationAngle());

		sunlightFactor = 0f;
		if ((crossAngle > -120 && crossAngle < -70) || (crossAngle > 70 && crossAngle < 120)) {
			sunlightFactor = (float) 1f - (Math.abs(crossAngle) - 70) / 50;
		}
		if (crossAngle >= -70 && crossAngle <= 70) {
			sunlightFactor = (float) 1f;
		}
		int sunlight = (int) Math.round(15 * sunlightFactor);
		if (sunlight != lastSunLight) {
			spaceLightIsInvalid = true;
			receivedLightIsInvalid = true;
		}
		//float[] sunSkyColors = new float[] { (float) 179 / 255, (float) 195 / 255, (float) 184 / 255 };
		//float[] sunSkyColors = new float[] { (float) 209 / 255, (float) 227 / 255, (float) 251 / 255 };
		float[] sunSkyColors = new float[] { (float) 203 / 255, (float) 233 / 255, (float) 244 / 255 };
		for (int i = 0; i < 3; i++) {
			clearColors[i] = addComponent(sunSkyColors[i] * (float) sunlightFactor, spaceColors[i]);
		}
		float lightness = clearColors[0] + clearColors[1] + clearColors[2];
		float[] sunRedColorsAdd = new float[] { 0.3f, 0.1f, 0.0f };
		for (int i = 0; i < 3; i++) {
			if (sunlightFactor < 0.5f && sunlightFactor > 0.0f) {
				float redFactor = (float) (1 - Math.abs(1 - sunlightFactor * 4));
				clearColors[i] = addComponent(clearColors[i], (float) sunRedColorsAdd[i] * redFactor);
			}
			clearColors[i] = mulComponent(clearColors[i], (float) sunlightFactor);
		}
		// fix lightness
		float newLightness = clearColors[0] + clearColors[1] + clearColors[2];
		for (int i = 0; i < 3; i++) {
			clearColors[i] = clearColors[i] * lightness / newLightness;
		}
		return clearColors;
	}

	public void tick() {
		updateGameTime();
		// earthCalendar.setTimeInMillis((long) gameTime * 1000);
		if (sunCalendar != null) {
			sunCalendar.setTimeInMillis((long) gameTime * 1000);
			sunCalendar.update();
			sunrise = Math.floor(sunCalendar.getSunrise() * 1000) / 1000;
			sunset = Math.floor(sunCalendar.getSunset() * 1000) / 1000;
			sunHA = sunCalendar.getHourAngle(); // Hour Angle is 0° at solar
												// noon
			sunEA = Math.floor(sunCalendar.getElevationAngle() * 1000) / 1000;
			sunTC = Math.floor(sunCalendar.getTimeCorrectionFactor() * 1000) / 1000;
		}
	}

	public float getSunHA() {
		return (float) sunCalendar.getHourAngle(); // Hour Angle is 0° at solar
													// noon
	}

	public float getSunEA() {
		return (float) sunCalendar.getElevationAngle();
	}
}
