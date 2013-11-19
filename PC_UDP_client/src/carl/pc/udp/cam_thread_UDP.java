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
import java.lang.Runnable;
import java.lang.Thread;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class cam_thread_UDP implements Runnable
{
    int nb = 0;
    CAR_GUI car_state;
    Thread t;
    
	public static int HEADER_SIZE = 5;
	public static int DATAGRAM_MAX_SIZE = 1450;
	public static int DATA_MAX_SIZE = DATAGRAM_MAX_SIZE - HEADER_SIZE;

    public cam_thread_UDP(CAR_GUI gui) 
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
        System.out.println("Cam thread waiting...");
		           
        handleConnection_UDP();
    }
    
    public void handleConnection_UDP() 
    {
		int current_frame = -1;
		int slicesStored = 0;
		byte[] imageData = null;
		DatagramSocket socket=null;

		try 
		{		         	
			InetAddress serverAddr = InetAddress.getLocalHost();
			System.out.println("PC IP address: " + serverAddr.getHostAddress());
			socket = new DatagramSocket(9000, serverAddr);
			byte[] buffer = new byte[DATAGRAM_MAX_SIZE];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			
			while (true) 
			{				
				socket.receive(packet);				
				byte[] data = packet.getData();			
				int frame_nb = (int)data[0];
				int nb_packets = (int)data[1];
				int packet_nb = (int)data[2];
				int size_packet = (int) ((data[3] & 0xff) << 8 | (data[4] & 0xff)); 
		
				if((packet_nb==0) && (current_frame != frame_nb))
				{
					current_frame = frame_nb;
					slicesStored = 0;				
					imageData = new byte[nb_packets * DATA_MAX_SIZE];
				}
				
				if(frame_nb == current_frame)
				{
						System.arraycopy(data, HEADER_SIZE, imageData, packet_nb * DATA_MAX_SIZE, size_packet);
						slicesStored++;				
				}

				/* If image is complete display it */
				if (slicesStored == nb_packets) 
				{					
					ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
					car_state.set_image(bis);
				}
			}
		} catch (IOException e) 
		{
			e.printStackTrace();
			socket.close();
		} 
    }
}
