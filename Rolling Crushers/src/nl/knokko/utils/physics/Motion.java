package nl.knokko.utils.physics;

import org.lwjgl.util.vector.Vector3f;

import nl.knokko.render.main.DisplayManager;

public class Motion {
	
	private static final float FACTOR = DisplayManager.FPS_CAP;
	
	//for 10 ticks per second and 5 kg
	// 100 kg * m/s/s --> 10 kg * m/s --> 2 m/s --> 0.2 m/t
	private static final float FORCE_FACTOR = 1 / FACTOR / FACTOR;
	
	private static final float PUNCH_FACTOR = 1 / FACTOR;
	
	private static final float getForceFactor(float mass){
		return FORCE_FACTOR / mass;
	}
	
	private static final float getPunchFactor(float mass){
		return PUNCH_FACTOR / mass;
	}
	
	private float x;
	private float y;
	private float z;
	
	private float mass;
	
	/**
	 * Construct a new Motion with the given start speed and mass
	 * @param mass The mass of the entity that contains this motion
	 * @param startSpeedX The start speed X of this motion, in meter/tick
	 * @param startSpeedY The start speed Y of this motion, in meter/tick
	 * @param startSpeedZ The start speed Z of this motion, in meter/tick
	 */
	public Motion(float mass, float startSpeedX, float startSpeedY, float startSpeedZ) {
		x = startSpeedX;
		y = startSpeedY;
		z = startSpeedZ;
		this.mass = mass;
	}
	
	public Motion(float mass){
		this.mass = mass;
	}
	
	public Motion(){}
	
	public void setMass(float mass){
		this.mass = mass;
	}
	
	@Override
	public String toString(){
		return "Motion:(" + x + "," + y + "," + z + ")";
	}
	
	public void set(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float getX(SpeedUnit unit){
		return x * unit.getFactor();
	}
	
	public float getY(SpeedUnit unit){
		return y * unit.getFactor();
	}
	
	public float getZ(SpeedUnit unit){
		return z * unit.getFactor();
	}
	
	public Vector3f getSpeedVector(SpeedUnit unit){
		return (Vector3f) new Vector3f(x, y, z).scale(unit.getFactor());
	}
	
	public float getSpeed(SpeedUnit unit){
		return (float) Math.sqrt(getX(unit) * getX(unit) + getY(unit) * getY(unit) + getZ(unit) * getZ(unit));
	}
	
	public float getPunch(){
		return getSpeed(SpeedUnit.METER_PER_SECOND) * mass;
	}
	
	public float getPunchX(){
		return Math.abs(getX(SpeedUnit.METER_PER_SECOND)) * mass;
	}
	
	public float getPunchY(){
		return Math.abs(getY(SpeedUnit.METER_PER_SECOND)) * mass;
	}
	
	public float getPunchZ(){
		return Math.abs(getZ(SpeedUnit.METER_PER_SECOND)) * mass;
	}
	
	public void addPunch(float punchX, float punchY, float punchZ){
		if(punchX != punchX || punchY != punchY || punchZ != punchZ)
			throw new IllegalArgumentException("Invalid punch (" + punchX + "," + punchY + "," + punchZ + ")");
		x += punchX * getPunchFactor(mass);
		y += punchY * getPunchFactor(mass);
		z += punchZ * getPunchFactor(mass);
	}
	
	public void addPunch(Vector3f punch){
		addPunch(punch.x, punch.y, punch.z);
	}
	
	public void addPunch(Vector3f punch, float power){
		addPunch(punch.x * power, punch.y * power, punch.z * power);
	}
	
	public void addForce(float forceX, float forceY, float forceZ){
		if(forceX != forceX || forceY != forceY || forceZ != forceZ)
			throw new IllegalArgumentException("Invalid force (" + forceX + "," + forceY + "," + forceZ + ")");
		x += forceX * getForceFactor(mass);
		y += forceY * getForceFactor(mass);
		z += forceZ * getForceFactor(mass);
	}
	
	public void addFrictionForce(float forceX, float forceY, float forceZ){
		boolean px = x > 0;
		boolean py = y > 0;
		boolean pz = z > 0;
		x += forceX * getForceFactor(mass);
		y += forceY * getForceFactor(mass);
		z += forceZ * getForceFactor(mass);
		if(x < 0 && px || x > 0 && !px)
			x = 0;
		if(y < 0 && py || y > 0 && !py)
			y = 0;
		if(z < 0 && pz || z > 0 && !pz)
			z = 0;
	}
	
	public void addForce(Vector3f force){
		addForce(force.x, force.y, force.z);
	}
	
	public void addFrictionForce(Vector3f force){
		addFrictionForce(force.x, force.y, force.z);
	}
	
	public float getMass(){
		return mass;
	}
}
