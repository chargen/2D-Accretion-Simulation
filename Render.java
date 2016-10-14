/**
 * @(#)Render.java
 *
 *
 * @author 
 * @version 1.00 2016/9/16
 */

import java.util.Scanner;
import java.io.*;
import java.awt.geom.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import javax.imageio.*;
public class Render {
	private int imageSizeX;
	private int imageSizeY;
	private int length;
	private double[] x;
	private double[] y;
	private double[] xOld;
	private double[] yOld;
	private double[] m;
	private int focusOn = 1;
	private double centerX;
	private double centerY;
	private BufferedImage particleImage;
	private BufferedImage trailImage;
	private int blackInt = new Color(0,0,0).getRGB();
	private int whiteInt = new Color(255,255,255).getRGB();
	private Graphics2D trailGraphics;
	private Graphics2D particleGraphics;
	private String directoryTextString;
	private String directoryImageString;
	private String directoryImageNameString;
	private int particleCount;
	private int trailLength;
	private double minMass;
	private int frameStart;
	private int frameSkip;
	private int frameCount;
	private int picCount;
	private double saveOn;
	private Color[] colors;
	private double zoom;
	private BufferedReader input;
	private String dataString;
	private long startTime, elapsedTime;
	private File inputFile;
	private FileReader inputFW;
	private Thread ImageSaver;
	private double incrementer;
	
	
    public Render(String name, int particles, int lengthMultiplier, double min, int frameStartConstruct, int frameSkipConstruct, int resolutionX, int resolutionY, double zoomFactor) {
    	directoryImageString = ".\\"+name+"\\image frames\\";
    	directoryImageNameString = name+"\\image frames";
    	particleCount = particles;
    	trailLength   = lengthMultiplier;
    	minMass		  = min;
    	frameStart    = frameStartConstruct;
    	frameSkip     = 1;
    	incrementer   = frameSkipConstruct;
    	imageSizeX    = resolutionX - resolutionX%2;
    	imageSizeY    = resolutionY - resolutionY%2;
    	zoom          = zoomFactor;
    	createDirectory(name);	
    }
    public void methodRunner() throws IOException{
    	length=particleCount;
    	trailImage = new BufferedImage(imageSizeX, imageSizeY, BufferedImage.TYPE_INT_ARGB );
    	trailGraphics = trailImage.createGraphics();
    	trailGraphics.setColor(Color.black);
    	trailGraphics.fillRect(0,0,imageSizeX,imageSizeY);
    	x = new double[particleCount];
		xOld = new double[particleCount];
		y = new double[particleCount];
		yOld = new double[particleCount];
		m = new double[particleCount];
		colors = new Color[particleCount];
		for(int i = 0;i<particleCount;i++){
			colors[i] = new Color((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256),128);
		}
		x[0]=0;
		frameCount = 0;
		picCount = 0;
		saveOn = 1;
		for (int i=1; i<frameStart; i++){
			input.readLine();
			frameCount++;
		}
		while((dataString = input.readLine()) != null){
				frameCount++;
	    		startTime=System.nanoTime();
		    	readTextFile(dataString);
		    	focus();
		    	drawParticles();
		    	if (trailLength!=0){
		    		fadeTrails();
		    		drawTrails();
		    	}
		    	saveImage();
    	}
    	if (dataString == null){
    		System.out.println("Reached a null");
    	}
    }
    private void readTextFile(String dataString){
        Scanner inputScanner = new Scanner(dataString);
        int j = 0;
        String temp = "";
        while(inputScanner.hasNext()){
        	temp = inputScanner.next();
        	xOld[j]=x[j];
        	yOld[j]=y[j];
        	if (temp.equals("SKIP")){
        		x[j]=10001;
	        	y[j]=10001;
	        	m[j]=0;
        	} else {
        		x[j]=Double.parseDouble(temp);
	        	y[j]=inputScanner.nextDouble();
	        	m[j]=inputScanner.nextDouble();
        	}
	        
	        j++;
        }
        if (picCount==0){
        	for(int i = 0; i < particleCount; i++){
	        	xOld[i]=x[i];
	        	yOld[i]=y[i];
        	}
        }
        inputScanner.close();
        
    }
    
    private void focus(){
    	if (focusOn==1){  //Full System Barycenter
	        double sumMass=0;
			double sumX=0;
			double sumY=0;
			for(int i=0; i<length;i++){
					sumMass+= m[i];
					sumX   += x[i]*m[i];
					sumY   += y[i]*m[i];
			}
			sumX/=sumMass;
			sumY/=sumMass;
			centerX=sumX;
			centerY=sumY;
        }else if(focusOn==2){  //Largest Particle
        	double max = Double.MIN_VALUE;
        	for(int i=0; i<length;i++){
        		if (m[i]>max){
        			max=m[i];
        			centerX=x[i];
        			centerY=y[i];
        		}
        	}
        }else{ //Origin
        	centerX=0;
        	centerY=0;
        }
    }
    
