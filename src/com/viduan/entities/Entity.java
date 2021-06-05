package com.viduan.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;

import com.viduan.main.Game;
import com.viduan.world.Camera;
import com.viduan.world.Node;
import com.viduan.world.Vector2i;
import com.viduan.world.World;

public class Entity {

	protected int maskx,masky,mwidth,mheight;
	
	public static BufferedImage LIFEPACK_EN = Game.skin.getSprite(6*16, 0, 16, 16);
	public static BufferedImage ENEMY_EN = Game.skin.getSprite(7*16, 16, 16, 16);
	public static BufferedImage ENEMY2_EN = Game.skin.getSprite(7*16, 32, 16, 16);
	public static BufferedImage ENEMY_FEEDBACK = Game.skin.getSprite(9*16, 16, 16, 16);
	public static BufferedImage BULLET_EN = Game.skin.getSprite(6*16, 16, 16, 16);
	public static BufferedImage WEAPON_EN = Game.skin.getSprite(7*16, 0, 16, 16);
	public static BufferedImage GUN_LEFT =Game.skin.getSprite(9*16,0 , 16, 16);
	public static BufferedImage GUN_RIGHT = Game.skin.getSprite(8*16, 0, 16, 16);
	public static BufferedImage GUN_LEFT_FEED = Game.skin.getSprite(16, 32, 16, 16);
	public static BufferedImage GUN_RIGHT_FEED = Game.skin.getSprite(0, 32, 16, 16);
	public static BufferedImage BOSS_EN= Game.skin.getSprite(0, 128, 32, 32);
	public static BufferedImage NPC = Game.skin.getSprite(32, 32, 16, 16);

	protected List<Node> path;
	
	public int depth;
	
	protected double z;
	public double x, y;
	protected int width, height;
	
	private BufferedImage skin;
	
	public Entity(int x, int y, int width, int height, BufferedImage skin,int mwidth,int mheight) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.skin = skin;
		
		this.maskx = 0;
		this.masky = 0;
		this.mheight = height;
		this.mwidth = width;
	}
	
	public static Comparator<Entity> nodeSorter = new Comparator<Entity>() {
		
		@Override
		
		public int compare(Entity n0, Entity n1) {
			if(n1.depth < n0.depth)
				return +1;
			if(n1.depth > n0.depth)
				return -1;
			return 0;
		}
		
	};
	
	public void setMask(int maskx,int masky,int mwidth, int mheight) {
		this.maskx = maskx;
		this.masky = masky;
		this.mheight = mheight;
		this.mwidth = mwidth;
	}
	
	public void setX(int newX) {
		this.x = newX;
	}
	public void setY(int newY) {
		this.y = newY;
	}
	
	
	public int getX() {
		return (int)this.x;
	}
	
	public int getY() {
		return (int)this.y;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void tick() {}
	
	public double calculeteDistance(int x1,int y1,int x2,int y2 ) {
		return Math.sqrt((x1-x2)*(x1-x2)+(y1 - y2)*(y1-y2));
	}
	
	public void followPath(List<Node> path) {
		if(path != null) {
			if(path.size() > 0) {
				Vector2i target = path.get(path.size() -1).tile;
				//xprev = x;
				//yprev = y;
				if(x < target.x * 16 && !this.isColidding(this.getX() + 1, this.getY())) {
					x++;
				}else if(x > target.x * 16 && !this.isColidding(this.getX() - 1, this.getY())) {
					x--;
				}
				if(y < target.y * 16 && !this.isColidding(this.getX(), this.getY() + 1)) {
					y++;
				}else if(y > target.y * 16 && !this.isColidding(this.getX(), this.getY() - 1)) {
					y--;
				}
				if(x == target.x * 16 && y == target.y * 16) {
					path.remove(path.size() -1);
				}
			}
		}
	}
	
	public static boolean isColidding(Entity e1,Entity e2) {
		Rectangle e1Mask = new Rectangle(e1.getX()+e1.maskx,e1.getY()+e1.masky,e1.mwidth,e1.mheight);
		Rectangle e2Mask = new Rectangle(e2.getX()+e2.maskx,e2.getY()+e2.masky,e2.mwidth,e2.mheight);
		if(e1Mask.intersects(e2Mask) && e1.z == e2.z) {
			return true;
		}
		return false;
	}
	
	public boolean isColiddingWithEnemy(int xnext,int ynext) {
		Rectangle enemyCur = new Rectangle(xnext,ynext,World.TILE_SIZE,World.TILE_SIZE);
		for(int i = 0;i < Game.enemies2.size();i++) {
			Enemy2 e = Game.enemies2.get(i);
				
			Rectangle targetEnemy = new Rectangle(e.getX(),e.getY(),World.TILE_SIZE,World.TILE_SIZE);	
			if(enemyCur.intersects(targetEnemy)) {
				
	
				return true;
			}
		}
		return false;
	}
	
	public boolean isColidding(int xnext,int ynext) {
		Rectangle enemyCur = new Rectangle(xnext,ynext,World.TILE_SIZE,World.TILE_SIZE);
		for(int i = 0;i < Game.enemies.size();i++) {
			Enemy e = Game.enemies.get(i);
			if(e == this) 
				continue;
				
			Rectangle targetEnemy = new Rectangle(e.getX(),e.getY(),World.TILE_SIZE,World.TILE_SIZE);	
			if(enemyCur.intersects(targetEnemy)) {
				return true;
			}
		}
		return false;
	}
	
	public void render(Graphics g) {
		g.drawImage(skin,this.getX() - Camera.x,this.getY() - Camera.y,null);
		//render as mascaras
		//g.setColor(Color.red);
		//g.fillRect(this.getX() + maskx - Camera.x,this.getY() + masky - Camera.y, mwidth, mheight);
	}
	
}
