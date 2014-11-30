package com.savage.bed.simbuild.controller;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

public class EditorController
{
	PerspectiveCamera camera;
	PlayerController pController;
	
	private Vector3 tmp, xzdir;
	
	public EditorController(PerspectiveCamera camera, PlayerController pController)
	{
		this.camera = camera;
		this.pController = pController;
		tmp = new Vector3();
		xzdir = new Vector3();
	}
	
	public void update(float delta)
	{
		pController.rotateCam();
		
		xzdir.set(camera.direction).y = 0;
		xzdir.nor();
		tmp.set(0, 0, 0);
		
		if(pController.c)
			tmp.add(xzdir);
		if(pController.x)
			tmp.sub(xzdir);
		if(pController.w)
			tmp.add(camera.direction);
		if(pController.a)
			tmp.add(xzdir.z, 0, -xzdir.x);
		if(pController.s)
			tmp.sub(camera.direction);
		if(pController.d)
			tmp.add(-xzdir.z, 0, xzdir.x);
		if(pController.r)
			tmp.add(0, 1, 0);
		if(pController.f)
			tmp.add(0, -1, 0);
		
		camera.translate(tmp.scl(3 * delta * (pController.e ? 25 : 1)));
		camera.update();
	}
}
