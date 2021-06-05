package com.viduan.graficos;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Skin {

	private BufferedImage skin;
	
	public Skin(String path){
		try {
			skin = ImageIO.read(getClass().getResource(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public BufferedImage getSprite(int x, int y, int width, int height) {
		return skin.getSubimage(x, y, width, height);
	}
	
}
