package com.viduan.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.viduan.main.Game;
import com.viduan.main.Sound;
import com.viduan.world.Camera;
import com.viduan.world.World;

public class Enemy2 extends Entity {
	
	
	private BufferedImage[] sprite;
	private int frames = 0,maxFrames =15, index = 0, maxIndex = 1;
	private double speed = 0.5;
	
	private int life = 10;
	
	private boolean isDamaged = false;
	private int damagedFrames = 10,damageCur = 0;

	public Enemy2(int x, int y, int width, int height, BufferedImage skin, int mwidth,int mheight) {
		super(x, y, width, height, null,mwidth, mheight);
		sprite = new BufferedImage[2];
		sprite[0] = Game.skin.getSprite(112, 32, World.TILE_SIZE, World.TILE_SIZE);
		sprite[1] = Game.skin.getSprite(128, 32, World.TILE_SIZE, World.TILE_SIZE);
		
	}

	public void tick() {
		if(this.calculeteDistance(this.getX(), this.getY(),Game.player.getX(), Game.player.getY()) <100) {
		if(this.icwp()==false) {
		if((int)x<Game.player.getX() && World.isFree((int)(x+speed),this.getY())
				&& !isColidding((int)(x+speed),this.getY())&& !this.isColiddingWithEnemy((int)(x+speed),this.getY())) {
			x+=speed;
		}else if((int)x>Game.player.getX()&& World.isFree((int)(x-speed),this.getY())
				&& !isColidding((int)(x-speed),this.getY())&& !this.isColiddingWithEnemy((int)(x-speed),this.getY())) {
			x-=speed;
		}
		if((int)y<Game.player.getY()&& World.isFree(this.getX(),(int)(y+speed))
				&& !isColidding(this.getX(),(int)(y+speed))&& !this.isColiddingWithEnemy(this.getX(),(int)(y+speed))) {
			y+=speed;
		}else if((int)y>Game.player.getY()&& World.isFree(this.getX(),(int)(y-speed))
				&& !isColidding(this.getX(),(int)(y-speed))&& !this.isColiddingWithEnemy(this.getX(),(int)(y-speed))) {
			y-=speed;
		}
		}else {
			if(Game.rand.nextInt(100)<10) {
				Sound.hitEffect.play();
				Game.player.life-=Game.rand.nextInt(5);
				Game.player.isDamaged = true;
			 	
			}
		}	
		}
		
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex) {
					index = 0;
				}
			}
			
			this.collidingBullet();
		
			if(life <=0) {
				destroySelf();
				return;
			}
			
			if(isDamaged){
				this.damageCur++;
				if(this.damageCur == this.damagedFrames) {
					this.damageCur = 0;
					this.isDamaged = false;
				}
			}
			
	}
	
	public void destroySelf() {
		Game.enemies2.remove(this);
		Game.entities.remove(this);
	}
	
	public void collidingBullet() {
		for(int i =0; i<Game.bullet.size();i++) {
			Entity e = Game.bullet.get(i);
			if(e instanceof BulletShoot) {
				if(Entity.isColidding(this,e)) {
				    isDamaged = true;
					life-=Game.rand.nextInt(7);
					Game.bullet.remove(i);
					
					return;
				}
			}
		}
		
	}
	
	public boolean icwp() {
		Rectangle enemyCur = new Rectangle(this.getX(),this.getY(),World.TILE_SIZE,World.TILE_SIZE);
		Rectangle player = new Rectangle(Game.player.getX(),Game.player.getY(),World.TILE_SIZE,World.TILE_SIZE);
		
		return enemyCur.intersects(player);
		
	}
	
	public boolean isColiddingWithEnemy(int xnext,int ynext) {
		Rectangle enemyCur = new Rectangle(xnext,ynext,World.TILE_SIZE,World.TILE_SIZE);
		for(int i = 0;i < Game.enemies.size();i++) {
			Enemy e = Game.enemies.get(i);
				
			Rectangle targetEnemy = new Rectangle(e.getX(),e.getY(),World.TILE_SIZE,World.TILE_SIZE);	
			if(enemyCur.intersects(targetEnemy)) {
				
	
				return true;
				
			}
			
		}
		return false;
	}
	
	public boolean isColidding(int xnext,int ynext) {
		Rectangle enemyCur = new Rectangle(xnext,ynext,World.TILE_SIZE,World.TILE_SIZE);
		for(int i = 0;i < Game.enemies2.size();i++) {
			Enemy2 e = Game.enemies2.get(i);
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
		if(!this.isDamaged) {
			g.drawImage(sprite[index],this.getX() - Camera.x,this.getY()- Camera.y,null);
		}else {
			g.drawImage(Entity.ENEMY_FEEDBACK,this.getX() - Camera.x,this.getY()- Camera.y,null);
		}
	}
}
