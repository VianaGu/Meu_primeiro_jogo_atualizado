package com.viduan.entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.viduan.main.Game;

public class Npc extends Entity{

	public boolean showMessage = false;
	public boolean show = false;
	
	public int curIndexMsg = 0;
	public int frasesIndex = 0;
	public int time = 0, maxTime = 10;
	
	public String[] frases = new String[3];
	
	public Npc(int x, int y, int width, int height, BufferedImage skin, int mwidth, int mheight) {
		super(x, y, width, height, skin, mwidth, mheight);
		// TODO Auto-generated constructor stub
	frases[0] = "Olá, seja muito bem-vindo ao jogo!";
	frases[1] = "Me chamo James";
	frases[2] = "Tudo bem?";
	}
	
	
	public void tick() {
		depth = 2;
		if(calculeteDistance(Game.player.getX(), Game.player.getY() ,this.getX() , this.getY()) < 20) { 
			if(show == false) {
			showMessage = true;
			show = true;
			}
		}else {
			//showMessage = false;
		}
		if(showMessage) {
			
			this.time++;
			if(this.time >= maxTime) {
			this.time = 0;	
			if(curIndexMsg < frases[frasesIndex].length()-1) {
				curIndexMsg++;
			}else {
				if(frasesIndex < frases.length-1) {
				frasesIndex++;
				curIndexMsg = 0;
				}
			}
			}
		}
		
	}
	
	public void render(Graphics g) {
		super.render(g);
		if(showMessage) {
			g.setColor(Color.black);
			g.fillRect(9, 9, Game.WIDTH-18, Game.HEIGHT-18);
			g.setColor(Color.white);
			g.fillRect(10, 10, Game.WIDTH-20, Game.HEIGHT-20);
			g.setFont(new Font("Arial",Font.BOLD,8));
			g.setColor(Color.black);
			g.drawString(frases[frasesIndex].substring(0,curIndexMsg),(int)x,(int)y);
			g.drawString(">Pressione enter para fechar<",(int)x,(int)y+10);
			
		}
	}

}
