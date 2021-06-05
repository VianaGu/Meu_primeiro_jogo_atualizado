package com.viduan.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.viduan.entities.Bullet;
import com.viduan.entities.BulletShoot;
import com.viduan.entities.Enemy;
import com.viduan.entities.Enemy2;
import com.viduan.entities.Entity;
import com.viduan.entities.LifePack;
import com.viduan.entities.Npc;
import com.viduan.entities.Player;
import com.viduan.entities.Weapon;
import com.viduan.graficos.Skin;
import com.viduan.graficos.UI;
import com.viduan.world.World;

public class Game extends Canvas implements Runnable,KeyListener, MouseListener,MouseMotionListener {

	
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private boolean isRunning = true;
	
	
	
	private Thread thread;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	public static final int SCALE = 3;
	private int CUR_LEVEL = 1, MAX_LEVEL = 3;
	public boolean saveGame = false,showGameSave = false;
	
	public boolean F11 ;
	 
	private BufferedImage image;
	
	private static boolean M = false;
	
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<Enemy2> enemies2;
	public static List<LifePack> lifepack;
	public static List<Weapon> weapon;
	public static List<Bullet> ammo;
	public static List<BulletShoot>bullet;
	
	public static Skin skin;
	
	public static World world;
	
	public static Player player;
	
	public Npc npc;
	
	public static Random rand;
	
	public UI ui;
	
	public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelfont.ttf");
	public Font newFont;
	
	public static String gameState = "menu";
	private boolean showMessageGameOver = true;
	private int framesGameOver = 0;
	private boolean restartGame = false;
	//sistema de cutscene!		
	public static int entrada = 1;
	public static int comecar = 2;
	public static int jogando = 3;
	public static int estado_cena = entrada;
	public int timeCena = 0,maxTimeCena = 30*2;
	
	
	public int mx,my;
	public Menu menu;
	//sistema de minimapa	
	public int[] pixels;
	public BufferedImage lightMap;
	public int[] lightMapPixels;
	public static int[] minimapaPixels;
	public static BufferedImage minimapa;
		
