package com.viduan.entities;

import java.awt.image.BufferedImage;

public class Weapon extends Entity {

	public Weapon(int x, int y, int width, int height, BufferedImage skin,int mwidth,int mheight) {
		super(x, y, width, height, skin, mwidth,mheight);
		
		depth = 0;
	}

}
