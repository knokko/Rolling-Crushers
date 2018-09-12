package nl.knokko.entity.category;

import nl.knokko.collission.Collider;

public interface ICollidingEntity {
	
	Collider getCollider();
	
	void addPunch(float punchX, float punchY, float punchZ);
	
	float getBounceFactor();
}
