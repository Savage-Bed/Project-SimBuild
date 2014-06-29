package com.savage.bed.framework;

import com.badlogic.gdx.*;

public abstract class SavageScreen extends ScreenAdapter
{
	protected final Game game;
	
	public SavageScreen(Game game)
	{
		this.game = game;
	}
}
