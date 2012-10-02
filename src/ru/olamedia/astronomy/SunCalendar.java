package ru.olamedia.astronomy;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * Sun position calculations
 * 
 * @author olamedia
 * 
 */
@SuppressWarnings("unused")
public class SunCalendar extends GregorianCalendar {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2708738531759361416L;
	double jd;
	double jcycle;
	double meanLongitude; // L
	private double latitude;
	private double longitude;

	public double getLongitude() { // User longitude at Earth
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	private double to360(double v) {
		double v360 = v - Math.floor(v / 360) * 360;
		while (v360 < 0) {
			v360 += 360;
		}
		return v360;
	}

	private void computePosition() {
		// These orbital elements are thus valid for the Sun's (apparent) orbit
		// around the Earth.
		// All angular values are expressed in degrees:
		double w = 282.9404 + 4.70935E-5 * jd; // (longitude of perihelion)
												// 282.9404_deg + 4.70935E-5_deg
												// * jd
		double a = 1.000000; // (mean distance, a.u.)
		double e = 0.016709 - 1.151E-9 * jd; // (eccentricity)
		double M = to360(356.0470 + 0.9856002585 * jd);// (mean anomaly)
														// 356.0470_deg +
		// 0.9856002585_deg * jd
		double oblecl = 23.4393 - 3.563E-7 * jd; // (obliquity of the ecliptic)
													// 23.4393_deg -
													// 3.563E-7_deg * jd
		double L = w + M; // Sun's mean longitude
		meanLongitude = L;
		// Let's go on computing an auxiliary angle, the eccentric anomaly.
		// Since the eccentricity of the Sun's (i.e. the Earth's) orbit is so
		// small, 0.017,
		// a first approximation of E will be accurate enough. Below E and M are
		// in degrees:
		double E = M + (180 / Math.PI) * e * Math.sin(M)
				* (1 + e * Math.cos(M)); // (eccentric anomaly)
		// Now we compute the Sun's rectangular coordinates in the plane of the
		// ecliptic, where the X axis points towards the perihelion:
		double x = Math.cos(E) - e; // x = r * Math.cos(v) = Math.cos(E) - e
		double y = Math.sin(E) * Math.sqrt(1 - e * e); // y = r * Math.sin(v) =
														// Math.sin(E) * sqrt(1
														// - e*e)

		// Convert to distance and true anomaly:
		double r = Math.sqrt(x * x + y * y);
		double v = Math.atan2(y, x);

		double lon = v + w;
	}

	double UT; // UT is the same as Greenwich time

	public void computeSidetime() {
		double GMST0 = to360(meanLongitude + 180) / 15;
		double LON = 15; // Central Europe (at 15 deg east longitude = +15
							// degrees long) on 19 april 1990 at 00:00 UT

		// LON is the terrestial longitude in degrees (western longitude is
		// negative, eastern positive).
		// To "convert" the longitude from degrees to hours we divide it by 15
		double SIDTIME = GMST0 + UT + LON / 15;
		// To compute the altitude and azimuth
		// we also need to know the Hour Angle, HA.

		// The Hour Angle is zero when the clestial body
		// is in the meridian i.e. in the south
		// (or, from the southern heimsphere, in the north) -
		// this is the moment when the celestial body
		// is at its highest above the horizon.

		// The Hour Angle increases with time
		// (unless the object is moving faster than the Earth rotates;
		// this is the case for most artificial satellites).
		// It is computed from:
		// RA - Right Ascension
		// double HA = SIDTIME - RA;
	}

	/*
	 * Local Standard Time Meridian (LSTM) The Local Standard Time Meridian
	 * (LSTM) is a reference meridian used for a particular time zone and is
	 * similar to the Prime Meridian, which is used for Greenwich Mean Time.
	 */
	public double getLocalStandardTimeMeridian() {
		return 15 * this.getTimeZone().getOffset(this.getTimeInMillis());
	}

	/*
	 * Equation of Time (EoT)
	 * 
	 * The equation of time (EoT) (in minutes) is an empirical equation that
	 * corrects for the eccentricity of the Earth's orbit and the Earth's axial
	 * tilt. where B in degrees and d is the number of days since the start of
	 * the year. The time correction EoT is plotted in the figure below.
	 */
	public double getEquationOfTime() {
		double B = getB();
		return ((double) 9.87 * Math.sin(2 * B) - 7.53 * Math.cos(B) - 1.5 * Math
				.sin(B));
	}

	/*
	 * Time Correction Factor (TC)
	 * 
	 * The net Time Correction Factor (in minutes) accounts for the variation of
	 * the Local Solar Time (LST) within a given time zone due to the longitude
	 * variations within the time zone and also incorporates the EoT above.
	 * 
	 * The factor of 4 minutes comes from the fact that the Earth rotates 1°
	 * every 4 minutes.
	 */
	public double getTimeCorrectionFactor() {
		// FIX LATER
		return 0;// 4 * (getLocalStandardTimeMeridian() - getLongitude())
					// + getEquationOfTime();
	}

	/*
	 * Local Solar Time (LST)
	 * 
	 * The Local Solar Time (LST) can be found by using the previous two
	 * corrections to adjust the local time (LT).
	 */
	public double getLocalSolarTime() {
		return (double) get(Calendar.HOUR_OF_DAY) + (double) get(Calendar.MINUTE) / 60
				+ (double) get(Calendar.SECOND) / (60 * 60) + getTimeCorrectionFactor()
				/ 60;
	}

	/*
	 * Hour Angle (HRA)
	 * 
	 * The Hour Angle converts the local solar time (LST) into the number of
	 * degrees which the sun moves across the sky. By definition, the Hour Angle
	 * is 0° at solar noon. Since the Earth rotates 15° per hour, each hour away
	 * from solar noon corresponds to an angular motion of the sun in the sky of
	 * 15°. In the morning the hour angle is negative, in the afternoon the hour
	 * angle is positive.
	 */
	public double getHourAngle() {
		return 15 * (getLocalSolarTime() - 12); // 15°(LST-12)
	}

	/*
	 * Declination
	 * 
	 * The declination angle has been previously given as:
	 * 
	 * Where d is the number of days since the start of the year.
	 */
	public double getDeclination() {
		return (double) 23.45 * Math.sin(getB());
	}

	public double getElevationAngle() {
		// φ is the latitude of the location of interest
		// δ is the declination angle
		// elevation angle at solar noon:
		// α = 90 - φ + δ (Northern Hemisphere: +90° North pole)
		// α = 90 + φ - δ (Southern Hemisphere: -90° South pole)
		// 90 + (getLatitude() - getDeclination())
		// * (getLatitude() > 0 ? -1 : 1);
		return Math.pow(
				Math.sin(Math.sin(getDeclination()) * Math.sin(getLatitude())
						+ Math.cos(getDeclination()) * Math.cos(getLatitude())
						* Math.cos(getHourAngle())

				), -1);
	}

	public double getZenithAngle() {
		return 90 - getElevationAngle();
	}

	public double getSunrise() {
		// Sunrise=12−1/150 cos−1(−sinφ sinδ cosφ cosδ)−TC/60
		double delta = getLongitude() + radToDeg(getHourAngleSunrise());
		double UTCsec = (720 - (4.0 * delta) - getEquationOfTime()) * 60;
		double sec = UTCsec
				+ (this.getTimeZone().getOffset(getTimeInMillis()) / 1000);// in
		// minutes,
		// UTC
		return (double) (int) sec / (60 * 60);
	}

	public double getSunset() {
		// Sunset=12+1/150 cos−1(−sinφ sinδ cosφ cosδ)−TC/60
		double lw = getLongitude();
		return (getHourAngle() + lw) / 360;
		// 2451545.009 + + jcycle
		// + 0.0053 * Math.sin(M) - 0.0069 * Math.sin(2*lamb);

		// return 12
		// + 15
		// * Math.pow(
		// Math.cos(-Math.tan(getLatitude())
		// * Math.tan(getDeclination())), -1)
		// - getTimeCorrectionFactor() / 60;
	}

	public double radToDeg(double angleRad) {
		return (180.0 * angleRad / Math.PI);
	}

	public double degToRad(double angleDeg) {
		return (Math.PI * angleDeg / 180.0);
	}

	public double getHourAngleSunrise() {
		double latRad = degToRad(getLatitude());
		double sdRad = degToRad(getDeclination());
		double HAarg = (Math.cos(degToRad(90.833))
				/ (Math.cos(latRad) * Math.cos(sdRad)) - Math.tan(latRad)
				* Math.tan(sdRad));
		double HA = Math.acos(HAarg);
		return (HA); // in radians (for sunset, use -HA)
	}

	public double getB() {
		return ((double) 360 / 365)
				* (double) (this.get(Calendar.DAY_OF_YEAR) - 81);
	}

	public SunCalendar(Calendar cal, double longitude, // West-East
			double latitude // +90° North-South -90°
	) {
		super();
		this.setTimeZone(cal.getTimeZone());
		this.setTime(cal.getTime());
		this.setLongitude(longitude);
		// Arctic Circle 66° 33′ 39″ N
		// Tropic of Cancer 23° 26′ 21″ N
		// Tropic of Capricorn 23° 26′ 21″ S
		// Antarctic Circle 66° 33′ 39" S
		this.setLatitude(latitude);
		// computePosition();
		// computeSidetime();
	}

	public void update() {
		jd = JulianDate.makeJulianDateUsingMyModified(this);
		// Here lw is the longitude west (west is positive, east is negative)
		// of the observer on the Earth;
		jcycle = jd - 2451545.009 - getLongitude() / 360;
	}

}