    private void drawParticles(){
    	particleImage = new BufferedImage(imageSizeX, imageSizeY, BufferedImage.TYPE_INT_ARGB );
    	particleGraphics = particleImage.createGraphics();
    	particleGraphics.setColor(new Color(0, true));
    	particleGraphics.fillRect(0,0,imageSizeX,imageSizeY);
    	for(int i=0; i<length; i++){
			int dispX = (int)(((x[i]-centerX)*200*zoom)+imageSizeX/2);
			int dispY = (int)(((y[i]-centerY)*200*zoom)+imageSizeY/2);
			int radius = (int)((Math.sqrt(m[i]))/2*zoom);
			for (int j = dispX-radius; j <= dispX+radius;j++){
				for (int k = dispY-radius; k <= dispY+radius;k++){
					if((j<imageSizeX)&&(k<imageSizeY)&&(j>0)&&(k>0)){
						if (((dispX-j)*(dispX-j)+(dispY-k)*(dispY-k))<=(radius*radius)){
							particleImage.setRGB(j,k, whiteInt);
						}
					}
				}
			}
		}
	}
	private void fadeTrails(){
		if (frameCount%trailLength==0){
			int testCol = 0;
			for(int i = 0; i<imageSizeX; i++){
				for(int j = 0; j<imageSizeY; j++){
					testCol = trailImage.getRGB(i,j);
					if (testCol!= -16777216){
						int R = (testCol & 255)-1, G = ((testCol >> 8) & 255)-1, B = ((testCol >> 16) & 255)-1, A = ((testCol >> 24) & 255);
						R=Math.max(0,R);
						G=Math.max(0,G);
						B=Math.max(0,B);
						Color c = new Color(B, G, R, A);
						trailImage.setRGB(i, j, c.getRGB());
					}
				}
			}
		}
	}
	private void drawTrails(){
		for(int i = 0; i<length; i++){
			if (x[i]<10000){
				if (m[i]>(minMass)){
					trailGraphics.setColor(colors[i]);
					trailGraphics.draw (new Line2D.Double((((x[i]-centerX)*200*zoom)+imageSizeX/2),(((y[i]-centerY)*200*zoom)+imageSizeY/2),(((xOld[i]-centerX)*200*zoom)+imageSizeX/2),(((yOld[i]-centerY)*200*zoom)+imageSizeY/2)));
					if (m[i]>(2*minMass)){
						trailGraphics.draw (new Line2D.Double((((x[i]-centerX)*200*zoom)+imageSizeX/2)+1,(((y[i]-centerY)*200*zoom)+imageSizeY/2),(((xOld[i]-centerX)*200*zoom)+imageSizeX/2)+1,(((yOld[i]-centerY)*200*zoom)+imageSizeY/2)));
						trailGraphics.draw (new Line2D.Double((((x[i]-centerX)*200*zoom)+imageSizeX/2),(((y[i]-centerY)*200*zoom)+imageSizeY/2)-1,(((xOld[i]-centerX)*200*zoom)+imageSizeX/2),(((yOld[i]-centerY)*200*zoom)+imageSizeY/2)-1));
						trailGraphics.draw (new Line2D.Double((((x[i]-centerX)*200*zoom)+imageSizeX/2)-1,(((y[i]-centerY)*200*zoom)+imageSizeY/2),(((xOld[i]-centerX)*200*zoom)+imageSizeX/2)-1,(((yOld[i]-centerY)*200*zoom)+imageSizeY/2)));
						trailGraphics.draw (new Line2D.Double((((x[i]-centerX)*200*zoom)+imageSizeX/2),(((y[i]-centerY)*200*zoom)+imageSizeY/2)+1,(((xOld[i]-centerX)*200*zoom)+imageSizeX/2),(((yOld[i]-centerY)*200*zoom)+imageSizeY/2)+1));
					}
				}
			}
		}
	}
	private void saveImage(){
		if ((int)saveOn==frameCount){
			BufferedImage outputImage = new BufferedImage(imageSizeX, imageSizeY, BufferedImage.TYPE_INT_ARGB );
			Graphics2D outputGraphics = outputImage.createGraphics();
			outputGraphics.drawImage(trailImage, 0, 0, null);
			outputGraphics.drawImage(particleImage, 0, 0, null);
			File f = null;
			try{
	            f = new File(directoryImageString+String.format("%010d", picCount)+".PNG");
	            ImageIO.write(outputImage, "PNG", f);
	        }
	        catch(Exception e){
	        	System.out.println(f.getAbsolutePath() + "\nFAILURE TO SAVE IMAGE");
	            e.printStackTrace();
	        }
	    	picCount++;
	    	saveOn+=incrementer;
	    	incrementer+=0.000;
	    	elapsedTime=System.nanoTime()-startTime;
		    System.out.println("Image "+picCount+" (Frame " + frameCount + ") saved (took " + String.format("%014d", elapsedTime) + " nanoseconds)");
		}
    }
    private void createDirectory(String name){
    	File directory = new File(directoryImageNameString);
		if (!directory.exists()) {
		    boolean result = false;
		
		    try{
		        directory.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		        System.out.println(directory.getAbsolutePath() + "\nFAILURE TO MAKE DIRECTORY");
        		se.printStackTrace();
		    }        
		    if(result) {
		    	System.out.println("Directory created!");
		    }
		}else{
			//if the directory exists, delete all existing images
			System.out.println("Clearing directory");
			for(File file: directory.listFiles()) 
    			if (!file.isDirectory()) 
        			file.delete();
        	System.out.println("Directory cleared!");
		}
		String inputFileName = ".\\" + name + "\\raw data.txt";
        inputFile = new File(inputFileName);
        input = null;
        try {
        	inputFW = new FileReader (inputFile);
        }
        catch (IOException e){
        	System.out.println(inputFile.getAbsolutePath() + "\nFAILURE TO MAKE FileReader");
        	e.printStackTrace();
        }
        input = new BufferedReader(inputFW);

	}
    
}