		public Game() {
			
			//Sound.music.loop();
			rand = new Random();
			addMouseListener(this);
			addKeyListener(this);
			addMouseMotionListener(this);
			//setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize()));
			setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
			initFrame();
			
			ui = new UI();
			image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
			try {
				lightMap = ImageIO.read(getClass().getResource("/lightmap.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			lightMapPixels = new int[lightMap.getWidth()*lightMap.getHeight()];
			lightMap.getRGB(0, 0, lightMap.getWidth(),lightMap.getHeight(),lightMapPixels,0, lightMap.getWidth());
			pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
			entities = new ArrayList<Entity>();
			enemies = new ArrayList<Enemy>();
			enemies2 = new ArrayList<Enemy2>();
			lifepack = new ArrayList<LifePack>();
			ammo = new ArrayList<Bullet>();
			weapon = new ArrayList<Weapon>();
			bullet = new ArrayList<BulletShoot>();
			
			
			skin = new Skin("/skin.png");
			player = new Player(0,0,16,16,skin.getSprite(32, 0, 16, 16),15,15);
			entities.add(player);
			world = new World("/level1.png");
			
			minimapa = new BufferedImage(World.WIDTH,World.HEIGHT,BufferedImage.TYPE_INT_RGB);
			minimapaPixels = ((DataBufferInt)minimapa.getRaster().getDataBuffer()).getData();
			
			npc = new Npc(32,32,16,16,skin.getSprite(32, 32, 16, 16),16,16);
			
			entities.add(npc);
			
			menu = new Menu();
		
		    
			try {
				newFont = Font.createFont(Font.TRUETYPE_FONT,stream).deriveFont(70f);
			} catch (FontFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}
	
	
		
	public void initFrame() {
	
		frame = new JFrame("Zelda Clone");
		frame.add(this);
		//frame.setUndecorated(true);
		frame.setResizable(false);
		frame.pack();
		Image image =null;
		try {
			image = ImageIO.read(getClass().getResource("./res/icon.png"));
		}catch(IOException e){
			e.printStackTrace();
		}
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image2 = toolkit.getImage(getClass().getResource("/icon.png"));
		Cursor c = toolkit.createCustomCursor(image2,new Point(0,0),"img");
		frame.setCursor(c);
		frame.setIconImage(image);
		frame.setAlwaysOnTop(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
		
	}
	
	public synchronized void stop() {
		isRunning= false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}
	
	public void tick() {
		if(gameState == "normal") {
			
			if(saveGame == true) {
				saveGame = false;
				String[] opt1 = {"level","life"};
				int[] opt2 = {this.CUR_LEVEL,  (int)player.life,};
				Menu.saveGame(opt1, opt2, 20);
				System.out.println("Jogo salvo");
			}
			this.restartGame = false;
			
			if(Game.estado_cena == Game.jogando) {
			for(int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}
		
			for(int i = 0; i < bullet.size(); i++) {
				bullet.get(i).tick();
			}
			
			if(enemies.size()==0 && enemies2.size() == 0) {
				//Proximo level
				CUR_LEVEL++;
				if(CUR_LEVEL > MAX_LEVEL) {
					CUR_LEVEL = 1;
				}
				String newWorld = "level"+CUR_LEVEL+".png";
				World.restartGame(newWorld);
			}	
			}else {
				if(Game.estado_cena == Game.entrada) {
					if(player.getX() < 100) {
						player.x++;
						player.reviewMap();
					}else {
						Game.estado_cena = Game.comecar;
					}
				}else if(Game.estado_cena == Game.comecar) {
					timeCena++;
					if(timeCena == maxTimeCena) {
						Game.estado_cena = Game.jogando;
						
					}
				}
			}
			
		}else if(gameState == "game_over") {
			this.framesGameOver++;
			if(this.framesGameOver == 30) {
				this.framesGameOver = 0;
				if(this.showMessageGameOver)
					this.showMessageGameOver = false;
					else 
						this.showMessageGameOver= true;	
			}
		}
		else if(this.saveGame) {
			this.showGameSave = true;
		}
		
		if(restartGame) {
			this.restartGame = false;
			gameState = "normal";
			CUR_LEVEL = 1;
			String newWorld = "level"+CUR_LEVEL+".png";
			World.restartGame(newWorld);
		}
		
		else if(gameState == "menu") {
			//MEnu
			menu.tick();
			player.updateCamera();
		}
	
	}
	
	/*public void drawRectangleExample(int xoff,int yoff) {
		for(int xx = 0; xx < 32; xx++) {
			for(int yy = 0; yy < 32; yy++) {
				int xOff = xx + xoff;
				int yOff = yy + yoff;
				if(xOff < 0||yOff <0|| xOff >= WIDTH||yOff >= HEIGHT)
					continue;
				pixels[xOff+(yOff*WIDTH)] = 0xFF0000;
			}
		}
	}*/
	
	public void applyLight() {
		for(int xx = 0; xx < WIDTH; xx++) {
			for(int yy = 0; yy < HEIGHT; yy++) {
				if(lightMapPixels[xx+(yy*WIDTH)] == 0xffffffff) {
					int pixel = Pixels.getLightBlend(pixels[xx+(yy*WIDTH)],0x878787, 0);
					pixels[xx+(yy*WIDTH)] = pixel;
				}
			}
		}
	}
	
	public void render () {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
		//fundo preparador.
		
		world.render(g);
		Collections.sort(entities, Entity.nodeSorter);
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
			}
		
		for(int i = 0; i < bullet.size(); i++) {
			bullet.get(i).render(g);
		}
		applyLight();
		ui.render(g);
		
		g.dispose();
		
		g = bs.getDrawGraphics();
		
		g.drawImage(image , 0, 0,WIDTH*SCALE,HEIGHT*SCALE, null);
		g.setFont(new Font("arial", Font.BOLD,25));
		g.setColor(Color.white);
		g.drawString("Munição: " + player.ammo,WIDTH*SCALE-160,HEIGHT*SCALE-440);
	
		if(this.saveGame  ) {
		g.setColor(Color.red);
		g.drawString("JOGO SALVO", 620,32);
		}
		if(gameState == "game_over") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0,0,0,100));
			g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
			g.setFont(new Font("arial", Font.BOLD,36));
			g.setColor(Color.white);
			g.drawString("GAME OVER",(WIDTH*SCALE)/2-100,(HEIGHT*SCALE)/2-30);
			g.setFont(new Font("arial", Font.BOLD,36));
			if(showMessageGameOver) {
				g.drawString("Pressione enter para recomeçar",(WIDTH*SCALE)/2-250,(HEIGHT*SCALE)/2+30);
			}
		}else if(gameState == "menu") {
			menu.render(g);
		}
		
		
		
		/*Graphics2D g2 = (Graphics2D) g;
		double angleMouse = Math.atan2(my-200+25,mx - 200+25);
		g2.rotate(angleMouse,200+25,200+25);
		g.setColor(Color.red);
		g.fillRect(200, 200, 50,50);*/
		World.renderMinimap();
		if(M == true) {
			g.drawImage(minimapa,WIDTH*SCALE/2-225,HEIGHT*SCALE/2-200,WIDTH*2,HEIGHT*2,null);
			g.setFont(new Font("newFont", Font.BOLD,20));
			g.setColor(Color.GREEN);
			g.drawString("VERDE: Player",WIDTH*SCALE/2-225,HEIGHT*SCALE/2+150);
			g.setFont(new Font("newFont", Font.BOLD,20));
			g.setColor(Color.red);
			g.drawString("VERMELHO: Inimigo", WIDTH*SCALE/2-225,HEIGHT*SCALE/2+170);
		}
		if(this.CUR_LEVEL == 3 && M == false) {
			g.drawImage(minimapa,WIDTH*SCALE-210,HEIGHT*SCALE-410,World.WIDTH*2,World.HEIGHT*2,null);
		}
		if(this.CUR_LEVEL == 2 && M == false) {
			g.drawImage(minimapa,WIDTH*SCALE-160,HEIGHT*SCALE-410,World.WIDTH*3,World.HEIGHT*3,null);
		}else if(this.CUR_LEVEL == 1 && M == false) {
			g.drawImage(minimapa,WIDTH*SCALE-160,HEIGHT*SCALE-410,World.WIDTH*5,World.HEIGHT*5,null);
		}
		if(Game.estado_cena == Game.comecar) {
			g.setFont(new Font("newFont", Font.BOLD,20));
			g.setColor(Color.black);
			g.drawString("Se Prepare!!", frame.getWidth()/2-60,frame.getHeight()/2);
		}
		
		bs.show();
	}
	@Override
	public void run() {
		
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		while(isRunning) {
			
			long now = System.nanoTime();
			delta+= (now - lastTime)/ns ;
			lastTime = now;
			if (delta >=1 ) {
				tick();
				render();
				frames++;
				delta--;
			}
			if (System.currentTimeMillis() - timer >=1000) {
				System.out.println("FPS: "+ frames );
				frames = 0;
				timer+=1000;
			}
			
		}
		
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_E) {
			if(player.equipGun == false) {
				player.equipGun = true;
			}else if(player.equipGun == true) {
				player.equipGun = false;
			}
		}
	
		if(e.getKeyCode() == KeyEvent.VK_M) {
			M = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_V) {
			player.jump = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT||
				e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_LEFT||
				e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_UP||
				e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;
			if(gameState == "menu") {
				menu.up = true;
			
			}
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN||
				e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
			if(gameState == "menu") {
				menu.down = true;
				
			}
			
		}
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			player.shoot = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(gameState == "normal") {
			npc.showMessage = false;
			}
			if(gameState == "game_over") {
				restartGame = true;
			}
			if(gameState== "menu") {
				menu.enter = true;
			}
			
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gameState = "menu";
			Menu.pause = true;
		}if(e.getKeyCode() == KeyEvent.VK_F) {
			if(gameState == "normal") {
			this.saveGame = true;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_M) {
			M = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_V) {
			player.jump = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT||
				e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_LEFT||
				e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_UP||
				e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN||
				e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		player.mouseShoot = true;
		int scale = frame.getWidth()/WIDTH;
		
		player.mx = (e.getX() /scale);
		player.my = (e.getY() /scale);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		this.mx = e.getX();
		this.my = e.getY();
	}

}
