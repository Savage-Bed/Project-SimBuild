package com.savage.bed.simbuild.object;

import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.savage.bed.simbuild.controller.MainController;

public class Inventory
{
	public final Stage stage;
	Window inventory;
	TextField property, fps, pos;
	public final HashMap<String, Integer> typeToIndex;
	public final Array<TextButton> cubeTypes;
	public final IntArray cubeCount;
	Skin inventorySkin;
	MainController mController;
	float lastIX, lastIY;
	int lastSelected;
	
	public Inventory(MainController mController, Skin inventorySkin)
	{
		this.inventorySkin = inventorySkin;
		this.mController = mController;
		
		inventorySkin.getFont("debug").setScale(0.05f);
		
		stage = new Stage(new ExtendViewport(80, 48));
		
		TextFieldStyle tfStyle = new TextFieldStyle();
		tfStyle.font = inventorySkin.getFont("debug");
		tfStyle.fontColor = Color.WHITE;
		
		WindowStyle wStyle = new WindowStyle();
		wStyle.background = new NinePatchDrawable(inventorySkin.getPatch("invBut"));
		wStyle.titleFont = inventorySkin.getFont("debug");
		
		property = new TextField("Property of Timothy Bednarzyk and Dylan Savageau", tfStyle);
		property.setDisabled(true);
		property.setSize(52, 4);
		property.setScale(0.5f);
		property.setPosition(29.5f, 0.1f);
		stage.addActor(property);
		
		fps = new TextField("", tfStyle);
		fps.setSize(10, 4);
		fps.setPosition(0, 45.6f);
		stage.addActor(fps);
		
		pos = new TextField("", tfStyle);
		pos.setSize(30, 4);
		pos.setPosition(0, 43);
		stage.addActor(pos);
		
		inventory = new Window("Inventory", wStyle);
		inventory.setSize(20, 34);
		inventory.setPosition(50, 6);
		lastIX = 50;
		lastIY = 6;
		inventory.padTop(1);
		inventory.setVisible(false);
		inventory.setLayoutEnabled(false);
		stage.addActor(inventory);
		
		cubeTypes = new Array<TextButton>();
		cubeCount = new IntArray();
		typeToIndex = new HashMap<String, Integer>();
	}
	
	public void setCubeTypes()
	{
		TextButtonStyle tbStyle = new TextButtonStyle();
		tbStyle.font = inventorySkin.getFont("debug");
		tbStyle.fontColor = Color.WHITE;
		tbStyle.checkedFontColor = Color.CYAN;
		
		Iterator<String> names = CubeManager.types.keySet().iterator();
		
		for(int i = 0; names.hasNext(); i++)
		{
			String name = names.next();
			TextButton toAdd = new TextButton("(0) " + name.toUpperCase(), tbStyle);
			toAdd.setName(name);
			toAdd.setSize(16, 4);
			toAdd.getLabel().setAlignment(Align.left);
			toAdd.setPosition(2, 28 - (4 * i));
			cubeTypes.add(toAdd);
			inventory.add(toAdd);
			cubeCount.add(0);
			typeToIndex.put(name, i);
		}
	}
	
	public void addCube(String type)
	{
		int index = typeToIndex.get(type);
		int count = cubeCount.get(index);
		count += 1;
		cubeCount.set(index, count);
		cubeTypes.get(index).setText("(" + count + ") " + cubeTypes.get(index).getName().toUpperCase());
	}
	
	public void addCube(int index)
	{
		int count = cubeCount.get(index);
		count += 1;
		cubeCount.set(index, count);
		cubeTypes.get(index).setText("(" + count + ") " + cubeTypes.get(index).getName().toUpperCase());
	}
	
	public void addCubes(String type, int amount)
	{
		int index = typeToIndex.get(type);
		int count = cubeCount.get(index);
		count += amount;
		cubeCount.set(index, count);
		cubeTypes.get(index).setText("(" + count + ") " + cubeTypes.get(index).getName().toUpperCase());
	}
	
	public void addCubes(int index, int amount)
	{
		int count = cubeCount.get(index);
		count += amount;
		cubeCount.set(index, count);
		cubeTypes.get(index).setText("(" + count + ") " + cubeTypes.get(index).getName().toUpperCase());
	}
	
	public boolean useCube(String type)
	{
		int index = typeToIndex.get(type);
		int count = cubeCount.get(index);
		if(count == 0)
			return false;
		count -= 1;
		cubeCount.set(index, count);
		cubeTypes.get(index).setText("(" + count + ") " + cubeTypes.get(index).getName().toUpperCase());
		return true;
	}
	
	public boolean useCube(int index)
	{
		int count = cubeCount.get(index);
		if(count == 0)
			return false;
		count -= 1;
		cubeCount.set(index, count);
		cubeTypes.get(index).setText("(" + count + ") " + cubeTypes.get(index).getName().toUpperCase());
		return true;
	}
	
	public boolean hasCube(String type)
	{
		int index = typeToIndex.get(type);
		int count = cubeCount.get(index);
		return count > 0;
	}
	
	public boolean hasCube(int index)
	{
		int count = cubeCount.get(index);
		return count > 0;
	}
	
	public int getIndex(String type)
	{
		return typeToIndex.get(type);
	}
	
	public void showInventory(boolean show)
	{
		inventory.setVisible(show);
	}
	
	public void setPos(int pX, int pY, int pZ, int cX, int cY, int cZ)
	{
		pos.setText(pX + ", " + pY + ", " + pZ + ": " + cX + ", " + cY + ", " + cZ);
	}
	
	public void resize(int width, int height)
	{
		stage.getViewport().update(width, height);
	}
	
	public void update(float delta)
	{
		stage.act(delta);
		
		if(mController.showInventory)
		{
			lastIX = inventory.getX();
			lastIY = inventory.getY();
		}
		else
			inventory.setPosition(lastIX, lastIY);
		
		cubeTypes.get(mController.selected).setChecked(true);
		if(lastSelected != mController.selected)
			cubeTypes.get(lastSelected).setChecked(false);
		lastSelected = mController.selected;
		
		fps.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
		
		stage.draw();
	}
	
	public void dispose()
	{
		stage.dispose();
	}
}
