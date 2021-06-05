package com.viduan.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.viduan.entities.Bullet;
import com.viduan.entities.Enemy;
import com.viduan.entities.Enemy2;
import com.viduan.entities.Entity;
import com.viduan.entities.LifePack;
import com.viduan.entities.Particle;
import com.viduan.entities.Player;
import com.viduan.entities.Weapon;
import com.viduan.graficos.Skin;
import com.viduan.main.Game;

public class World {

	
	
	public static Tile[]tiles;
	public static int WIDTH,HEIGHT;
	public static final int TILE_SIZE = 16;
	
	public World(String path) {
		try {
			
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			int[] pixels = new int[map.getWidth() * map.getHeight()];
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			tiles = new Tile[map.getWidth() * map.getHeight()];
			map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());
		
				for(int xx = 0; xx < map.getWidth();xx++) {
					for(int yy = 0; yy < map.getHeight();yy++) {
						int pixelAtual = pixels[xx + (yy*map.getWidth())];
						tiles[xx +(yy*WIDTH)] = new FloorTile(xx*16,yy*16, Tile.TILE_FLOOR);

						if(pixelAtual == 0xFF000000) {
							//floor
							tiles[xx +(yy*WIDTH)] = new FloorTile(xx*16,yy*16, Tile.TILE_FLOOR);
						}else if(pixelAtual == 0xFFFFFFFF) {
							//parede
							tiles[xx +(yy*WIDTH)] = new WallTile(xx*16,yy*16, Tile.TILE_WALL);
						}else if(pixelAtual == 0xFF404040) {
							tiles[xx +(yy*WIDTH)] = new FloorTile(xx*16,yy*16, Tile.TILE_FLOOR2);
							//grama
						}else if(pixelAtual == 0xFF00137F) {
							//Player
							Game.player.setX(xx*16);
							Game.player.setY(yy*16);
						}else if(pixelAtual == 0xFFFF0000) {
							//Enemy
							Enemy en = new Enemy (xx*16,yy*16,16,16,Entity.ENEMY_EN,8,8);
							Game.entities.add(en);
							Game.enemies.add(en);
							
						}else if(pixelAtual == 0xFF52347C) {
							Enemy2 en2 = new Enemy2(xx*16,yy*16,16,16,Entity.ENEMY2_EN,8,8);
							Game.entities.add(en2);
							Game.enemies2.add(en2);
							
						}else if(pixelAtual == 0xFF7F0000) {
							//weapon
							Weapon w = new Weapon(xx*16,yy*16,16,16,Entity.WEAPON_EN,16,16);
							Game.entities.add(w);
							Game.weapon.add(w);

						}else if(pixelAtual == 0xFF00FF21) {
							//life pack
							LifePack pack = new LifePack(xx*16,yy*16,16,16,Entity.LIFEPACK_EN,16,16);
							Game.entities.add(pack);
							Game.lifepack.add(pack);

						}else if(pixelAtual == 0xFF00FF90) {
							//bullet
							Bullet ammo = new Bullet(xx*16,yy*16,16,16,Entity.BULLET_EN,16,16);
							Game.entities.add(ammo);
							Game.ammo.add(ammo);
						
						  }
							
					
						
						
					
				
					}
					}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public static boolean isFreeDynamic(int xnext,int ynext, int width,int height) {
		int x1 = xnext /TILE_SIZE;
		int y1 = ynext /TILE_SIZE;
		
		int x2 = (xnext+width-1) /TILE_SIZE;
		int y2 = ynext /TILE_SIZE;
		
		int x3 = xnext /TILE_SIZE;
		int y3 = (ynext+height-1) /TILE_SIZE;
		
		int x4 = (xnext+width-1) /TILE_SIZE;
		int y4 = (ynext+height-1) /TILE_SIZE;
		
		return !((tiles[x1+(y1*World.WIDTH)] instanceof WallTile)||
				(tiles[x2+(y2*World.WIDTH)] instanceof WallTile) ||
				(tiles[x3+(y3*World.WIDTH)] instanceof WallTile) ||
				(tiles[x4+(y4*World.WIDTH)] instanceof WallTile));
	}
	
	public static boolean isFree(int xnext,int ynext) {
		int x1 = xnext /TILE_SIZE;
		int y1 = ynext /TILE_SIZE;
		
		int x2 = (xnext+TILE_SIZE-1) /TILE_SIZE;
		int y2 = ynext /TILE_SIZE;
		
		int x3 = xnext /TILE_SIZE;
		int y3 = (ynext+TILE_SIZE-1) /TILE_SIZE;
		
		int x4 = (xnext+TILE_SIZE-1) /TILE_SIZE;
		int y4 = (ynext+TILE_SIZE-1) /TILE_SIZE;
		
		return !((tiles[x1+(y1*World.WIDTH)] instanceof WallTile)||
				(tiles[x2+(y2*World.WIDTH)] instanceof WallTile) ||
				(tiles[x3+(y3*World.WIDTH)] instanceof WallTile) ||
				(tiles[x4+(y4*World.WIDTH)] instanceof WallTile));
	}
	
	public static void restartGame(String level) {
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.enemies2 = new ArrayList<Enemy2>();
		Game.lifepack = new ArrayList<LifePack>();
		Game.ammo = new ArrayList<Bullet>();
		Game.skin = new Skin("/skin.png");
		Game.player = new Player(0,0,16,16,Game.skin.getSprite(32, 0, 16, 16),15,15);
		Game.entities.add(Game.player);
		Game.world = new World("/"+level);
		Game.minimapa = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		Game.minimapaPixels = ((DataBufferInt)Game.minimapa.getRaster().getDataBuffer()).getData();
		return;
	}
	
	public static void generateParticles(int amount,int x,int y) {
		for(int i = 0;i < amount;i++) {
			Game.entities.add(new Particle(x,y,1,1,null,1,1));
			
		}
	}
	
	
	public void render(Graphics g) {
		int xstart = Camera.x / 16;
		int ystart = Camera.y / 16;
		
		int xfinal = xstart + (Game.WIDTH / 16);
		int yfinal = ystart + (Game.HEIGHT / 16 );
		for(int xx = xstart; xx <= xfinal; xx++) {
			for(int yy = ystart; yy <= yfinal; yy++) {
				if(xx < 0 || yy < 0 ||xx >= WIDTH ||yy>=HEIGHT)
					continue;
				Tile tile = tiles[xx + (yy*WIDTH)];
				tile.render(g);
			}
		}
	}

	public static void renderMinimap() {
		for(int i =0; i < Game.minimapaPixels.length;i++) {
			Game.minimapaPixels[i] = 0;
		}
		for(int xx = 0; xx < WIDTH;xx++) {
			for(int yy = 0; yy < HEIGHT;yy++) {
				if(tiles[xx + (yy*WIDTH)] instanceof WallTile) {
					Game.minimapaPixels[xx+ (yy*WIDTH)] = 0xFFFFFFFF;
		        }
				for(int enemyAtual =0;enemyAtual < Game.enemies.size();enemyAtual++) {
					Enemy atual = Game.enemies.get(enemyAtual);
					    
					int xEnemy = atual.getX()/16;
					int yEnemy = atual.getY()/16;
					Game.minimapaPixels[xEnemy + (yEnemy*WIDTH)] = 0xFFFF0000;
	
				}
				for(int enemyAtual =0;enemyAtual < Game.enemies2.size();enemyAtual++) {
					Enemy2 atual = Game.enemies2.get(enemyAtual);
					    
					int xEnemy = atual.getX()/16;
					int yEnemy = atual.getY()/16;
					Game.minimapaPixels[xEnemy + (yEnemy*WIDTH)] = 0xFFFF0000;
				}
				for(int lifePackAtual =0;lifePackAtual < Game.lifepack.size();lifePackAtual++) {
					LifePack atual = Game.lifepack.get(lifePackAtual);
					    
					int xEnemy = atual.getX()/16;
					int yEnemy = atual.getY()/16;
					Game.minimapaPixels[xEnemy + (yEnemy*WIDTH)] = 0xFF222222;
				}
				
			}
			
			
		int xPlayer = Game.player.getX()/16;
		int yPlayer = Game.player.getY()/16;
		Game.minimapaPixels[xPlayer + ((yPlayer )*WIDTH)] = 0x00ff00;
		
		 
		}
	}
}
