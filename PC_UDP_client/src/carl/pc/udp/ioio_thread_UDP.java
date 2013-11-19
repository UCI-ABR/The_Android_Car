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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ioio_thread_UDP implements Runnable
{
	float gas;
    CAR_GUI car_state;
    Thread t;
    int size_p;
    short servo = 1500;
    short motor = 1500;
    

    public ioio_thread_UDP(CAR_GUI gui) 
    {
        car_state = gui;
        try 
        {
        	t = new Thread(this);
        	t.start();
        } 
        catch (Exception e){e.printStackTrace();}
    }
    
    public void run() 
    {
    	System.out.println("IOIO thread waiting...");
            
        	handleConnection_UDP();
    }
    
    public void handleConnection_UDP() 
    {
    	DatagramSocket socket=null;

    	try
    	{
    		InetAddress serverAddr = InetAddress.getLocalHost();
    		socket = new DatagramSocket(9002, serverAddr);
    	}
    	catch (Exception e){e.printStackTrace();} 

    	byte[] buffer = new byte[1];
    	DatagramPacket packet_R = new DatagramPacket(buffer, buffer.length);
    	InetAddress IP_phone=null;
    	int port_phone = -1;
    	
		try
		{
			socket.receive(packet_R);
		}
		catch (Exception e) {} 

		IP_phone = packet_R.getAddress();
		port_phone = packet_R.getPort();
		System.out.println("port android: " + port_phone);

    	while (true) 
    	{    		
			if(car_state.RIGHT == true)
			{
				if(servo >1000) servo = (short) (servo - 10);
			}
			else
			{
				if(car_state.LEFT == true)
				{
					if(servo <2000) servo = (short) (servo + 10);
				}
				else
				{
					if(servo != 1500)					//default
					{
						if(servo<1500) servo = (short) (servo + 10);
						else 		   servo = (short) (servo - 10);
					}
				}
			}
			
			if(car_state.UP == true)
			{
				motor = 1471;
			}
			else
			{
				if(car_state.DOWN == true)
				{
					motor = 1600;
				}
				else
				{
					motor = 1500;
				}
			}

    		if(port_phone > -1)
    		{
    			byte[] data2 = new byte[4];
    			data2[0] = (byte) (servo >> 8);
    			data2[1] = (byte) servo; 
    			data2[2] = (byte) (motor >> 8);
    			data2[3] = (byte) motor; 

    			size_p = data2.length;
    			DatagramPacket packet_S = new DatagramPacket(data2, size_p, IP_phone, port_phone);
    			try
    			{
    				socket.send(packet_S);
    			}
    			catch (Exception e){e.printStackTrace();} 
    		}

    		try 
    		{
    			Thread.sleep(10);
    		} 
    		catch (InterruptedException e) {e.printStackTrace();}
    	}
    }
}
