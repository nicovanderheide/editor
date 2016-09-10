package nl.nicovanderheide.locale.properties.editor.support;

public final class Time {

	/** One second. */
	private static final long SECOND = 1000;

	/** One minute. */
	private static final long MINUTE = SECOND * 60;

	/** One hour. */
	private static final long HOUR = MINUTE * 60;

	/** One day. */
	private static final long DAY = HOUR * 24;

	/** One year. */
	private static final long YEAR = DAY * 365;

	/** All possible timeSpans to check */
	private static final long[] CHECKS = { YEAR, DAY, HOUR, MINUTE, SECOND, 1 };

	/**
	 * A Utility-Class using static methods should not be able to be
	 * instantiated.
	 */
	private Time() {
	}

	private static long check(final long checkTo, long milliseconds,
			StringBuffer timeString) {
		Long check = milliseconds / checkTo;
		if (check > 0) {
			timeString.append(check);
			if (checkTo == YEAR) {
				timeString.append('y');
			} else if (checkTo == DAY) {
				timeString.append('d');
			} else if (checkTo == HOUR) {
				timeString.append('h');
			} else if (checkTo == MINUTE) {
				timeString.append('m');
			} else if (checkTo == SECOND) {
				timeString.append('s');
			} else {
				timeString.append("ms");
			}
			timeString.append(' ');
			milliseconds = milliseconds % checkTo;
		}

		return milliseconds;
	}

	/**
	 * Taken between.
	 * 
	 * @param begin
	 *            the begin
	 * @return the representation of the time it has taken between 'begin' and
	 *         the SystemTime
	 */
	public static String takenBetween(final Long begin) {
		return taken(System.currentTimeMillis() - begin);
	}

	/**
	 * How long is taken for the amount of milliseconds.
	 * 
	 * @param duration
	 *            the count of milliseconds some process has taken.
	 * @return the representation of the time it has taken
	 */
	public static String taken(final Long duration) {
		long milliseconds = Math.abs(duration);
		StringBuffer timeString = new StringBuffer();
		for (long checkTo : CHECKS) {
			milliseconds = check(checkTo, milliseconds, timeString);
		}
		if (timeString.length() == 0) {
			timeString.append("0ms");
		}
		return timeString.toString().trim();
	}

}