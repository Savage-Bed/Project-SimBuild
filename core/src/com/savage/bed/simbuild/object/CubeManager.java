package com.savage.bed.simbuild.object;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.*;
import com.badlogic.gdx.utils.Array;
import com.savage.bed.simbuild.main.SimBuildSettings;

public final class CubeManager
{
	public static class CubeType
	{
		public final Array<CubeTextureData> texData;
		public final boolean allowEditMode, isLight;
		public final String name;
		public final float strength;
		public final int lightLevel;
		
		public CubeType(String name, boolean allowEditMode, float strength, boolean isLight, int lightLevel, CubeTextureData... textureDatas)
		{
			texData = new Array<CubeTextureData>();
			texData.addAll(textureDatas);
			this.allowEditMode = allowEditMode;
			this.name = name;
			this.strength = strength;
			this.isLight = isLight;
			this.lightLevel = (isLight ? lightLevel : 0);
		}
	}
	
	public static class Cube
	{
		private static final BoundingBox box = new BoundingBox(new Vector3(-0.5f, -0.5f, -0.5f), new Vector3(0.5f, 0.5f, 0.5f));
		private int x, y, z;
		public int lightLevel;
		public String type;
		public final boolean isLight;
		public Cube lightSource;
		
		protected Cube(String type, int x, int y, int z, int lightLevel, boolean isLight)
		{
			this.type = type;
			this.x = x;
			this.y = y;
			this.z = z;
			this.isLight = isLight;
			this.lightLevel = lightLevel;
			if(isLight)
				lightSource = this;
		}
		
		public void setPosition(int x, int y, int z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public BoundingBox box()
		{
			box.getCenter().set(x, y, z);
			return box;
		}
		
		public int x()
		{
			return x;
		}
		
		public int y()
		{
			return y;
		}
		
		public int z()
		{
			return z;
		}
		
		public boolean equals(Cube cube)
		{
			return this == cube;
		}
	}
	
	public static class CubeTextureData
	{
		public static final int FACE_FRONT = 1;
		public static final int FACE_TOP = 2;
		public static final int FACE_BACK = 4;
		public static final int FACE_BOTTOM = 8;
		public static final int FACE_RIGHT = 16;
		public static final int FACE_LEFT = 32;
		public static final int ALL_FACES = 63;
		public static final int SIDE_FACES = 53;
		public static final int TOPBOTTOM_FACES = 10;
		public final int faces,texX, texY, texWidth, texHeight;
		
		public CubeTextureData(int faces, int texX, int texY, int texWidth, int texHeight)
		{
			this.faces = faces;
			this.texX = texX;
			this.texY = texY;
			this.texWidth = texWidth;
			this.texHeight = texHeight;
		}
		
		
	}
	
	public static class IgnoreFaceFlag
	{
		public static final IgnoreFaceFlag IgnoreNone = new IgnoreFaceFlag();
		public static final IgnoreFaceFlag IgnoreAll = new IgnoreFaceFlag(true, true, true, true, true, true);
		
		public boolean front, top, back, bottom, right, left;
		
		public IgnoreFaceFlag() { }
		
		public IgnoreFaceFlag(boolean front, boolean top, boolean back, boolean bottom, boolean right, boolean left)
		{
			setFlags(front, top, back, bottom, right, left);
		}
		
		public IgnoreFaceFlag setFlags(boolean front, boolean top, boolean back, boolean bottom, boolean right, boolean left)
		{
			this.front = front;
			this.top = top;
			this.back = back;
			this.bottom = bottom;
			this.right = right;
			this.left = left;
			
			return this;
		}
		
		public IgnoreFaceFlag resetFlags()
		{
			front = false;
			top = false;
			back = false;
			bottom = false;
			right = false;
			left = false;
			
			return this;
		}
	}
	
	public static int MAXLIGHT = 16;
	public static Material staticMaterial;
	private static Texture texture;
	public static HashMap<String, CubeType> types = new HashMap<String, CubeType>();
	private static VertexInfo bl = new VertexInfo(), br = new VertexInfo(), tl = new VertexInfo(), tr = new VertexInfo();
	private static int x, y, z;
	
