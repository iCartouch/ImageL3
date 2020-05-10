
	import java.io.File;
	import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import javax.imageio.ImageIO;
	import javax.swing.ImageIcon;
	import javax.swing.JFrame;
	import javax.swing.JLabel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;



	public class Launcher {


		// AFFICHAGE
		public static void imshow(BufferedImage image) throws IOException {
		      //Instantiate JFrame
		      JFrame frame = new JFrame();

		      //Set Content to the JFrame
		      frame.getContentPane().add(new JLabel(new ImageIcon(image)));
		      frame.pack();
		      frame.setVisible(true);
		 }



		// CREATION IMAGE NOIRE
		static BufferedImage zeros(int width, int height) {
			int[] noir = {0,0,0,255};
	        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        for (int x = 0; x < width; x++) {
	            for (int y = 0; y < height ; y++) {
	            	img.getRaster().setPixel(x, y, noir);
	            }
	        }
	        return img;
	    }


		// EGALISATION    NE FONCTIONNE PAS
		public static BufferedImage normalize_minmax(BufferedImage image) {
			int rows = image.getHeight();
			int cols = image.getWidth();

			System.out.println("Size is, w=" + cols + "; h="+rows);
			// search the max and the min values in the image for each chanel

			float min = 255;
			float max = 0;

			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {

					//float pixel = ((image.getRGB(j, i) >> 16) & 0xff);
					//System.out.println("x="+i+";y="+j+"; pix="+pixel);

					float[] out = new float[3];
					image.getRaster().getPixel(j, i, out);

					if (out[0] < min) {
						min = out[0];
					}
					if (out[0] > max) {
						max = out[0];
					}
				}
			}

			System.out.println("min =" + min + "; max="+max);
			BufferedImage img = new BufferedImage(cols, rows, 10);

			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					int pixel = 0;//((image.getRGB(j, i) >> 16) & 0xff);

					float[] out = new float[3];
					image.getRaster().getPixel(j, i, out);

					pixel = (int) ( 255 * (out[0] - min) / (max - min));
					int[] new_color = {pixel, pixel, pixel};
					img.getRaster().setPixel(j, i, new_color);
				}
			}

			return img;

		}



		// Image en Gris
		public static BufferedImage toGray(BufferedImage img) {
			for (int y=0;y<img.getHeight();y++) {
				for(int x=0;x<img.getWidth();x++) {
					int p = img.getRGB(x,y); // r�cup�ration des couleurs RGB du pixel a la position (x, y)
					int a = (p>>24)&0xff;
			        int r = (p>>16)&0xff;
			        int g = (p>>8)&0xff;
			        int b = p&0xff;
			        int gr = (r+g+b)/3;
			        int[] gris= {gr,gr,gr,255};
					img.getRaster().setPixel(x, y, gris);
				}
			}
			return img;
		}



		// SEUILLAGE
		public static BufferedImage threshold(BufferedImage img, int s) {
			int rows = img.getHeight();
			int cols = img.getWidth();

			BufferedImage im_thresh = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
			int[] noir = {0,0,0,255};
			int[] blanc = {255,255,255,255};
			for (int i = 0; i < cols; i++) {
				for (int j = 0; j < rows; j++) {
					int pixel = img.getRGB(i, j);
					int red = (pixel >> 16) & 0xff;
					if (red < s) {
						im_thresh.getRaster().setPixel(i, j, noir);
					}else {
						im_thresh.getRaster().setPixel(i, j, blanc );
					}
				}
			}
			return im_thresh;
		}



		// CONVOLUTION 2.0
		public static BufferedImage convolveOP(BufferedImage img, int x, int y, float[] mask) {

				/* Définition du noyau */
				Kernel kernel = new Kernel(x, y, mask);

				/* Création de la convolution associée */
				ConvolveOp convolution = new ConvolveOp(kernel);

				/* Application de la convolution à l'image */
				return convolution.filter(img, null);
		}

		public static BufferedImage detectionRect(BufferedImage img) {
			System.out.println("YOOO");
			int[] noir = {0,0,0,255};
			int[] blanc = {255,255,255,255};
			
			int [][] rect=new int [20][5];
			for(int x=0;x<20;x++) {
				for (int y=0;y<5;y++){
					rect[x][y]=1;
				}
			}
			
			int tab[][]= new int[20][5];
			for(int l=0;l<img.getHeight();l++) {
				for (int c=0;c<img.getWidth();c++){
					int p = img.getRGB(c,l); // r�cup�ration des couleurs RGB du pixel a la position (x, y)
					
						if (p==1) {
							boolean bool = true;
							for(int i=0;i<20;i++) {
								for (int j=0;j<5;j++){
									if (img.getRGB(c+j,l+i)!=rect[i][j]) {
									bool = false;
									}	
								}
							}
							if (bool){
								img.getRaster().setPixel(c, l, blanc);
							}else {
								img.getRaster().setPixel(c, l, noir);
							}
						}
						
				}
			}
			
			return img;
		}


		// TAB DE IMG
		public static int[] imageHistogram(BufferedImage img) {
				int tab[]= new int[img.getWidth()];
				for(int l=0;l<img.getHeight();l++) {
					for (int c=0;c<img.getWidth();c++){
						int p = img.getRGB(c,l); // r�cup�ration des couleurs RGB du pixel a la position (x, y)
						int a = (p>>24)&0xff;
				        int r = (p>>16)&0xff;
				        int g = (p>>8)&0xff;
				        int b = p&0xff;
							tab[r]++;							// Incrémentation de l'histogramme
					}
				}
			return tab;
		}



		// Seuillage OTSU
		public static int otsuTreshold(BufferedImage imgo) {
			int[] tab = imageHistogram(imgo);
	        int total = imgo.getHeight() * imgo.getWidth();
	        float sum = 0;
	        for(int i=0; i<tab.length; i++) sum += i * tab[i];

	        float sumB = 0;
	        int wB = 0;
	        int wF = 0;

	        float varMax = 0;
	        int threshold = 0;

	        for(int i=0 ; i<tab.length ; i++) {
	            wB += tab[i];
	            wF = total - wB;
	            if(wF == 0) break;

	            sumB += (float) (i * tab[i]);
	            float mB = sumB / wB;
	            float mF = (sum - sumB) / wF;

	            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

	            if(varBetween > varMax) {
	                varMax = varBetween;
	                threshold = i;
	            }
	        }
	        return threshold;
	    }


		// TAB sur 100
		public static int[] pourcentage(int[] tab) {
			// MAX
			int max = tab[0];
			for (int i=1;i<tab.length;i++) {
				if(max<tab[i]) max=tab[i];
			}

			// Pourcentage sur 100
			for (int i=0;i<tab.length;i++) {
				tab[i]=(tab[i]*100)/max;
			}
			return tab;
		}


		// Ecriture Histo
		public static BufferedImage ecritHisto(int[] tab) {
			// Nouvel Image
			BufferedImage img2 = zeros(tab.length,100);

			// Implementation Histogramme
			for (int i=0;i<tab.length;i++) { // 0 a 255 (x) height
				for(int j=100-tab[i];j<100;j++){
					img2.setRGB(i,j,Color.RED.getRGB());
				}
			}
			return img2;
		}


		// Histogramme NiveauGris
		public static BufferedImage histoG(BufferedImage img) {

			int[] tab = imageHistogram(img);

			tab = pourcentage(tab);

			return ecritHisto(tab);
		}


		// Trouver 2 coupures
		public static int[] coupure(int[] tab) {
			System.out.println("Coupure");
			int[] res = new int[2];
			for (int i=0;i<tab.length;i++) {
				if (tab[i]>10) {
					res[0]=i;
					i=tab.length;
				}
			}

			for (int i=res[0];i<tab.length;i++) {
				if (tab[i]<10) {
					res[1]=i;
					i=tab.length;
				}
			}
			return res;
		}

		// Trouver 2 coupures
		public static int[] coupure2(int[] tab) {
					System.out.println("Coupure 2");
					int[] res = new int[2];
					int x = 0;
					int m =0;
					for (int i=0;i<tab.length;i++) {
						if (tab[i]>75) {
							x = i-1;
							//System.out.println(i+" "+x);
						}
						if (tab[i]==100) {
							res[0]=i-x;
							//System.out.println(i);
							m = i ;
							i=tab.length;
						}
					}

					for (int j=m;j<tab.length;j++) {

						if (tab[j]<75) {
							res[1]=j-1;
							j=tab.length;
							}

					}

					return res;
				}


		// Histogramme Projection Noir  HistoR
		public static BufferedImage histoR(BufferedImage img) {
					int[] tab = new int[img.getWidth()];
					for(int x=0;x<img.getWidth();x++) {
						for (int y=0;y<img.getHeight();y++){
							int p = img.getRGB(x,y); // r�cup�ration des couleurs RGB du pixel a la position (x, y)
					        int r = (p>>16)&0xff;
					        if(-p==16777216) {
					        	tab[x]++;
					        }
						}
					}

					tab = pourcentage(tab);
					/*
					for (int i=0;i<tab.length;i++) {
						System.out.println(tab[i]);
					}
					*/
					int [] res = coupure(tab);
					System.out.println(res[0]+" " + res[1]+" " + res.length);
					if (res[0]==0 && res[1]==0) { res = coupure2(tab); System.out.println(res[0]+" " + res[1]+" " + res.length);}
					System.out.println(res[0]+" " + res[1]+" " + res.length);
					/*
					// Tab taille de creux
					int tabfinal[] = new int[res[1]-res[0]];
					for(int i =0;i<tabfinal.length;i++) {
						tabfinal[i]=tab[i+100];

					}
					*/
					return ecritHisto(tab);
				}


		// Nouvel Image en fonction de histoR
		public static BufferedImage resizeImage(BufferedImage img) {
			int[] tab = new int[img.getWidth()];
			for(int x=0;x<img.getWidth();x++) {
				for (int y=0;y<img.getHeight();y++){
					int p = img.getRGB(x,y); // r�cup�ration des couleurs RGB du pixel a la position (x, y)
			        int r = (p>>16)&0xff;
			        if(-p==16777216) {
			        	tab[x]++;
			        }
				}
			}

			tab = pourcentage(tab);
			int [] res = coupure(tab);
			if (res[0]==0 && res[1]==0) { res = coupure2(tab);}

			BufferedImage img3 = zeros(res[1]-res[0],img.getHeight());
				for(int x=0;x<img3.getWidth();x++) {
						for (int y=0;y<img3.getHeight();y++){
							int p = img.getRGB(x+res[0],y); // r�cup�ration des couleurs RGB du pixel a la position (x, y)
							int a = (p>>24)&0xff;
					        int r = (p>>16)&0xff;
					        int g = (p>>8)&0xff;
					        int b = p&0xff;
					        int[] pixel = {r,g,b,255};
					        img3.getRaster().setPixel(x, y, pixel);
							}
						}
			return img3;
		}


		public static BufferedImage coupureCentre(BufferedImage img) {
			BufferedImage new_img = zeros((int)(img.getWidth()/3)+1,img.getHeight());
			for(int x=(int)(img.getWidth()/3);x<(int)((img.getWidth()*2)/3)-1;x++) {
				for (int y=0;y<img.getHeight();y++){
					int p = img.getRGB(x,y); // r�cup�ration des couleurs RGB du pixel a la position (x, y)

			        int r = (p>>16)&0xff;
			        int g = (p>>8)&0xff;
			        int b = p&0xff;
			        int[] pixel = {r,g,b,255};
			        new_img.getRaster().setPixel(x-(int)(img.getWidth()/3), y, pixel);
			        }
				}

			return new_img;
		}

		public static BufferedImage coupureDroite(BufferedImage img) {
			BufferedImage new_img = zeros((int)(img.getWidth()/3)+1,img.getHeight());
			for(int x=(int)(img.getWidth()*2/3);x<(int)(img.getWidth())-1;x++) {
				for (int y=0;y<img.getHeight();y++){
					int p = img.getRGB(x,y); // r�cup�ration des couleurs RGB du pixel a la position (x, y)

			        int r = (p>>16)&0xff;
			        int g = (p>>8)&0xff;
			        int b = p&0xff;
			        int[] pixel = {r,g,b,255};
			        new_img.getRaster().setPixel(x-(int)(img.getWidth()*2/3), y, pixel);
			        }
				}

			return new_img;
		}
		
		public static BufferedImage coupureGauche(BufferedImage img) {
			BufferedImage new_img = zeros((int)(img.getWidth()/3)+1,img.getHeight());
			for(int x=0;x<(int)(img.getWidth())/3-1;x++) {
				for (int y=0;y<img.getHeight();y++){
					int p = img.getRGB(x,y); // r�cup�ration des couleurs RGB du pixel a la position (x, y)

			        int r = (p>>16)&0xff;
			        int g = (p>>8)&0xff;
			        int b = p&0xff;
			        int[] pixel = {r,g,b,255};
			        new_img.getRaster().setPixel(x, y, pixel);
			        }
				}

			return new_img;
		}
		
		public static void menu() {
			System.out.println("***** Détection d'escalier *****\n");
			System.out.println("\t1. Recadrage automatique");
			System.out.println("\t11. Recadrage Centre");
			System.out.println("\t12. Recadrage Droite");
			System.out.println("\t13. Recadrage Gauche");
			System.out.println("\t2. Appliquer une dilatation");
			System.out.println("\t3. Appliquer une érosion");
			System.out.println("\t4. Appliquer une ouverture");
			System.out.println("\t5. Appliquer une fermeture");
			System.out.println("\t6. Appliquer un lissage");
			System.out.println("\t7. Appliquer detection de contour Sobel");
			System.out.println("\t8. Reset");
			System.out.println("\t9. Compter le nombre de marche");
			System.out.println("\t0. Quitter");
			
		}


		public static void main(String[] args) throws IOException {
			
			File path = new File("/Users/maxiiiiiiiiime/Documents/Image/ImageEsc/esc12.jpg");
			
			BufferedImage img = null;

			try {
				img = ImageIO.read(path);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			BufferedImage gray = toGray(img);

			BufferedImage img2 = null;
			img2 = threshold(gray, otsuTreshold(gray));		// SEUILLAGE

			imshow(img2);
				
			BufferedImage test = img2;
			int choix=99;
			do {		
				menu();
				
				Scanner sc = new Scanner(System.in);
				
					try {
						choix = sc.nextInt();
					}
					catch(InputMismatchException e) {
						System.out.println("Erreur touche "+e.getMessage()+"\nRelancer le Programme");break;
					}

					if (choix<0 || choix>9) System.out.println("Mauvaise touche");
				
				switch(choix) {
					case 1: System.out.println("Recadrage automatique");
							test = resizeImage(test); imshow(test); break;
					
					case 11: System.out.println("Recadrage centre");
							test = coupureCentre(test); imshow(test); break;
							
					case 12: System.out.println("Recadrage centre");
							test = coupureDroite(test); imshow(test); break;
			
					case 13: System.out.println("Recadrage centre");
							test = coupureGauche(test); imshow(test); break;
					
					case 2: System.out.println("Appliquer une dilatation");
							test = MorphMath.dilate(test, 2); imshow(test); break;
					
					case 3:  System.out.println("Appliquer une érosion");
							test = MorphMath.erode(test, 2); imshow(test); break;
					
					case 4:  System.out.println("Appliquer une ouverture");
							test = MorphMath.close(test, 2); imshow(test); break;
					
					case 5: System.out.println("Appliquer une fermeture");
							test = MorphMath.open(test, 2); imshow(test); break;
					
					case 6: System.out.println("Appliquer un lissage");
					
							float[] m_conv = {3f,3f,3f,
											3f,3f,3f,
											3f,3f,3f};
							test = convolveOP(test, 3,3,m_conv); imshow(test); break;
					
					case 7: System.out.println("Appliquer detection de contour Sobel");
					
						float[] sob1 = {-1f,-2f,-1f,
										0f,0f,0f,
										1f,2f,1f};

						float[] sob2 = {-1f,0f,1f,
										-2f,0f,2f,
										-1f,0f,1f};
						
						test = convolveOP(test, 3,3,sob1); test = convolveOP(test, 3,3,sob2); imshow(test); break;
					
					case 8: System.out.println("Reset");
							test = img2; imshow(test); break;
					
					case 9: System.out.println("Compter le nombre de marche");
							test = Label8.getCC(test);
							System.out.println("Sur cette image, il y a "+(Label8.getNumberOfCC(test)-1)+" marches d'escalier");
							imshow(test); break;		
							
					case 0: System.out.println("***** À bientot *****");break;
					
					default : System.out.println("Mauvaise touche"); break;
				}
				
			}while(choix!=0);
					
		}

}
