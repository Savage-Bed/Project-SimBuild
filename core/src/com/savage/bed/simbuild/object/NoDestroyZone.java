package com.savage.bed.simbuild.object;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class NoDestroyZone
{
	public static final int PLAYER_MODE = 1;
	public static final int EDITOR_MODE = 2;
	private static final BoundingBox box = new BoundingBox();
	private static final Vector3 tmp = new Vector3(), tmp2 = new Vector3();
	public int noDestroyMode;
	public int x, y, z, width, height, depth;
	
	public NoDestroyZone() { }
	
	public NoDestroyZone(int mode, int x, int y, int z, int width, int height, int depth)
	{
		noDestroyMode = mode;
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
		this.depth = depth;
	}
	
	public BoundingBox box()
	{
		box.set(tmp.set(x, y, z), tmp2.set(x + width, y + height, z + depth));
		return box;
	}
}
