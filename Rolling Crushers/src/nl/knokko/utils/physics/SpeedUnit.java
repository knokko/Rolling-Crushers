package nl.knokko.utils.physics;

import nl.knokko.render.main.DisplayManager;

public enum SpeedUnit {
	
	METER_PER_TICK(1),
	METER_PER_SECOND(DisplayManager.FPS_CAP);
	
	SpeedUnit(float factor){
		this.factor = factor;
	}
	
	private final float factor;
	
	/**
	 * 
	 * @return
	 */
	public float getFactor(){
		return factor;
	}
}
