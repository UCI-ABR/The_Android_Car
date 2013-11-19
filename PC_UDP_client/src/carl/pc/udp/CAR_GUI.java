/*******************************************************************************************************
Copyright (c) 2011 Regents of the University of California.
All rights reserved.

This software was developed at the University of California, Irvine.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in
   the documentation and/or other materials provided with the
   distribution.

3. All advertising materials mentioning features or use of this
   software must display the following acknowledgment:
   "This product includes software developed at the University of
   California, Irvine by Nicolas Oros, Ph.D.
   (http://www.cogsci.uci.edu/~noros/)."

4. The name of the University may not be used to endorse or promote
   products derived from this software without specific prior written
   permission.

5. Redistributions of any form whatsoever must retain the following
   acknowledgment:
   "This product includes software developed at the University of
   California, Irvine by Nicolas Oros, Ph.D.
   (http://www.cogsci.uci.edu/~noros/)."

THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
IN NO EVENT SHALL THE UNIVERSITY OR THE PROGRAM CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************************************/

package carl.pc.udp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class CAR_GUI extends BasicGameState 
{
	/** The ID given to this state */
	public static final int ID = 2;
	/** The font to write the message with */
	UnicodeFont uFont, uFont2;
	/** The image to be display */
	private Image logo, ima_up, ima_down, ima_left, ima_right;
	ByteArrayInputStream bis;
    Texture the_texture;
	
	GameContainer the_container;
	
	float x_O,y_O,z_O,x_A,y_A,z_A;
	float gas;
	int servo_val = 1500;
	boolean key_pressed=false, LEFT=false, RIGHT=false, UP=false, DOWN=false;
	
	cam_thread_UDP cam_thread;
	sensors_thread_UDP sensors_thread;
	ioio_thread_UDP IOIO_thread;
	
	
	/**
	 * @see org.newdawn.slick.state.BasicGameState#getID()
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @see org.newdawn.slick.state.BasicGameState#init(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame)
	 */
	@SuppressWarnings("unchecked")
	public void init(GameContainer container, StateBasedGame game) throws SlickException 
	{
		String fontPath = "data-latin.ttf";
		uFont = new UnicodeFont(fontPath , 50, false, false); //Create Instance
		uFont.addAsciiGlyphs();   //Add Glyphs
		uFont.addGlyphs(fontPath); //Add Glyphs
		uFont.getEffects().add(new ColorEffect(java.awt.Color.white)); //Add Effects
		uFont.loadGlyphs();  //Load Glyphs
		
		uFont2 = new UnicodeFont(fontPath , 20, false, false); //Create Instance
		uFont2.addAsciiGlyphs();   //Add Glyphs
		uFont2.addGlyphs(fontPath); //Add Glyphs
		uFont2.getEffects().add(new ColorEffect(java.awt.Color.red)); //Add Effects
		uFont2.loadGlyphs();  //Load Glyphs
		
		logo = new Image("CARL_Logo.jpg"); 
		ima_up = new Image("up_arrow.png"); 
		ima_down = new Image("down_arrow.png"); 
		ima_left = new Image("left_arrow.png"); 
		ima_right = new Image("right_arrow.png");
		
		ima_up.setAlpha((float) 0.5);
		ima_down.setAlpha((float) 0.5);
		ima_left.setAlpha((float) 0.5);
		ima_right.setAlpha((float) 0.5);
		
		cam_thread = new cam_thread_UDP(this);
		sensors_thread = new sensors_thread_UDP(this);
		IOIO_thread = new ioio_thread_UDP(this);
	}

	/**
	 * @see org.newdawn.slick.state.BasicGameState#render(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, StateBasedGame game, Graphics g) 
	{
		try 
		{
			if(bis != null)
			{
				the_texture = TextureLoader.getTexture("jpeg", bis);
				logo.setRotation(90);									// if phone is in portrait mode
				logo.setTexture(the_texture);
			}
		} catch (IOException e) {e.printStackTrace();}
		
		uFont.drawString(200,50, "Camera");		
		g.drawImage(logo,300-(logo.getWidth()/2),300-(logo.getHeight()/2));
		uFont.drawString(200,550, "Sensors");		
		uFont2.drawString(50,650, "Acceleration" + "\n x: " + x_A + "\n y: " +  y_A + "\n z: " +   z_A);
		uFont2.drawString(300,650, "Orientation" + "\n x: " +  x_O + "\n y: " +  y_O + "\n z: " +  z_O);
			
		uFont.drawString(640,50, "Motors");		
		g.drawImage(ima_up,700,250);
		g.drawImage(ima_down,700,350);
		g.drawImage(ima_left,650,300);
		g.drawImage(ima_right,750, 300);

		if(LEFT == true) ima_left.setAlpha((float) 1.0);
		else
		{
			if(RIGHT == true)  ima_right.setAlpha((float) 1.0);
			else
			{				
				ima_left.setAlpha((float) 0.5);
				ima_right.setAlpha((float) 0.5);				
			}
		}

		if(UP == true) ima_up.setAlpha((float) 1.0);
		else 
		{
			if(DOWN == true)  ima_down.setAlpha((float) 1.0);
			else
			{
				ima_up.setAlpha((float) 0.5);
				ima_down.setAlpha((float) 0.5);
			}
		}	
		uFont2.drawString(600,450, "Pulse Servo \n" + IOIO_thread.servo); 
		uFont2.drawString(800,450, "Pulse Motor \n" + IOIO_thread.motor); 
	}
	
	public void set_image(ByteArrayInputStream b)
	{
		bis =b;		
	}
	
	public void set_sensors_values(float x_O2, float y_O2, float z_O2, float x_A2, float y_A2, float z_A2)
	{
		x_O = x_O2;
		y_O = y_O2;
		z_O = z_O2;
		x_A = x_A2;
		y_A = y_A2;
		z_A = z_A2;
	}

	/**
	 * @see org.newdawn.slick.state.BasicGameState#update(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame, int)
	 */
	public void update(GameContainer container, StateBasedGame game, int delta) 
	{	
		synchronized(this)
		{
			RIGHT = false;
			LEFT = false;
			UP = false;
			DOWN = false;
			
			Input input = container.getInput();
			
			if(input.isKeyDown(Input.KEY_A) ^ input.isKeyDown(Input.KEY_D))	//if ONLY one key is pressed...otherwise, don't do anything
			{
				if (input.isKeyDown(Input.KEY_A))			//left
				{
					LEFT = true;
				}

				if (input.isKeyDown(Input.KEY_D))		//right
				{
					RIGHT = true;
				}
			}
			if(input.isKeyDown(Input.KEY_W) ^ input.isKeyDown(Input.KEY_S))	//if ONLY one key is pressed...otherwise, don't do anything
			{
				if (input.isKeyDown(Input.KEY_W) && DOWN==false)			//up
				{
					UP = true;
				}

				if (input.isKeyDown(Input.KEY_S) && UP==false)		//down
				{
					DOWN = true;
				}
			}
		}
	}

	/**
	 * @see org.newdawn.slick.state.BasicGameState#keyReleased(int, char)
	 */
	public void keyPressed(int key, char c) 
	{
		if (key == Input.KEY_ESCAPE)System.exit(0);
	}
}
