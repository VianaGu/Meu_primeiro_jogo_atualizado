package com.viduan.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.viduan.world.World;

public class Menu {
	
	public String[] options = {"Novo Jogo","Carregar Jogo","Sair","Continuar"};
		
	public int curOptions =0;
	public int maxOptions = options.length -1;
	
	public boolean up,down, enter;
	
	public static boolean pause = false;
	
	public static boolean saveExists = false,saveGame = false;
	
	public void tick() {
		
		File file = new File("save.txt");
		if(file.exists()) {
			saveExists = true;
		}else {
			saveExists = false;	
			}
		
		if(up) {
			up = false;
			curOptions--;
			if(curOptions<0) {
				curOptions = maxOptions;
			}
		}
		else if(down) {
			down = false;
			curOptions++;
			if(curOptions>maxOptions) {
				curOptions = 0;
			}
		}
		if(enter == true) {
		
			enter = false;
			if(options[curOptions] == "Novo Jogo" || options[curOptions] == "Continuar") {
				Game.gameState = "normal";
				pause = false;
				file = new File("save.txt");
				file.delete();
			
				}else if(options[curOptions] == "Carregar Jogo") {
					file = new File("save.txt");
					if(file.exists()) {
						String saver = loadGame(20);
						applySave(saver);
				}
			}
			else if(options[curOptions] == "Sair") {
				System.exit(1);
			}
		}
	}
	
	
	public static void applySave(String str) {
		String[] spl = str.split("/");
		for(int i = 0; i < spl.length; i++) {
			String[] spl2 = spl[i].split(":");
			switch(spl2[0]) 
			{
				case "level":
					World.restartGame("level"+spl2[1]+".png");
					Game.gameState = "normal";
					pause = false;
					break;
				case "life":
					Game.player.life = Integer.parseInt(spl2[1]);
					break;
			}
		}
	}
	
	public static String loadGame(int encode) {
		String line ="";
		File file = new File("save.txt");
		if(file.exists()) {
			try {
				String singleLine = null;
				BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
				try {
					while((singleLine = reader.readLine()) !=null) {
						String[] trans = singleLine.split(":");
						char[] val = trans[1].toCharArray();
						trans[1] = "";
						for(int i = 0; i<val.length; i++) {
							val[i] -= encode;
							trans[1]+=val[i];
						}
						line += trans[0];
						line += ":";
						line += trans[1];
						line += "/";
					}
				}catch(IOException e) {}
			}catch(FileNotFoundException e) {
				
			}
			
		}
		return line;
	}
	
	public static void saveGame(String[] val1, int[] val2,int encode) {
		BufferedWriter write = null;
		try{
			write = new BufferedWriter(new FileWriter("save.txt"));
		}catch(IOException e){
			e.printStackTrace();
		}
		
		for(int i = 0; i < val1.length; i++) {
			String current = val1[i];
			current += ":";
			char[] value = Integer.toString(val2[i]).toCharArray();
			
			for(int n = 0; n < value.length; n++) {
				value[n]+=encode;
				current+=value[n];
			}
			try {
				write.write(current);
				if(i<val1.length-1)
					write.newLine();
			}catch(IOException e) {
				
			}
			
		}
		try {
			write.flush();
			write.close();
		}catch(IOException e) {
			
		}
	}
	
	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(0,0,0,100));
		g2.fillRect(0, 0,Game.WIDTH*Game.SCALE,Game.HEIGHT*Game.SCALE);
		g.setColor(Color.white);
		g.setFont(new Font("arial",Font.BOLD,36));
		g.drawString("Viduan", Game.WIDTH*Game.SCALE/2-60,Game.HEIGHT*Game.SCALE-400);
		
		//op�oes
		g.setFont(new Font("arial",Font.BOLD,24));
		if(pause == false) 
			g.drawString("Novo Jogo", Game.WIDTH*Game.SCALE/2-60,Game.HEIGHT*Game.SCALE-300);
		else
			g.drawString("Continuar", Game.WIDTH*Game.SCALE/2-60,Game.HEIGHT*Game.SCALE-300);
			g.drawString("Carregar Jogo", Game.WIDTH*Game.SCALE/2-60,Game.HEIGHT*Game.SCALE-260);
			g.drawString("Sair", Game.WIDTH*Game.SCALE/2-60,Game.HEIGHT*Game.SCALE-220);
		
		if(options[curOptions] == "Novo Jogo" || options[curOptions] == "Continuar") {
			g.drawString("> ", Game.WIDTH*Game.SCALE/2-80,Game.HEIGHT*Game.SCALE-300);
		}
		else if(options[curOptions] == "Carregar Jogo") {
			g.drawString("> ", Game.WIDTH*Game.SCALE/2-80,Game.HEIGHT*Game.SCALE-260);
		}else if(options[curOptions] == "Sair") {
			g.drawString("> ", Game.WIDTH*Game.SCALE/2-80,Game.HEIGHT*Game.SCALE-220);
		}
	}
}
