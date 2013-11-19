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
package carl.IOIO_car;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.IOIOFactory;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import android.util.Log;

public class IOIO_Thread extends Thread 
{
	/** Subclasses should use this field for controlling the IOIO. */
	protected IOIO ioio_;
	private boolean abort_ = false;
	private PwmOutput servo, motor;
	int size_p;
	int servo_val = 1500, motor_val=1500;
	
    final String tag = "Sensors";
	InetAddress serverAddr;
	DatagramSocket socket;	
	String ip_address;
	
	Android_Activity main_app;
	boolean START = true;
	int a_nb=0;
	
	
	public IOIO_Thread(Android_Activity app, String ip)
	{
		main_app = app;
		ip_address = ip;
    	try
    	{ 
    		serverAddr = InetAddress.getByName(ip_address);
    		socket = new DatagramSocket();
    	}
    	catch (Exception exception) 
    	{
    		Log.e(tag, "Error: ", exception);
    	}
	}

	/** Not relevant to subclasses. */
	@Override
	public final void run() 
	{
		super.run();
		while (true) 
		{
			try 
			{
				synchronized (this) 
				{
					if (abort_) {break;}
					ioio_ = IOIOFactory.create();
				}
				ioio_.waitForConnect();
				setup();
				while (true) 
				{
					loop();
				}
			} 
			catch (ConnectionLostException e) 
			{
				if (abort_) {break;}
			} 
			catch (Exception e) 
			{
				Log.e("AbstractIOIOActivity","Unexpected exception caught", e);
				ioio_.disconnect();
				break;
			} 
			finally 
			{
				try {ioio_.waitForDisconnect();
				} 
				catch (InterruptedException e) {}
			}
		}
	}

	protected void setup() throws ConnectionLostException 
	{
		motor = ioio_.openPwmOutput(new DigitalOutput.Spec(5, DigitalOutput.Spec.Mode.OPEN_DRAIN), 50);
		servo = ioio_.openPwmOutput(new DigitalOutput.Spec(7, DigitalOutput.Spec.Mode.OPEN_DRAIN), 50);
		
		servo.setPulseWidth(1500);		// pulse is between 1000 and 2000
		motor.setPulseWidth(1500);
		
		/*********************  send phone IP address to computer  *********************/
		byte[] data = new byte[1];			
		data[0] = (byte) (1);
		size_p = data.length;
		DatagramPacket packet = new DatagramPacket(data, size_p, serverAddr, 9002);
		try 
		{
			socket.send(packet); 											
		} 
		catch (IOException e) {Log.e("IOIO_thread","error: ", e);}
	}

	protected void loop() throws ConnectionLostException 
	{
		try 
		{			
			byte[] data2 = new byte[4];
			DatagramPacket receivePacket = new DatagramPacket(data2, data2.length);
    		try
    		{
    			socket.receive(receivePacket);
    		}
    		catch (Exception e) {}
    		
			byte[] data3 = receivePacket.getData();		
			servo_val = (int) ((data3[0] & 0xff) << 8 | (data3[1] & 0xff)); 
			motor_val = (int) ((data3[2] & 0xff) << 8 | (data3[3] & 0xff)); 
			servo.setPulseWidth(servo_val);		// pulse is between 1000 and 2000
			motor.setPulseWidth(motor_val);
		} 
		catch (Exception e) {Log.e("IOIO_thread","error: ", e);} 
	}

	/** Not relevant to subclasses. */
	public synchronized final void abort() 
	{
		abort_ = true;
		servo.close();
		motor.close();		
    	socket.close();
		if (ioio_ != null) {
			ioio_.disconnect();
		}
	}
}