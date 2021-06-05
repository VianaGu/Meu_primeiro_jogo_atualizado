package com.viduan.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.viduan.main.Game;
import com.viduan.main.Sound;
import com.viduan.world.Camera;
import com.viduan.world.World;

public class Player extends Entity {

	public boolean right, up , down, left;
	public int right_dir = 0, left_dir = 1;
	public int dir = right_dir;
	public double speed = 1.2;
	
	private int frames = 0,maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	private BufferedImage playerDamage; 
	
	public int ammo = 0;
	
	public boolean isDamaged = false;
	private int damageFrames = 0;
	
	public boolean mouseShoot = false;
	public boolean shoot = false;
	
	private boolean hasGun;
	public boolean equipGun = true;
	
	public double life = 100,maxLife = 100;
	public int mx, my;
	
	public boolean jump = false,jumping = false;
	
	public int z = 0;
	
	
	public int jumpFrames = 50, curJump = 0;
	public boolean jumpUp =false,jumpDown = false;
	
	
	public Player(int x, int y, int width, int height, BufferedImage skin,int mwidth,int mheight) {
		super(x, y, width, height, skin,mwidth,mheight);

		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		playerDamage = Game.skin.getSprite(0, 16,World.TILE_SIZE,World.TILE_SIZE);
		
		for(int i = 0; i < 4; i++) {
		rightPlayer[i] = Game.skin.getSprite(32+(i*16), 0, 16,16);
		}
		for(int i = 0; i < 4; i++) {
		leftPlayer[i] = Game.skin.getSprite(32+(i*16), 16, 16,16);
		}
	}
	public void reviewMap(){
		int xx = (int) (x/16);
		int yy = (int) (y/16);
		
		World.tiles[xx+1+(yy*World.WIDTH)].show = true;
		World.tiles[xx+(yy*World.WIDTH)].show = true;
		World.tiles[xx-1+(yy*World.WIDTH)].show = true;
		
		World.tiles[xx+((yy+1)*World.WIDTH)].show = true;
		World.tiles[xx+((yy-1)*World.WIDTH)].show = true;
		
		World.tiles[xx-1+((yy-1)*World.WIDTH)].show = true;
		World.tiles[xx+1+((yy-1)*World.WIDTH)].show = true;
		
		World.tiles[xx-1+((yy+1)*World.WIDTH)].show = true;
		World.tiles[xx+1+((yy+1)*World.WIDTH)].show = true;
	}
	
	
	public void tick() {
		depth = 1;
		reviewMap();
		
		
		if(jump == true) {
			if(jumping == false) {
				jump = false;
				jumping = true;
				jumpUp = true;
			}		
		}
		if(jumping == true) {
			
				if(jumpUp) {
					curJump++;
				}else if(jumpDown) {
					curJump--;
					if(curJump <= 0 ) {
						jumping =false;
						jumpDown = false;
						jumpUp = false;
					}
				}
				z = curJump;
				if(curJump >= jumpFrames) {
					jumpUp = false;
					jumpDown = true;
					
					
				}
			
		}
		moved = false;
		if(right && World.isFreeDynamic((int)(x+speed),this.getY(),14,14) ){
			moved = true;
			dir = right_dir;
			x+=speed;
			
		}
		else if(left && World.isFreeDynamic((int)(x-speed),this.getY(),14,14)){
			moved = true;
			dir = left_dir;
			x-=speed;
		}if(up && World.isFreeDynamic(this.getX(),(int)(y-speed),14,14)){
			moved = true;
			y-=speed;
		}
		else if(down && World.isFreeDynamic(this.getX(),(int)(y+speed),14,14)){
			moved = true;
			y+=speed;
		}
		if(moved) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex) {
					index = 0;
				}
			}
		}
		
		this.checkCollisionAmmo();
		this.checkCollisonLifePack();
		this.checkCollisonGun();
		this.updateCamera();
		
		if(isDamaged) {
			damageFrames++;
			if(damageFrames == 8) {
				damageFrames = 0;
				isDamaged =false;
				
			}
		}
		
		if(shoot) {
			shoot = false;
			if(this.equipGun == false) {
				this.equipGun = true;
			}
			else if(hasGun && ammo > 0 ) {
			Sound.soundShoot.play();			
					
			ammo--;
			int dx = 0;
			int px = 0;   
			int py = 6;
			if(dir == right_dir) {
				 px = 18;
				 dx = 1;
			}else {
				 px = -8;
				 dx = -1;
			}

			BulletShoot bullet = new BulletShoot(this.getX()+px,this.getY()+py,3,3,null,dx,0,mwidth,mheight);
			Game.bullet.add(bullet);
				}
		}
		
		if(mouseShoot) {
			mouseShoot = false;
			if(this.equipGun == false) {
				this.equipGun = true;
			}
			else if(hasGun && ammo > 0) {
			Sound.soundShoot.play();			

			ammo--;
			double angle = 0;
			int px= 0,py = 8;
			if(dir == right_dir) {
				 px = 18;
				 angle = Math.atan2(my - (this.getY()+py - Camera.y)  ,mx - (this.getX()+8 - Camera.x));
			}else {
				 px = -8;
				 angle = Math.atan2(my - (this.getY()+py - Camera.y)  ,mx - (this.getX()+8 - Camera.x));
			}
			double dx = Math.cos(angle);
			double dy = Math.sin(angle);

			BulletShoot bullet = new BulletShoot(this.getX()+px,this.getY()+py,3,3,null,dx,dy,mwidth,mheight);
			Game.bullet.add(bullet);
			}
	}
		if(life <= 0) {
			//Game Over
			life = 0;
			Game.gameState = "game_over";
		}
		
		
	}
	public void updateCamera() {
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2),0,World.WIDTH*16 -Game.WIDTH );
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2),0,World.HEIGHT*16 - Game.HEIGHT);
	}
	
	public void checkCollisonGun() {
		for(int i = 0; i < Game.weapon.size();i++) {
			Entity atual = Game.weapon.get(i);
			if(atual instanceof Weapon) {
				
				if(Entity.isColidding(this, atual)) {
					hasGun = true;
					Game.entities.remove(atual);
					Game.weapon.remove(atual);
					
				}
			}
		}
	}
	
	public void checkCollisonLifePack() {
		for(int i = 0; i < Game.lifepack.size();i++) {
			Entity atual = Game.lifepack.get(i);
			if(atual instanceof LifePack) {
				
				if(Entity.isColidding(this, atual)) {
					if(life == 100) {
						return;
					}
					if(life < 100) { 
						
						life += 10;
						if(life > 100)
							life = 100;
					Game.entities.remove(atual);
					Game.lifepack.remove(atual);
					}
				}
			}
		}
	}
	
	public void checkCollisionAmmo() {
		for(int i = 0; i < Game.ammo.size();i++) {
			Entity atual = Game.ammo.get(i);
			if(atual instanceof Bullet) {
				
				if(Entity.isColidding(this, atual)) {
					ammo+=10;
					Game.entities.remove(atual);
					Game.ammo.remove(atual);
					
				}
			}
		}
	}
	
	public void render(Graphics g) {
		if(!isDamaged) {
			if(dir == right_dir) {
				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY()- Camera.y - z,null);
				if(this.hasGun && equipGun) {
					g.drawImage(Entity.GUN_RIGHT,this.getX()+4-Camera.x,this.getY()+2-Camera.y - z,null);
				}
		
			}	
			else if(dir == left_dir) {
				g.drawImage(leftPlayer[index], this.getX()- Camera.x, this.getY() - Camera.y - z,null);
				if(this.hasGun && equipGun) {
					g.drawImage(Entity.GUN_LEFT,this.getX()-4-Camera.x,this.getY()+2-Camera.y - z,null);
				}
			}
			
		}else {
			if(dir == right_dir) {
				g.drawImage(playerDamage, this.getX() - Camera.x, this.getY()- Camera.y - z,null);
				if(this.hasGun && equipGun) {
					g.drawImage(Entity.GUN_RIGHT_FEED,this.getX()+4-Camera.x,this.getY()+2-Camera.y - z,null);
				}
		
			}	
			else if(dir == left_dir) {
				g.drawImage(playerDamage, this.getX()- Camera.x, this.getY() - Camera.y - z,null);
				if(this.hasGun && equipGun) {
					g.drawImage(Entity.GUN_LEFT_FEED,this.getX()-4-Camera.x,this.getY()+2-Camera.y - z,null);
				}
			}
		}
	}

}
