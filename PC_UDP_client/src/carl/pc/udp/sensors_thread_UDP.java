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
import java.io.IOException;
import java.lang.Runnable;
import java.lang.Thread;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class sensors_thread_UDP implements Runnable
{
	float x_O,y_O,z_O,x_A,y_A,z_A;
    CAR_GUI car_state;
    Thread t;

    public sensors_thread_UDP(CAR_GUI gui) 
    {
        try 
        {
        	t = new Thread(this);
        	t.start();
        } 
        catch (Exception e){e.printStackTrace();}
        car_state = gui;
    }
    
    public void run() 
    {
    	System.out.println("sensors thread waiting...");
          
    	handleConnection_UDP();
    }
    
    public void handleConnection_UDP() 
    {
		DatagramSocket socket=null;

		try 
		{		         	
			InetAddress serverAddr = InetAddress.getLocalHost();
			socket = new DatagramSocket(9001, serverAddr);
			byte[] buffer = new byte[12];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			
			while (true) 
			{				
				socket.receive(packet);
				byte[] data = packet.getData();	
				
				short nb = (short) ((data[0] & 0xff) << 8 | (data[1] & 0xff)); 
				x_O = (float)nb / 100;
				
				nb = (short) ((data[2] & 0xff) << 8 | (data[3] & 0xff)); 
				y_O = (float)nb / 100;
				
				nb = (short) ((data[4] & 0xff) << 8 | (data[5] & 0xff)); 
				z_O = (float)nb / 100;
				
				nb = (short) ((data[6] & 0xff) << 8 | (data[7] & 0xff)); 
				x_A = (float)nb / 100;
				
				nb = (short) ((data[8] & 0xff) << 8 | (data[9] & 0xff)); 
				y_A = (float)nb / 100;
				
				nb = (short) ((data[10] & 0xff) << 8 | (data[11] & 0xff)); 
				z_A = (float)nb / 100;
				
				car_state.set_sensors_values(x_O, y_O, z_O, x_A, y_A, z_A);
				
				Thread.sleep(10);
								
//				System.out.println("x: " + x_O + " y: "+ y_O + " z: "+ z_O + " x: "+ x_A + " y: "+ y_A + " z: " + z_A);
			}
		} catch (IOException e) 
		{
			e.printStackTrace();
			socket.close();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }
}
