package com.savage.bed.simbuild.main;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;

public class SimBuildSettings
{
	/*public enum RenderMode
	{
		FrustumCulling(true), ModelWhenDirty(false);
		public final boolean frustumCull;
		
		private RenderMode(boolean frustumCull)
		{
			this.frustumCull = frustumCull;
		}
	}*/
	
	public static int PAUSEKEY = Keys.ENTER;
	public static int EXITKEY = Keys.ESCAPE;
	public static int FORWARDKEY = Keys.W;
	public static int LEFTKEY = Keys.A;
	public static int BACKWARDKEY = Keys.S;
	public static int RIGHTKEY = Keys.D;
	public static int UPKEY = Keys.R;
	public static int DOWNKEY = Keys.F;
	public static int RUNKEY = Keys.SHIFT_LEFT;
	public static int EFORWARDKEY = Keys.E;
	public static int EBACKWARDKEY = Keys.Q;
	public static int JUMPKEY = Keys.SPACE;
	public static int INVENTORY = Keys.I;
	public static int CHANGEMODE = Keys.SPACE;
	public static int ISELECTNEXT = Keys.DOWN;
	public static int ISELECTPREVIOUS = Keys.UP;
	
	public static int CREATEBLOCK = Buttons.RIGHT;
	public static int DESTROYBLOCK = Buttons.LEFT;
	
	public static int RENDERDIST = 20;
	//public static RenderMode renderMode = RenderMode.ModelWhenDirty;
	
	public static int cubeTexSize = 16;
	public static int cubeAtlasWidth = 48;
	public static int cubeAtlasHeight = 48;
	public static float cubeTexWidth = 0.5f;
	public static float cubeTexHeight = 0.5f;
	
	public static void calcCubeTex()
	{
		cubeTexWidth = (float) cubeTexSize / (float) cubeAtlasWidth;
		cubeTexHeight = (float) cubeTexSize / (float) cubeAtlasHeight;
	}
}
