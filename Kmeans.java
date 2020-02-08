package kmeans;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Kmeans {
	public static void main(String [] args){
		if (args.length < 3){
		    System.out.println("Usage: Kmeans <actual image filename> <compressed image filename> <k value>");
		    return;
		}
		try{
		    BufferedImage originalImage = ImageIO.read(new File(args[0]));
		    int k = Integer.parseInt(args[2]); 
		    BufferedImage kmeansJpg = kmeans_helper(originalImage,k);    
		    ImageIO.write(kmeansJpg, "jpg", new File(args[1])); 
		    
		}catch(IOException e){
		    System.out.println(e.getMessage());
		}	
	}
	
	private static BufferedImage kmeans_helper(BufferedImage originalImage, int k){
		int w=originalImage.getWidth();
		int h=originalImage.getHeight();
		BufferedImage kmeansImage = new BufferedImage(w,h,originalImage.getType());
		Graphics2D g = kmeansImage.createGraphics();
		g.drawImage(originalImage, 0, 0, w,h , null);
		int[] rgb=new int[w*h];
		int count=0;
		for(int i=0;i<w;i++){
		    for(int j=0;j<h;j++){
		    	rgb[count++]=kmeansImage.getRGB(i,j);
		    }
		}

		kmeans(rgb,k);

		count=0;
		for(int i=0;i<w;i++){
		    for(int j=0;j<h;j++){
		    	kmeansImage.setRGB(i,j,rgb[count++]);
		    }
		}
		return kmeansImage;
	}

  private static void kmeans( int[] rgb, int k ) {

    int[] oldCentroids = new int[k];   
    int[] centroids = new int[k];   
    int[] pointscount = new int[k];  
    int[] rcount = new int[k];   
    int[] gcount = new int[k]; 
    int[] bcount = new int[k];  
    int[] cluster = new int[rgb.length];        
    double distance = 0;                   
    int assignedcentroid = 0;               
    
    Arrays.fill(pointscount, 0);
    Arrays.fill(rcount, 0);
    Arrays.fill(gcount, 0);
    Arrays.fill(bcount, 0);
   
    for ( int i = 0; i < k; i++ ) {
      Random random = new Random();
      int centroid = 0;
      centroid = rgb[random.nextInt( rgb.length )];
      centroids[i] = centroid;
    }
    
    while( !isConverged(oldCentroids, centroids) ) {
      for ( int i = 0; i < centroids.length; i++ ) {
    	  oldCentroids[i] = centroids[i];
      }

      for ( int i = 0; i < rgb.length; i++ ) {
    	  double maxEucDistance = Double.MAX_VALUE;
    	  for ( int j = 0; j < centroids.length; j++ ) {
    		  distance = EuclideanDistance( rgb[i], centroids[j] );
    		  if ( distance < maxEucDistance ) {
    			  maxEucDistance = distance;
    			  assignedcentroid = j;
    		  }
    	  }

    	  cluster[i] = assignedcentroid;
    	  pointscount[assignedcentroid]++;
    	  rcount[assignedcentroid] +=   ((rgb[i] & 0x00FF0000) >>> 16);
    	  gcount[assignedcentroid] += ((rgb[i] & 0x0000FF00) >>> 8);
    	  bcount[assignedcentroid] +=  ((rgb[i] & 0x000000FF));
      }

      for ( int i = 0; i < centroids.length; i++ ) {
    	  int averageOfRed =   (int)(rcount[i] / pointscount[i]);
    	  int averageOfGreen = (int)(gcount[i] / pointscount[i]);
    	  int averageOfBlue =  (int)(bcount[i] / pointscount[i]);
    	  centroids[i] = ((averageOfRed & 0x000000FF) << 16) | ((averageOfGreen & 0x000000FF) << 8) | (averageOfBlue & 0x000000FF);
      }
   } 

    for ( int i = 0; i < rgb.length; i++ ) {
      rgb[i] = centroids[cluster[i]];
    }
  }
  private static boolean isConverged( int[] oldCentroids, int[] centroids ) {
	  for ( int i = 0; i < centroids.length; i++ ) {
		  if ( oldCentroids[i] != centroids[i] ) {
		        return false;
		  }
	  }
      return true;
  }

  private static double EuclideanDistance( int pixel, int centroid ) {
    int red = ((pixel & 0x00FF0000) >>> 16) - ((centroid & 0x00FF0000) >>> 16);
    int green = ((pixel & 0x0000FF00) >>> 8)  - ((centroid & 0x0000FF00) >>> 8);
    int blue = (pixel & 0x000000FF) - (centroid & 0x000000FF);
    return Math.sqrt( Math.pow(red, 2) + Math.pow(green, 2) + Math.pow(blue, 2) );
  }
}