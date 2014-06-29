package com.savage.bed.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.graphics.g3d.environment.*;
import com.badlogic.gdx.math.*;

public class SavageHelper
{
	public static final String SAVAGE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890:,-()";
	
	/**Creates a default Environment for rendering*/
	public static Environment buildDefaultEnvironment()
	{
		Environment env = new Environment();
		
		env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1));
		env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1, -0.8f, -0.2f));
		return env;
	}
	
	/**Returns a position that will allow multiple lines of text to be centered to the given position*/
	public static Vector2 getMultiLineTextCenterPosition(String text, BitmapFont font, float centerX, float centerY)
	{
		TextBounds bounds = font.getMultiLineBounds(text);
		return new Vector2(centerX - (bounds.width / 2), centerY + (bounds.height / 2));
	}
	
	/**Returns a position that will allow multiple lines of text to be centered to the given position*/
	public static Vector2 getMultiLineTextCenterPosition(String text, BitmapFont font, Vector2 position)
	{
		TextBounds bounds = font.getMultiLineBounds(text);
		return new Vector2(position.x - (bounds.width / 2), position.y + (bounds.height / 2));
	}
	
	/**Returns a position that will allow a single line of text to be centered to the given position*/
	public static Vector2 getTextCenterPosition(String text, BitmapFont font, float centerX, float centerY)
	{
		TextBounds bounds = font.getBounds(text);
		return new Vector2(centerX - (bounds.width / 2), centerY + (bounds.height / 2));
	}
	
	/**Returns a position that will allow a single line of text to be centered to the given position*/
	public static Vector2 getTextCenterPosition(String text, BitmapFont font, Vector2 position)
	{
		TextBounds bounds = font.getBounds(text);
		return new Vector2(position.x - (bounds.width / 2), position.y + (bounds.height / 2));
	}
	
	/**Returns a position that will allow a single line of text to be centered to the given position*/
	public static Vector2 getTextCenterPosition(String text, BitmapFontCache fontCache, float centerX, float centerY)
	{
		TextBounds bounds = fontCache.getFont().getBounds(text);
		return new Vector2(centerX - (bounds.width / 2), centerY + (bounds.height / 2));
	}
	
	/**Returns a position that will allow a single line of text to be centered to the given position*/
	public static Vector2 getTextCenterPosition(String text, BitmapFontCache fontCache, Vector2 position)
	{
		TextBounds bounds = fontCache.getFont().getBounds(text);
		return new Vector2(position.x - (bounds.width / 2), position.y + (bounds.height / 2));
	}
	
	/**Sets a position that will allow a single line of text to be centered to the given position*/
	public static void getTextCenterPosition(String text, BitmapFont font, float centerX, float centerY, Vector2 toSet)
	{
		TextBounds bounds = font.getBounds(text);
		toSet.set(centerX - (bounds.width / 2), centerY + (bounds.height / 2));
	}
	
	/**Sets a position that will allow a single line of text to be centered to the given position*/
	public static void getTextCenterPosition(String text, BitmapFont font, Vector2 position, Vector2 toSet)
	{
		TextBounds bounds = font.getBounds(text);
		toSet.set(position.x - (bounds.width / 2), position.y + (bounds.height / 2));
	}
	
	/**Sets a position that will allow a single line of text to be centered to the given position*/
	public static void getTextCenterPosition(String text, BitmapFontCache fontCache, float centerX, float centerY, Vector2 toSet)
	{
		TextBounds bounds = fontCache.getFont().getBounds(text);
		toSet.set(centerX - (bounds.width / 2), centerY + (bounds.height / 2));
	}
	
	/**Sets a position that will allow a single line of text to be centered to the given position*/
	public static void getTextCenterPosition(String text, BitmapFontCache fontCache, Vector2 position, Vector2 toSet)
	{
		TextBounds bounds = fontCache.getFont().getBounds(text);
		toSet.set(position.x - (bounds.width / 2), position.y + (bounds.height / 2));
	}
	
	public static float getTextWidth(String text, BitmapFont font)
	{
		TextBounds bounds = font.getBounds(text);
		return bounds.width;
	}
	
	public static float getMultiLineTextWidth(String text, BitmapFont font)
	{
		TextBounds bounds = font.getMultiLineBounds(text);
		return bounds.width;
	}
	
	public static float getTextWidth(String text, BitmapFontCache fontCache)
	{
		TextBounds bounds = fontCache.getFont().getBounds(text);
		return bounds.width;
	}
	
	public static float getTextHeight(String text, BitmapFont font)
	{
		TextBounds bounds = font.getBounds(text);
		return bounds.height;
	}
	
	public static float getMultiLineTextHeight(String text, BitmapFont font)
	{
		TextBounds bounds = font.getMultiLineBounds(text);
		return bounds.height;
	}
	
	public static float getTextHeight(String text, BitmapFontCache fontCache)
	{
		TextBounds bounds = fontCache.getFont().getBounds(text);
		return bounds.height;
	}
	
	/**Shortens the amount of code needed to generate a FreeTypeFont*/
	public static BitmapFont genBitmapFont(String ttfLoc, int size, boolean allChars)
	{
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(ttfLoc));
		FreeTypeFontParameter param = new FreeTypeFontParameter();
		if(!allChars)
			param.characters = SAVAGE_CHARS;
		param.size = size;
		param.minFilter = TextureFilter.Linear;
		param.magFilter = TextureFilter.Linear;
		
		BitmapFont toReturn = gen.generateFont(param);
		gen.dispose();
		return toReturn;
	}
	
	/**Shortens the amount of code needed to generate a FreeTypeFont*/
	public static BitmapFont genBitmapFont(FileHandle ttfFile, int size, boolean allChars)
	{
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(ttfFile);
		FreeTypeFontParameter param = new FreeTypeFontParameter();
		if(!allChars)
			param.characters = SAVAGE_CHARS;
		param.size = size;
		param.minFilter = TextureFilter.Linear;
		param.magFilter = TextureFilter.Linear;
		
		BitmapFont toReturn = gen.generateFont(param);
		gen.dispose();
		return toReturn;
	}
	
	/**Shortens the amount of code needed to generate a FreeTypeFont*/
	public static BitmapFont genBitmapFont(FreeTypeFontGenerator gen, int size, boolean allChars)
	{
		FreeTypeFontParameter param = new FreeTypeFontParameter();
		if(!allChars)
			param.characters = SAVAGE_CHARS;
		param.size = size;
		param.minFilter = TextureFilter.Linear;
		param.magFilter = TextureFilter.Linear;
		return gen.generateFont(param);
	}
}
