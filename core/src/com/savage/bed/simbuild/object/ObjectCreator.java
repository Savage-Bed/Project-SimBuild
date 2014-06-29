package com.savage.bed.simbuild.object;

import java.util.*;

import com.badlogic.gdx.utils.*;
import com.savage.bed.framework.NoiseGenerator;
import com.savage.bed.simbuild.object.CubeManager.Cube;

public class ObjectCreator
{	
	private static int u, v, w;
	public static Array<Cube> genTree(int x, int y, int z, Random random)
	{	
		Array<Cube> toReturn = new Array<Cube>();
		
		int height = random.nextInt(3) + 4;
		
		for(v = 0; v < height; v++)
		{
			toReturn.add(CubeManager.newCube("wood", x, y + v, z));
		}
		toReturn.add(CubeManager.newCube("leaves", x, y + height, z));
		for(v = height - 1; v > height - 3; v--)
		{
			toReturn.add(CubeManager.newCube("leaves", x + 1, y + v, z));
			toReturn.add(CubeManager.newCube("leaves", x, y + v, z + 1));
			toReturn.add(CubeManager.newCube("leaves", x - 1, y + v, z));
			toReturn.add(CubeManager.newCube("leaves", x, y + v, z - 1));
		}
		
		return toReturn;
	}
	
	public static Array<Cube> genIsland(int x, int y, int z, int width, int height, int depth, String topType, String otherType)
	{
		Array<Cube> toReturn = new Array<Cube>();
		for(u = 0; u < width; u++)
		{
			for(v = 0; v < depth; v++)
			{
				for(w = 0; w < height; w++)
				{
					if(w == height - 1)
					{
						toReturn.add(CubeManager.newCube(topType, u + x, w + y, v + z));
					}
					else
						toReturn.add(CubeManager.newCube(otherType, u + x, w + y, v + z));
				}
			}
		}
		
		return toReturn;
	}
	
	public static Array<Cube> genPerlinIsland(int x, int y, int z, int width, int minHeight, int maxHeight, int depth, int octave, Random random)
	{	
		Array<Cube> toReturn = new Array<Cube>();
		
		int scalar = maxHeight - minHeight;
		float[][] perlinNoise = NoiseGenerator.generatePerlinNoise(NoiseGenerator.generateWhiteNoise(width, depth, random), octave, scalar, minHeight);
		
		int noise;
		
		for(u = 0; u < width; u++)
		{
			for(w = 0; w < depth; w++)
			{
				noise = Math.round(perlinNoise[u][w]);
				toReturn.add(CubeManager.newCube("grass", u + x, noise + y, w + z));
				
				for(v = noise - 1; v > noise - (5 > minHeight ? minHeight : 5); v--)
					toReturn.add(CubeManager.newCube("dirt", u + x, v, w + z));
			}
		}
		
		return toReturn;
	}
	
	public static void generateRandomTrees(Chunk chunk, Random random)
	{
		Array<Cube> toAdd = new Array<Cube>();
		for(u = 1; u < Chunk.CHUNK_XZ - 1; u++)
		{
			for(w = 1; w < Chunk.CHUNK_XZ - 1; w++)
			{
				int h = chunk.getHeightRel(u, w);
				if(chunk.getCubeFastRel(u, h, w).type == "leaves")
					continue;
				if(random.nextFloat() > 0.99f && h > 0)
					toAdd.addAll(genTree(u, h + 1, w, random));
			}
		}
		chunk.addAllRel(toAdd);
	}
}
