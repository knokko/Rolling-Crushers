package nl.knokko.utils;

public class Options {

	public static float getScrollSensitivity() {
		return 0.001f;
	}

	public static float getZoomSensitivity() {
		return getScrollSensitivity() * 5;
	}

	public static float getMouseSensitivity() {
		return 0.25f;
	}

}