	public static void setStatic(Texture tex)
	{
		texture = tex;		
		staticMaterial = new Material(TextureAttribute.createDiffuse(texture));
		
		bl.setCol(1, 1, 1, 1);
		br.setCol(1, 1, 1, 1);
		tl.setCol(1, 1, 1, 1);
		tr.setCol(1, 1, 1, 1);
	}
	
	public static Cube newCube(CubeType type, int x, int y, int z)
	{
		return new Cube(type.name, x, y, z, type.lightLevel, type.isLight);
	}
	
	public static Cube newCube(String typeName, int x, int y, int z)
	{
		CubeType type = getType(typeName);
		return new Cube(typeName, x, y, z, type.lightLevel, type.isLight);
	}
	
	public static CubeType getType(Cube cube)
	{
		if(cube != null)
			return types.get(cube.type);
		else
			return null;
	}
	
	public static CubeType getType(String type)
	{
		return types.get(type);
	}
	
	public static CubeType newCubeType(String name, boolean allowsEditMode, float strength, boolean isLight, int lightLevel, CubeTextureData... textureDatas)
	{
		if(types.containsKey(name))
			return types.get(name);
		else
		{
			CubeType type = new CubeType(name, allowsEditMode, strength, isLight, lightLevel, textureDatas);
			types.put(name, type);
			return type;
		}
	}
	
	public static void genCube(Cube cube, MeshBuilder builder, IgnoreFaceFlag ignoreFlag)
	{
		Array<CubeTextureData> datas = types.get(cube.type).texData;
		x = cube.x;
		y = cube.y;
		z = cube.z;
		
		float ll = (float) cube.lightLevel / (float) MAXLIGHT;
		
		bl.setCol(ll, ll, ll, 1);
		br.setCol(ll, ll, ll, 1);
		tl.setCol(ll, ll, ll, 1);
		tr.setCol(ll, ll, ll, 1);
		
		if(cube.lightSource == null)
			cube.lightLevel = 0;
		
		for(CubeTextureData data : datas)
		{
			int faces = data.faces;
			if(faces > CubeTextureData.ALL_FACES || data.faces < 1)
				Gdx.app.error("Cube Texture Gen", "Cube Data Faces are invalid");
			
			if(faces >= CubeTextureData.FACE_LEFT)
			{
				faces -= 32;
				if(!ignoreFlag.left)
					appendLeftFace(data, builder);
			}
			if(faces >= CubeTextureData.FACE_RIGHT)
			{
				faces -= 16;
				if(!ignoreFlag.right)
					appendRightFace(data, builder);
			}
			if(faces >= CubeTextureData.FACE_BOTTOM)
			{
				faces -= 8;
				if(!ignoreFlag.bottom)
					appendBottomFace(data, builder);
			}
			if(faces >= CubeTextureData.FACE_BACK)
			{
				faces -= 4;
				if(!ignoreFlag.back)
					appendBackFace(data, builder);
			}
			if(faces >= CubeTextureData.FACE_TOP)
			{
				faces -= 2;
				if(!ignoreFlag.top)
					appendTopFace(data, builder);
			}
			if(faces >= CubeTextureData.FACE_FRONT)
			{
				faces -= 1;
				if(!ignoreFlag.front)
					appendFrontFace(data, builder);
			}
			if(faces != 0)
				Gdx.app.error("Cube Texture Gen", "Cube Data Faces are invalid");
		}
	}
	
	public static void appendFrontFace(CubeTextureData data, MeshBuilder builder)
	{
		bl.setPos(-0.5f + x, -0.5f + y, 0.5f + z);
		bl.setNor(0, 0, 1);
		bl.setUV((data.texX * SimBuildSettings.cubeTexWidth), (data.texHeight * SimBuildSettings.cubeTexHeight) + (data.texY * SimBuildSettings.cubeTexHeight));
		
		tl.setPos(-0.5f + x, 0.5f + y, 0.5f + z);
		tl.setNor(0, 0, 1);
		tl.setUV((data.texX * SimBuildSettings.cubeTexWidth), (data.texY * SimBuildSettings.cubeTexHeight));
		
		tr.setPos(0.5f + x, 0.5f + y, 0.5f + z);
		tr.setNor(0, 0, 1);
		tr.setUV((data.texWidth * SimBuildSettings.cubeTexWidth) + (data.texX * SimBuildSettings.cubeTexWidth), (data.texY * SimBuildSettings.cubeTexHeight));
		
		br.setPos(0.5f + x, -0.5f + y, 0.5f + z);
		br.setNor(0, 0, 1);
		br.setUV((data.texWidth * SimBuildSettings.cubeTexWidth) + (data.texX * SimBuildSettings.cubeTexWidth), (data.texHeight * SimBuildSettings.cubeTexHeight) + (data.texY * SimBuildSettings.cubeTexHeight));
		
		builder.rect(bl, br, tr, tl);
	}
	
