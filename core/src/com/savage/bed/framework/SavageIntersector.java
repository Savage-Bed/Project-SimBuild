package com.savage.bed.framework;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

public class SavageIntersector
{
	public enum Face
	{
		NONE(false), TOP(true), FRONT(true), BACK(true), BOTTOM(true), LEFT(true), RIGHT(true), CENTER(true);
		public final boolean hit;
		
		private Face(boolean hit)
		{
			this.hit = hit;
		}
	}
	static final Vector3 v0 = new Vector3(), v1 = new Vector3(), v2 = new Vector3(), min = new Vector3(), max = new Vector3();
	
	public static Face intersectRayBox (Ray ray, BoundingBox box, Vector3 intersection) {
		min.set(box.min).add(box.getCenter());
		max.set(box.max).add(box.getCenter());
		v0.set(ray.origin).sub(min);
		v1.set(ray.origin).sub(max);
		
		if (v0.x > 0 && v0.y > 0 && v0.z > 0 && v1.x < 0 && v1.y < 0 && v1.z < 0) {
			return Face.CENTER;
		}
		float lowest = 0, t;
		Face face = Face.NONE;
		
		// min x
		if (ray.origin.x <= min.x && ray.direction.x > 0) {
			t = (min.x - ray.origin.x) / ray.direction.x;
			if (t >= 0) {
				v2.set(ray.direction).scl(t).add(ray.origin);
				if (v2.y >= min.y && v2.y <= max.y && v2.z >= min.z && v2.z <= max.z && (!face.hit || t < lowest)) {
					face = Face.LEFT;
					lowest = t;
				}
			}
		}
		// max x
		if (ray.origin.x >= max.x && ray.direction.x < 0) {
			t = (max.x - ray.origin.x) / ray.direction.x;
			if (t >= 0) {
				v2.set(ray.direction).scl(t).add(ray.origin);
				if (v2.y >= min.y && v2.y <= max.y && v2.z >= min.z && v2.z <= max.z && (!face.hit || t < lowest)) {
					face = Face.RIGHT;
					lowest = t;
				}
			}
		}
		// min y
		if (ray.origin.y <= min.y && ray.direction.y > 0) {
			t = (min.y - ray.origin.y) / ray.direction.y;
			if (t >= 0) {
				v2.set(ray.direction).scl(t).add(ray.origin);
				if (v2.x >= min.x && v2.x <= max.x && v2.z >= min.z && v2.z <= max.z && (!face.hit || t < lowest)) {
					face = Face.BOTTOM;
					lowest = t;
				}
			}
		}
		// max y
		if (ray.origin.y >= max.y && ray.direction.y < 0) {
			t = (max.y - ray.origin.y) / ray.direction.y;
			if (t >= 0) {
				v2.set(ray.direction).scl(t).add(ray.origin);
				if (v2.x >= min.x && v2.x <= max.x && v2.z >= min.z && v2.z <= max.z && (!face.hit || t < lowest)) {
					face = Face.TOP;
					lowest = t;
				}
			}
		}
		// min z
		if (ray.origin.z <= min.z && ray.direction.z > 0) {
			t = (min.z - ray.origin.z) / ray.direction.z;
			if (t >= 0) {
				v2.set(ray.direction).scl(t).add(ray.origin);
				if (v2.x >= min.x && v2.x <= max.x && v2.y >= min.y && v2.y <= max.y && (!face.hit || t < lowest)) {
					face = Face.BACK;
					lowest = t;
				}
			}
		}
		// max z
		if (ray.origin.z >= max.z && ray.direction.z < 0) {
			t = (max.z - ray.origin.z) / ray.direction.z;
			if (t >= 0) {
				v2.set(ray.direction).scl(t).add(ray.origin);
				if (v2.x >= min.x && v2.x <= max.x && v2.y >= min.y && v2.y <= max.y && (!face.hit || t < lowest)) {
					face = Face.FRONT;
					lowest = t;
				}
			}
		}
		if (face.hit && intersection != null) {
			intersection.set(ray.direction).scl(lowest).add(ray.origin);
		}
		return face;
	}
	
	public static boolean intersectRayBounds (Ray ray, BoundingBox box, Vector3 intersection) {
		min.set(box.min).add(box.getCenter());
		max.set(box.max).add(box.getCenter());
		v0.set(ray.origin).sub(min);
		v1.set(ray.origin).sub(max);
		
		if (v0.x > 0 && v0.y > 0 && v0.z > 0 && v1.x < 0 && v1.y < 0 && v1.z < 0) {
			return true;
		}
		float lowest = 0, t;
		boolean hit = false;

		// min x
		if (ray.origin.x <= min.x && ray.direction.x > 0) {
			t = (min.x - ray.origin.x) / ray.direction.x;
			if (t >= 0) {
				v2.set(ray.direction).scl(t).add(ray.origin);
				if (v2.y >= min.y && v2.y <= max.y && v2.z >= min.z && v2.z <= max.z && (!hit || t < lowest)) {
					hit = true;
					lowest = t;
				}
			}
		}
		// max x
		if (ray.origin.x >= max.x && ray.direction.x < 0) {
			t = (max.x - ray.origin.x) / ray.direction.x;
			if (t >= 0) {
				v2.set(ray.direction).scl(t).add(ray.origin);
				if (v2.y >= min.y && v2.y <= max.y && v2.z >= min.z && v2.z <= max.z && (!hit || t < lowest)) {
					hit = true;
					lowest = t;
				}
			}
		}
		// min y
		if (ray.origin.y <= min.y && ray.direction.y > 0) {
			t = (min.y - ray.origin.y) / ray.direction.y;
			if (t >= 0) {
				v2.set(ray.direction).scl(t).add(ray.origin);
				if (v2.x >= min.x && v2.x <= max.x && v2.z >= min.z && v2.z <= max.z && (!hit || t < lowest)) {
					hit = true;
					lowest = t;
				}
			}
		}
		// max y
		if (ray.origin.y >= max.y && ray.direction.y < 0) {
			t = (max.y - ray.origin.y) / ray.direction.y;
			if (t >= 0) {
				v2.set(ray.direction).scl(t).add(ray.origin);
				if (v2.x >= min.x && v2.x <= max.x && v2.z >= min.z && v2.z <= max.z && (!hit || t < lowest)) {
					hit = true;
					lowest = t;
				}
			}
		}
		// min z
		if (ray.origin.z <= min.z && ray.direction.z > 0) {
			t = (min.z - ray.origin.z) / ray.direction.z;
			if (t >= 0) {
				v2.set(ray.direction).scl(t).add(ray.origin);
				if (v2.x >= min.x && v2.x <= max.x && v2.y >= min.y && v2.y <= max.y && (!hit || t < lowest)) {
					hit = true;
					lowest = t;
				}
			}
		}
		// max y
		if (ray.origin.z >= max.z && ray.direction.z < 0) {
			t = (max.z - ray.origin.z) / ray.direction.z;
			if (t >= 0) {
				v2.set(ray.direction).scl(t).add(ray.origin);
				if (v2.x >= min.x && v2.x <= max.x && v2.y >= min.y && v2.y <= max.y && (!hit || t < lowest)) {
					hit = true;
					lowest = t;
				}
			}
		}
		if (hit && intersection != null) {
			intersection.set(ray.direction).scl(lowest).add(ray.origin);
		}
		return hit;
	}
}
