package com.savage.bed.simbuild.screen;

import java.util.Random;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.savage.bed.framework.*;
import com.savage.bed.framework.SavageIntersector.Face;
import com.savage.bed.simbuild.controller.*;
import com.savage.bed.simbuild.object.*;
import com.savage.bed.simbuild.object.CubeManager.*;

public class MainScreen extends SavageScreen
{
	AssetManager manager;
	PerspectiveCamera pCam;
	ModelBatch mBatch;
	
	Inventory inventory;
	
	ShapeRenderer hitBoxRender;
	
	Music music;
	
	Vector3 selVec, tmp, tmp2;
	Vector2 tmpV2;
	Cube lastSelect;
	
	BitmapFont font;
	
	PlayerController pController;
	MainController mController;
	EditorController cController;
	
	ChunkManager chunkManager;
	Array<Chunk> chunks;
	Array<Chunk> lightChunks;
	boolean updateLightChunks;
	
	Array<Cube> nearby;
	
	Random random;
		
	boolean load;
	int halfWidth, halfHeight;
	float curStren, fullStren, waitDel, del;
	int pCX, pCZ;
	
	public MainScreen(Game game)
	{
		super(game);
	}
	
	@Override
	public void show()
	{
		manager = new AssetManager();
		pCam = new PerspectiveCamera(67, 800, 480);
		pCam.near = .1f;
		mBatch = new ModelBatch();
		
		pController = new PlayerController(pCam);
		pCam.translate(0, 2, 2);
		pCam.near = 0.1f;
		pCam.far = 150;
		mController = new MainController(pController);
		cController = new EditorController(pCam, pController);
		hitBoxRender = new ShapeRenderer();
		
		chunkManager = new ChunkManager();
		
		selVec = new Vector3();
		tmp = new Vector3();
		tmp2 = new Vector3();
		tmpV2 = new Vector2();
		chunks = new Array<Chunk>();
		lightChunks = new Array<Chunk>();
		nearby = new Array<Cube>();
		
		music = Gdx.audio.newMusic(Gdx.files.internal("audio/main.mp3"));
		music.setLooping(true);
		music.setVolume(0.75f);
		
		font = SavageHelper.genBitmapFont("font/debugFont.ttf", 40, false);
		manager.load("inventory/inventory.pack", TextureAtlas.class);
		
		random = new Random();
		
		halfWidth = 400;
		halfHeight = 240;
		
		load = true;
		
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	@Override
	public void resize(int width, int height)
	{
		pCam.viewportWidth = width;
		pCam.viewportHeight = height;
		pCam.update();
		
		halfWidth = width / 2;
		halfHeight = height / 2;
		if(inventory != null)
			inventory.resize(width, height);
	}
	
	@Override
	public void render(float delta)
	{
		if(!manager.update())
			return;
		if(load)
			load();
		
		if(delta > 0.10f)
			delta = 0.10f;
		if(waitDel > 0)
			waitDel -= delta;
		
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		
		pCX = pController.x() / Chunk.CHUNK_XZ;
		pCZ = pController.z() / Chunk.CHUNK_XZ;
		
		chunkManager.getNearby(pCX, pCZ, 6, chunks, random);
		
		/*if(updateLightChunks)
		{
			lightChunks.clear();
			lightChunks.addAll(chunks);
		}*/
		
		draw(delta);
		
		inventory.update(delta);
		
		if(mController.gamePaused);
		else if(mController.showInventory)
			inventory.showInventory(true);
		else
		{
			if(mController.updatePlayer)
				updatePlayer(delta);
			else
				updateEditor(delta);
		}
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	public synchronized void draw(float delta)
	{
		mBatch.begin(pCam);
		for(Chunk chunk : chunks)
		{
			if(chunk.modelInvalid)
			{
				chunk.buildModel();
			}
			if(pCam.frustum.boundsInFrustum(chunk.x, 0, chunk.z, Chunk.CHUNK_XZ, Chunk.CHUNK_HEIGHT, Chunk.CHUNK_XZ))
			{
				chunk.render(mBatch);
			}
		}
		
		mBatch.end();
		
		if(lastSelect != null)
		{
			float scl = (curStren / fullStren);
			Gdx.gl.glLineWidth(scl * 6);
			hitBoxRender.setProjectionMatrix(pCam.combined);
			hitBoxRender.setColor(scl, scl, scl, 1);
			hitBoxRender.begin(ShapeType.Line);
			hitBoxRender.box(selVec.x - (0.5f * scl), selVec.y - (0.5f * scl), selVec.z + (0.5f * scl), scl, scl, scl);
			hitBoxRender.end();
		}
		else if(!mController.updatePlayer)
		{
			Gdx.gl.glLineWidth(1);
			hitBoxRender.setProjectionMatrix(pCam.combined);
			hitBoxRender.setColor(0, 0, 0, 1);
			hitBoxRender.begin(ShapeType.Line);
			hitBoxRender.box(selVec.x - 0.5f, selVec.y - 0.5f, selVec.z + 0.5f, 1, 1, 1);
			hitBoxRender.end();
		}
	}
	
	public void updatePlayer(float delta)
	{
		inventory.showInventory(false);
		
		nearby.clear();
		
		for(Chunk chunk : chunks)
			chunk.getNearby(pController.x(), pController.y(), pController.z(), nearby);
		
		pController.update(nearby, delta);
		
		pController.updateCamPos();
		
		int pX = pController.x();
		int pY = pController.y();
		int pZ = pController.z();
		
		inventory.setPos(pX, pY, pZ, pX / Chunk.CHUNK_XZ, pY / Chunk.CHUNK_HEIGHT, pZ / Chunk.CHUNK_XZ);
		
		Chunk chunk = chunkManager.getChunkAbs(pX, pZ);
		Cube c = null;
		if(chunk != null && pY - 1 >= 0 && pY - 1 < Chunk.CHUNK_HEIGHT)
			c = chunk.getCubeFastAbs(pX, pY - 1, pZ);
		if(c != null)
			pController.editAvailable = CubeManager.getType(c).allowEditMode;
		
		Ray ray = pCam.getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		
		int index = -1, tmpIndex = 0;
		float tmpDist, dist = 20;
		Face tmpF = Face.NONE, face = Face.NONE;
		for(Cube cube : nearby)
		{
			tmpF = SavageIntersector.intersectRayBox(ray, cube.box(), tmp2);
			if(tmpF.hit)
			{
				tmpDist = tmp.set(cube.x(), cube.y(), cube.z()).dst2(pX, pY, pZ);
				if(tmpDist < dist)
				{
					dist = tmpDist;
					index = tmpIndex;
					face = tmpF;
				}
			}
			tmpIndex++;
		}
		int x, y, z;
		if(index != -1)
		{
			c = nearby.get(index);
			x = c.x();
			y = c.y();
			z = c.z();
			chunk = chunkManager.getChunkAbs(x, z);
			selVec.set(x, y, z);
		}
		else
		{
			ray.getEndPoint(selVec, 2);
			x = Math.round(selVec.x);
			y = Math.round(selVec.y);
			z = Math.round(selVec.z);
			selVec.set(x, y, z);
			
			chunk = chunkManager.getChunkAbs(x, z);
			if(chunk != null && y >= 0 && y < Chunk.CHUNK_HEIGHT)
				c = chunk.getCubeFastAbs(x, y, z);
		}
		
		if(c != null)
		{
			fullStren = CubeManager.getType(c).strength;
			if(mController.destroy)
			{
				if(!c.equals(lastSelect))
					curStren = fullStren;
				else
					curStren -= delta;
				if(curStren <= 0)
				{
					inventory.addCube(c.type);
					if(chunk != null)
						chunk.deleteCubeFastAbs(x, y, z);
				}
			}
			else if(mController.create)
			{
				if(inventory.hasCube(mController.selected))
				{
					switch(face)
					{
					case CENTER:
						break;
					case NONE:
						break;
					case TOP:
						x = (int) selVec.x;
						y = (int) selVec.y + 1;
						z = (int) selVec.z;
						break;
					case BOTTOM:
						x = (int) selVec.x;
						y = (int) selVec.y - 1;
						z = (int) selVec.z;
						break;
					case FRONT:
						x = (int) selVec.x;
						y = (int) selVec.y;
						z = (int) selVec.z + 1;
						break;
					case BACK:
						x = (int) selVec.x;
						y = (int) selVec.y;
						z = (int) selVec.z - 1;
						break;
					case RIGHT:
						x = (int) selVec.x + 1;
						y = (int) selVec.y;
						z = (int) selVec.z;
						break;
					case LEFT:
						x = (int) selVec.x - 1;
						y = (int) selVec.y;
						z = (int) selVec.z;
						break;
					}
					
					chunk = chunkManager.getChunkAbs(x, z);
					if(chunk != null)
						c = chunk.getCubeFastAbs(x, y, z);
					selVec.set(x, y, z);
					if(pController.cubeNotInPos(x, y, z) && c == null && waitDel <= 0)
					{
						inventory.useCube(mController.selected);
						chunk.addCubeFastAbs(CubeManager.newCube(inventory.cubeTypes.get(mController.selected).getName(), x, y, z));
						waitDel = 0.2f;
					}
				}
			}
			else
				curStren = fullStren;
		}
		lastSelect = c;
	}
	
	public void updateEditor(float delta)
	{
		nearby.clear();
		
		for(Chunk chunk : chunks)
			chunk.getNearby(pController.x(), pController.y(), pController.z(), nearby);
		
		inventory.showInventory(true);
		cController.update(delta);
		
		int pX = pController.x();
		int pY = pController.y();
		int pZ = pController.z();
						
		inventory.setPos(pX, pY, pZ, pX / Chunk.CHUNK_XZ, pY / Chunk.CHUNK_HEIGHT, pZ / Chunk.CHUNK_XZ);
		
		Ray ray = pCam.getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		int index = -1, tmpIndex = 0;;
		float tmpDist, dist = 20;
		Face tmpF = Face.NONE, face = Face.NONE;
		for(Cube cube : nearby)
		{
			tmpF = SavageIntersector.intersectRayBox(ray, cube.box(), tmp2);
			if(tmpF.hit)
			{
				tmpDist = tmp.set(cube.x(), cube.y(), cube.z()).dst2(pX, pY, pZ);
				if(tmpDist < dist)
				{
					dist = tmpDist;
					index = tmpIndex;
					face = tmpF;
				}
			}
			tmpIndex++;
		}
		Chunk chunk;
		Cube c = null;
		int x, y, z;
		if(index != -1)
		{
			c = nearby.get(index);
			x = c.x();
			y = c.y();
			z = c.z();
			chunk = chunkManager.getChunkAbs(x, z);
			selVec.set(x, y, z);
			
			curStren = fullStren = CubeManager.getType(c).strength;
		}
		else
		{
			ray.getEndPoint(selVec, 2);
			x = Math.round(selVec.x);
			y = Math.round(selVec.y);
			z = Math.round(selVec.z);
			selVec.set(x, y, z);
			
			chunk = chunkManager.getChunkAbs(x, z);
			if(chunk != null)
				c = chunk.getCubeFastAbs(x, y, z);
		}
		
		lastSelect = c;
		
		if(mController.create && c == null && chunk != null)
			chunk.addCubeFastAbs(CubeManager.newCube(inventory.cubeTypes.get(mController.selected).getName(), x, y, z));
		else if(c != null && waitDel <= 0)
		{
			if(mController.destroy)
			{
				chunk.deleteCubeFastAbs(x, y, z);
				waitDel = 0.3f;
			}
			else if(mController.create)
			{
				switch(face)
				{
				case CENTER:
					break;
				case NONE:
					break;
				case TOP:
					x = (int) selVec.x;
					y = (int) selVec.y + 1;
					z = (int) selVec.z;
					break;
				case BOTTOM:
					x = (int) selVec.x;
					y = (int) selVec.y - 1;
					z = (int) selVec.z;
					break;
				case FRONT:
					x = (int) selVec.x;
					y = (int) selVec.y;
					z = (int) selVec.z + 1;
					break;
				case BACK:
					x = (int) selVec.x;
					y = (int) selVec.y;
					z = (int) selVec.z - 1;
					break;
				case RIGHT:
					x = (int) selVec.x + 1;
					y = (int) selVec.y;
					z = (int) selVec.z;
					break;
				case LEFT:
					x = (int) selVec.x - 1;
					y = (int) selVec.y;
					z = (int) selVec.z;
					break;
				}
				
				chunk = chunkManager.getChunkAbs(x, z);
				if(chunk != null)
					c = chunk.getCubeFastAbs(x, y, z);
				selVec.set(x, y, z);
				if(pController.cubeNotInPos(x, y, z) && c == null)
				{
					chunk.addCubeFastAbs(CubeManager.newCube(inventory.cubeTypes.get(mController.selected).getName(), x, y, z));
					waitDel = 0.3f;
				}
			}
		}
	}
	
	private void load()
	{
		Texture tex = new Texture(Gdx.files.internal("texturePack/simple.png"));
		CubeManager.setStatic(tex);
		Skin inventorySkin = new Skin(manager.get("inventory/inventory.pack", TextureAtlas.class));
		inventorySkin.add("debug", font);
		inventory = new Inventory(mController, inventorySkin);
		Gdx.input.setInputProcessor(new InputMultiplexer(mController, pController, inventory.stage));
		
		Chunk.parent = chunkManager;
		
		generateCubeTypes();
		generateWorld();
		
		inventory.setCubeTypes();
		
		inventory.addCubes("god", 50);
		
		music.play();
		load = false;
	}
	
	private void generateCubeTypes()
	{
		CubeManager.newCubeType("dirt", false, 0.5f, false, 0, new CubeTextureData(CubeTextureData.ALL_FACES, 0, 1, 1, 1));
		CubeManager.newCubeType("grass", false, 0.65f, false, 0, new CubeTextureData(CubeTextureData.FACE_TOP, 0, 0, 1, 1), new CubeTextureData(CubeTextureData.SIDE_FACES, 0, 0, 1, 2), new CubeTextureData(CubeTextureData.FACE_BOTTOM, 0, 1, 1, 1));
		CubeManager.newCubeType("leaves", false, 0.1f, false, 0, new CubeTextureData(CubeTextureData.ALL_FACES, 2, 0, 1, 1));
		CubeManager.newCubeType("wood", false, 2.2f, false, 0, new CubeTextureData(CubeTextureData.SIDE_FACES, 1, 0, 1, 1), new CubeTextureData(CubeTextureData.TOPBOTTOM_FACES, 1, 1, 1, 1));
		CubeManager.newCubeType("stone", false, 6, false, 0, new CubeTextureData(CubeTextureData.ALL_FACES, 0, 2, 1, 1));
		CubeManager.newCubeType("sand", false, 0.7f, false, 0, new CubeTextureData(CubeTextureData.ALL_FACES, 1, 2, 1, 1));
		CubeManager.newCubeType("tar", false, 10, false, 0, new CubeTextureData(CubeTextureData.ALL_FACES, 2, 1, 1, 1));
		CubeManager.newCubeType("god", true, 0.01f, true, CubeManager.MAXLIGHT, new CubeTextureData(CubeTextureData.ALL_FACES, 2, 2, 1, 1));
	}
	
	private void generateWorld()
	{
		int i, t;
		Array<Cube> cubes = ObjectCreator.genPerlinIsland(-7 * Chunk.CHUNK_XZ, 0, -7 * Chunk.CHUNK_XZ, 15 * Chunk.CHUNK_XZ, 5, 16, 15 * Chunk.CHUNK_XZ, 4, random);
		chunkManager.allocateChunks(-14, -14, 15, 15);
		chunkManager.addAllCubesAbs(cubes);
		for(i = -7; i <= 7; i++)
		{
			for(t = -7; t <= 7; t++)
			{
				Chunk c = chunkManager.getChunk(i, t);
				ObjectCreator.generateRandomTrees(c, random);
			}
		}
	}
	
	@Override
	public void hide()
	{
		inventory.dispose();
		mBatch.dispose();
		manager.dispose();
		music.dispose();
		CubeManager.disposeStatic();
	}
}
