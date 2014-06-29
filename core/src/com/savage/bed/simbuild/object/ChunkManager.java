package com.savage.bed.simbuild.object;

import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.utils.Array;
import com.savage.bed.simbuild.object.CubeManager.Cube;

public class ChunkManager
{
	private HashMap<String, Chunk> chunks;
	
	public ChunkManager()
	{
		chunks = new HashMap<String, Chunk>();
	}
	
	public void addChunk(Chunk chunk)
	{
		chunks.put(chunk.cx + " " + chunk.cz, chunk);
	}
	
	public Chunk allocateChunk(int x, int z)
	{
		Chunk chunk = new Chunk(x, z);
		addChunk(chunk);
		return chunk;
	}
	
	public void allocateChunks(int startX, int startZ, int endX, int endZ)
	{
		for(int x = startX; x <= endX; x++)
			for(int z = startZ; z <= endZ; z++)
				allocateChunk(x, z);
	}
	
	public Chunk getChunk(int x, int z)
	{
		return chunks.get(x + " " + z);
	}
	
	public Chunk getChunkAbs(int x, int z)
	{
		int u = (x < 0 ? ((x + 1) / Chunk.CHUNK_XZ) - 1 : x / Chunk.CHUNK_XZ);
		int w = (z < 0 ? ((z + 1) / Chunk.CHUNK_XZ) - 1 : z / Chunk.CHUNK_XZ);
		return getChunk(u, w);
	}
	
	public Cube getCubeCarefulAbs(int x, int y, int z)
	{
		Chunk chunk = getChunkAbs(x, z);
		return (chunk == null ? null : chunk.getCubeAbs(x, y, z));
	}
	
	public Cube getCubeAbs(int x, int y, int z)
	{
		Chunk chunk = getChunkAbs(x, z);
		return (chunk == null ? null : chunk.getCubeFastAbs(x, y, z));
	}
	
	public Cube getCubeFastAbs(int x, int y, int z)
	{
		return getChunkAbs(x, z).getCubeFastAbs(x, y, z);
	}
	
	public void addAllCubesAbs(Array<Cube> cubes)
	{
		for(Cube cube : cubes)
		{
			getChunkAbs(cube.x(), cube.z()).addCubeFastAbs(cube);
		}
	}
	
	public void getNearby(int x, int z, int dist, Array<Chunk> near, Random random)
	{
		near.clear();
		for(int u = x - dist; u < x + dist; u++)
			for(int w = z - dist; w < z + dist; w++)
			{
				Chunk nearby = getChunk(u, w);
				if(nearby != null)
					near.add(nearby);
				else
				{
					Chunk c = allocateChunk(u, w);
					c.addAllFastRel(ObjectCreator.genPerlinIsland(0, 0, 0, 16, 5, 25, 16, 5, random));
					near.add(c);
				}
			}
	}
}
