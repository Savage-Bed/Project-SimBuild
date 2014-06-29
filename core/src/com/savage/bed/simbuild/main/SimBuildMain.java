package com.savage.bed.simbuild.main;

import com.badlogic.gdx.*;
import com.savage.bed.simbuild.screen.*;

public class SimBuildMain extends Game
{

	@Override
	public void create()
	{
		SimBuildSettings.calcCubeTex();
		setScreen(new MainScreen(this));
	}	
}
