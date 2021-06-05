package com.viduan.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.viduan.main.Game;

public class Tile {

	public static BufferedImage TILE_FLOOR = Game.skin.getSprite(0,0, 16,16);
	public static BufferedImage TILE_WALL = Game.skin.getSprite(16,0, 16,16);
	public static BufferedImage TILE_FLOOR2 = Game.skin.getSprite(16, 16, 16, 16);
	
	public boolean show = false;
	
	private BufferedImage skin;
	private int x,y;
	
	public Tile(int x, int y, BufferedImage skin) {
		this.x = x;
		this.y = y;
		this.skin = skin;
	}
	
	public void render(Graphics g) {
		if(show) {
			g.drawImage(skin, x - Camera.x, y - Camera.y, null);
		}
	}
	
}