	public static void appendTopFace(CubeTextureData data, MeshBuilder builder)
	{
		bl.setPos(-0.5f + x, 0.5f + y, -0.5f + z);
		bl.setNor(0, 1, 0);
		bl.setUV((data.texX * SimBuildSettings.cubeTexWidth), (data.texY * SimBuildSettings.cubeTexHeight));
		
		tl.setPos(-0.5f + x, 0.5f + y, 0.5f + z);
		tl.setNor(0, 1, 0);
		tl.setUV((data.texX * SimBuildSettings.cubeTexWidth), (data.texHeight * SimBuildSettings.cubeTexHeight) + (data.texY * SimBuildSettings.cubeTexHeight));
		
		tr.setPos(0.5f + x, 0.5f + y, 0.5f + z);
		tr.setNor(0, 1, 0);
		tr.setUV((data.texWidth * SimBuildSettings.cubeTexWidth) + (data.texX * SimBuildSettings.cubeTexWidth), (data.texHeight * SimBuildSettings.cubeTexHeight) + (data.texY * SimBuildSettings.cubeTexHeight));
		
		br.setPos(0.5f + x, 0.5f + y, -0.5f + z);
		br.setNor(0, 1, 0);
		br.setUV((data.texWidth * SimBuildSettings.cubeTexWidth) + (data.texX * SimBuildSettings.cubeTexWidth), (data.texY * SimBuildSettings.cubeTexHeight));
		
		builder.rect(tl, tr, br, bl);
	}
	
	public static void appendBackFace(CubeTextureData data, MeshBuilder builder)
	{
		br.setPos(-0.5f + x, -0.5f + y, -0.5f + z);
		br.setNor(0, 0, -1);
		br.setUV((data.texWidth * SimBuildSettings.cubeTexWidth) + (data.texX * SimBuildSettings.cubeTexWidth), (data.texHeight * SimBuildSettings.cubeTexHeight) + (data.texY * SimBuildSettings.cubeTexHeight));
		
		tr.setPos(-0.5f + x, 0.5f + y, -0.5f + z);
		tr.setNor(0, 0, -1);
		tr.setUV((data.texWidth * SimBuildSettings.cubeTexWidth) + (data.texX * SimBuildSettings.cubeTexWidth), (data.texY * SimBuildSettings.cubeTexHeight));
		
		tl.setPos(0.5f + x, 0.5f + y, -0.5f + z);
		tl.setNor(0, 0, -1);
		tl.setUV((data.texX * SimBuildSettings.cubeTexWidth), (data.texY * SimBuildSettings.cubeTexHeight));
		
		bl.setPos(0.5f + x, -0.5f + y, -0.5f + z);
		bl.setNor(0, 0, -1);
		bl.setUV((data.texX * SimBuildSettings.cubeTexWidth), (data.texHeight * SimBuildSettings.cubeTexHeight) + (data.texY * SimBuildSettings.cubeTexHeight));
		
		builder.rect(bl, br, tr, tl);
	}
	
