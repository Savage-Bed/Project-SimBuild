package com.savage.bed.simbuild.main.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.savage.bed.simbuild.main.SimBuildMain;

public class DesktopLauncher
{
	public static void main(String[] args)
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.depth = 8;
		config.width = 800;
		config.height = 480;
		config.foregroundFPS = 0;
		config.backgroundFPS = 25;
		
		new LwjglApplication(new SimBuildMain(), config);
	}
}