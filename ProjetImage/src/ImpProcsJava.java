import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class ImpProcsJava {
	
	public static void go() {
		
		// TODO Auto-generated method stub
		File path = new File("/Users/maxiiiiiiiiime/Documents/Image/ImageEsc/esc20.jpg");

		// Image Normal
		BufferedImage img = null;
		try {
			img = ImageIO.read(path);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Creation Image");
		
		// Passage en gris
		img = Launcher.toGray(img);
		
		//SEUILLAGE{r,g,b,255}
		int tab[]=new int[img.getWidth()];
		int seuil = Launcher.otsuTreshold(img);
		int[] noir = {0,0,0,255};
		int[] blanc = {255,255,255,255};
		for(int x=0;x<img.getWidth();x++) {
			for (int y=0;y<img.getHeight();y++){
				int p = img.getRGB(x,y); // r�cup�ration des couleurs RGB du pixel a la position (x, y)
				int a = (p>>24)&0xff; 
		        int r = (p>>16)&0xff; 
		        int g = (p>>8)&0xff; 
		        int b = p&0xff;							
		        if(r<=seuil) {								
					img.getRaster().setPixel(x, y, noir);
					tab[x]++;								// Incrémentation de l'histogramme
				}
		        else img.getRaster().setPixel(x, y, blanc);
			}	
		}
		System.out.println("Largeur : " +img.getWidth());
		System.out.println("Hauteur : " +img.getHeight());
		
		
		// Histogramme

		// MAX
		int max= tab[0];
		for (int i=1;i<tab.length;i++) {
			if(max<tab[i]) max=tab[i];
		}
		
		// Nouvel Image
		BufferedImage img2 = new BufferedImage (tab.length,100,BufferedImage.TYPE_INT_RGB);
		
		// Pourcentage sur 100
		for (int i=0;i<tab.length;i++) {
			tab[i]=(tab[i]*100)/max;
		}
		
		// Implementation Histogramme
		for (int i=0;i<tab.length;i++) { // 0 a 255 (x) height 
			for(int j=100-tab[i];j<100;j++){
				img2.setRGB(i,j,Color.RED.getRGB());
			}
	    }
		
		
		// Affichage de l'image
		try {
			Launcher.imshow(img);
			Launcher.imshow(img2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("J'affiche !!");
	}
	
	
	public static void main(String[] args) {
		go();
	}
}
