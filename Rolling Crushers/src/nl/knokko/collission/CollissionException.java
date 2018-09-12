package nl.knokko.collission;

public class CollissionException extends RuntimeException {

	/**
	 * The serial version ID?
	 */
	private static final long serialVersionUID = 3275831784448586883L;

	public CollissionException(String message) {
		super(message);
	}
	
	public CollissionException(Class<? extends Collider> calledClass, Class<? extends Collider> targetClass){
		this("Can't check for collission between class " + calledClass.getName() + " and class " + targetClass.getName());
	}
}
