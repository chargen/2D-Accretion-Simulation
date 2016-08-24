/**
 * @(#)ParticleInteraction.java
 *
 *
 * @author 
 * @version 1.00 2016/8/24
 */
import java.lang.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
public class ParticleInteraction {
        
    /**
     * Creates a new instance of <code>ParticleInteraction</code>.
     */
    public ParticleInteraction() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	//Create display
    	BufferedImage displayImage = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB );
    	JFrame frame = new JFrame();
		ImageIcon icon = new ImageIcon(displayImage);
		JLabel label = new JLabel(icon);
		frame.add(label);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		int Black = new Color(0,0,0).getRGB();
		int White = new Color(255,255,255).getRGB();
    	//Set simulation parameters, next 7 lines
    	int particleCount = 1000;
    	double startingMass = 1.0;
    	double xOrigin = 0.0;
    	double yOrigin = 0.0;
    	double diskRadius = 1.0;
    	double deltaTime = 0.001; //timestep in seconds
    	double constantGravity=0.00001; //gravitational constant
    	double initialVel=1.0;
    	
        //Create array of particles and place them in the disk, next 4 lines
        Particle[] particleArray = new Particle[particleCount];
        for(int i=0; i<particleCount; i++){
        	particleArray[i]=new Particle(diskRadius,xOrigin,yOrigin,startingMass,initialVel);
        }
        
        
        while(true){
        	//Calculate total force on particle, next 10 lines
        	for(int i=0; i<particleCount; i++){
        		for(int j=0; j<particleCount; j++){
        			if(i!=j){
        				double distance = Math.sqrt(((particleArray[i].getXPosition()-particleArray[j].getXPosition())*(particleArray[i].getXPosition()-particleArray[j].getXPosition()))+((particleArray[i].getYPosition()-particleArray[j].getYPosition())*(particleArray[i].getYPosition()-particleArray[j].getYPosition())));
        				double angle = Math.atan2(particleArray[i].getYPosition()-particleArray[j].getYPosition(),particleArray[i].getXPosition()-particleArray[j].getXPosition());
        				particleArray[i].setXForce(particleArray[i].getXForce()+(-1.0)*Math.cos(angle)*(constantGravity*particleArray[i].getMass()*particleArray[j].getMass()/(distance*distance)));
        				particleArray[i].setYForce(particleArray[i].getYForce()+(-1.0)*Math.sin(angle)*(constantGravity*particleArray[i].getMass()*particleArray[j].getMass()/(distance*distance)));
        			}
        		}
        	}
        	//Update velocities and positions
        	for(int i=0; i<particleCount; i++){
        		particleArray[i].setXVelocity(particleArray[i].getXVelocity()+particleArray[i].getXForce()*deltaTime);
        		particleArray[i].setYVelocity(particleArray[i].getYVelocity()+particleArray[i].getYForce()*deltaTime);
        		particleArray[i].setXPosition(particleArray[i].getXPosition()+particleArray[i].getXVelocity()*deltaTime);
        		particleArray[i].setYPosition(particleArray[i].getYPosition()+particleArray[i].getYVelocity()*deltaTime);
       		}
       		
       		for(int r=0;r<800;r++){
				for(int c=0;c<800;c++){
					displayImage.setRGB(r,c, Black);
				}
			}
			for(int i=0; i<particleCount; i++){
				int dispX = (int)((particleArray[i].getXPosition()*200)+400);
				int dispY = (int)((particleArray[i].getYPosition()*200)+400);
				if ((dispX<800)&&(dispY<800)&&(dispX>0)&&(dispY>0)){
					displayImage.setRGB(dispX,dispY, White);
				}
				//System.out.println((int)(particleArray[i].getXPosition()*200)+400+","+(int)((particleArray[i].getYPosition()*200)+400));
				//System.out.println(particleArray[i].getXPosition()+","+particleArray[i].getYPosition());
       		}
       		//frame = new JFrame();
			
			//icon = new ImageIcon(displayImage);
			//label = new JLabel(icon);
			//frame.add(label);
			//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
			try {
   					Thread.sleep(17);
   				} catch (Exception e) {
   					System.out.println(e);
   				}
   			frame.repaint();
       		//System.out.print("("+String.format("%.5g",particleArray[0].getXPosition())+",");
       		//System.out.print(String.format("%.5g",particleArray[0].getYPosition())+") Distance from center:");
       		//System.out.print(String.format("%.5g",Math.sqrt(((particleArray[0].getXPosition())*(particleArray[0].getXPosition()))+((particleArray[0].getYPosition())*(particleArray[0].getYPosition())))));
        	//System.out.print(" Velocity: ("+String.format("%.5g",particleArray[0].getXVelocity())+",");
       		//System.out.println(String.format("%.5g",particleArray[0].getYVelocity())+")");
       		//System.out.print("("+String.format("%.5g",particleArray[1].getXPosition())+",");
       		//System.out.print(String.format("%.5g",particleArray[1].getYPosition())+") Distance:");
       		//System.out.println(String.format("%.5g",Math.sqrt(((particleArray[0].getXPosition()-particleArray[1].getXPosition())*(particleArray[0].getXPosition()-particleArray[1].getXPosition()))+((particleArray[0].getYPosition()-particleArray[1].getYPosition())*(particleArray[0].getYPosition()-particleArray[1].getYPosition())))));
        	
        }
    }
}