	public static void appendBottomFace(CubeTextureData data, MeshBuilder builder)
	{
		bl.setPos(-0.5f + x, -0.5f + y, -0.5f + z);
		bl.setNor(0, -1, 0);
		bl.setUV((data.texWidth * SimBuildSettings.cubeTexWidth) + (data.texX * SimBuildSettings.cubeTexWidth), (data.texY * SimBuildSettings.cubeTexHeight));
		
		tl.setPos(-0.5f + x, -0.5f + y, 0.5f + z);
		tl.setNor(0, -1, 0);
		tl.setUV((data.texWidth * SimBuildSettings.cubeTexWidth) + (data.texX * SimBuildSettings.cubeTexWidth), (data.texHeight * SimBuildSettings.cubeTexHeight) +  (data.texY * SimBuildSettings.cubeTexHeight));
		
		tr.setPos(0.5f + x, -0.5f + y, 0.5f + z);
		tr.setNor(0, -1, 0);
		tr.setUV((data.texX * SimBuildSettings.cubeTexWidth), (data.texHeight * SimBuildSettings.cubeTexHeight) +  (data.texY * SimBuildSettings.cubeTexHeight));
		
		br.setPos(0.5f + x, -0.5f + y, -0.5f + z);
		br.setNor(0, -1, 0);
		br.setUV((data.texX * SimBuildSettings.cubeTexWidth),(data.texY * SimBuildSettings.cubeTexHeight));
		
		builder.rect(bl, br, tr, tl);
	}
	
	public static void appendLeftFace(CubeTextureData data, MeshBuilder builder)
	{
		bl.setPos(-0.5f + x, -0.5f + y, -0.5f + z);
		bl.setNor(-1, 0, 0);
		bl.setUV((data.texX * SimBuildSettings.cubeTexWidth), (data.texHeight * SimBuildSettings.cubeTexHeight) + (data.texY * SimBuildSettings.cubeTexHeight));
		
		br.setPos(-0.5f + x, -0.5f + y, 0.5f + z);
		br.setNor(-1, 0, 0);
		br.setUV((data.texWidth * SimBuildSettings.cubeTexWidth) + (data.texX * SimBuildSettings.cubeTexWidth), (data.texHeight * SimBuildSettings.cubeTexHeight) + (data.texY * SimBuildSettings.cubeTexHeight));
		
		tr.setPos(-0.5f + x, 0.5f + y, 0.5f + z);
		tr.setNor(-1, 0, 0);
		tr.setUV((data.texWidth * SimBuildSettings.cubeTexWidth) + (data.texX * SimBuildSettings.cubeTexWidth), (data.texY * SimBuildSettings.cubeTexHeight));
		
		tl.setPos(-0.5f + x, 0.5f + y, -0.5f + z);
		tl.setNor(-1, 0, 0);
		tl.setUV((data.texX * SimBuildSettings.cubeTexWidth), (data.texY * SimBuildSettings.cubeTexHeight));
		
		builder.rect(bl, br, tr, tl);
	}
	
	public static void appendRightFace(CubeTextureData data, MeshBuilder builder)
	{
		br.setPos(0.5f + x, -0.5f + y, -0.5f + z);
		br.setNor(1, 0, 0);
		br.setUV((data.texWidth * SimBuildSettings.cubeTexWidth) + (data.texX * SimBuildSettings.cubeTexWidth), (data.texHeight * SimBuildSettings.cubeTexHeight) + (data.texY * SimBuildSettings.cubeTexHeight));
		
		bl.setPos(0.5f + x, -0.5f + y, 0.5f + z);
		bl.setNor(1, 0, 0);
		bl.setUV((data.texX * SimBuildSettings.cubeTexWidth), (data.texHeight * SimBuildSettings.cubeTexHeight) + (data.texY * SimBuildSettings.cubeTexHeight));
		
		tl.setPos(0.5f + x, 0.5f + y, 0.5f + z);
		tl.setNor(1, 0, 0);
		tl.setUV((data.texX * SimBuildSettings.cubeTexWidth), (data.texY * SimBuildSettings.cubeTexHeight));
		
		tr.setPos(0.5f + x, 0.5f + y, -0.5f + z);
		tr.setNor(1, 0, 0);
		tr.setUV((data.texWidth * SimBuildSettings.cubeTexWidth) + (data.texX * SimBuildSettings.cubeTexWidth), (data.texY * SimBuildSettings.cubeTexHeight));
		
		builder.rect(bl, br, tr, tl);
	}
	
	public final static void disposeStatic()
	{
		texture.dispose();
	}
}
