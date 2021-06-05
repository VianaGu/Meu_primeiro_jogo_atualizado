package com.viduan.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.viduan.main.Game;
import com.viduan.main.Sound;
import com.viduan.world.AStar;
import com.viduan.world.Camera;
import com.viduan.world.Vector2i;
import com.viduan.world.World;

public class Enemy extends Entity {
	
	
	private BufferedImage[] sprite;
	private int frames = 0,maxFrames =15, index = 0, maxIndex = 1;
	
	private int life = 10;
	
	private boolean isDamaged = false;
	private int damagedFrames = 10,damageCur = 0;

	public Enemy(int x, int y, int width, int height, BufferedImage skin, int mwidth, int mheight) {
		super(x, y, width, height, null,mwidth,mheight);
		sprite = new BufferedImage[2];
		sprite[0] = Game.skin.getSprite(7*16, 16, 16, 16);
		sprite[1] = Game.skin.getSprite(8*16, 16, 16, 16);
		
	}

	public void tick() {
		depth = 0;
	if(this.calculeteDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY()) < 100 ) {
	 if(!this.icwp())  {
		 if(path == null || path.size() == 0) {
				Vector2i start = new Vector2i(((int)(x/16)),((int)(y/16)));
				Vector2i end = new Vector2i(((int)(Game.player.x/16)),((int)(Game.player.y/16)));
				path = AStar.findPath(Game.world, start, end);
			}
		
			if(new Random().nextInt(100) < 80)
				followPath(path);
			
			if(x % 16 == 0 && y % 16 == 0) {
				if(new Random().nextInt(100) < 10) {
					Vector2i start = new Vector2i(((int)(x/16)),((int)(y/16)));
					Vector2i end = new Vector2i(((int)(Game.player.x/16)),((int)(Game.player.y/16)));
					path = AStar.findPath(Game.world, start, end);
				}
			}
		
		
	 }else {
		 if(Game.rand.nextInt(100) < 5) {
			Sound.hitEffect.play();
			Game.player.life-=Game.rand.nextInt(3);
			Game.player.isDamaged = true;
							 	
					
				 
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
	}
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}
	
	
	
	public void collidingBullet() {
		for(int i =0; i<Game.bullet.size();i++) {
			Entity e = Game.bullet.get(i);
			if(e instanceof BulletShoot) {
				if(Entity.isColidding(this,e)) {
				    isDamaged = true;
					life-= Game.rand.nextInt(7);
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
	
	
	
	
	public void render(Graphics g) {
		if(!this.isDamaged) {
			g.drawImage(sprite[index],this.getX() - Camera.x,this.getY()- Camera.y,null);
		}else {
			g.drawImage(Entity.ENEMY_FEEDBACK,this.getX() - Camera.x,this.getY()- Camera.y,null);
		}
	}
}
