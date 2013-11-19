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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.util.Log;

/* code based on: 
 * IBMEyes.java
 * sample code for IBM Developerworks Article
 * Author: W. Frank Ableson
 * fableson@msiservices.com 
 */

@SuppressWarnings("deprecation")
public class Sensors_thread implements SensorListener 
{
    final String tag = "Sensors";
    Android_Activity parent_context;	
	InetAddress serverAddr;
	DatagramSocket socket;	
	float x_O, y_O, z_O, x_A, y_A, z_A;
	short ix_O, iy_O, iz_O, ix_A, iy_A, iz_A;
	int size_p;
	
	SensorManager mSensorManager = null;	
	String ip_address;

    public Sensors_thread(SensorManager sm, String ip)
	{
    	super();
    	mSensorManager = sm;    	
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
    
    public void stop_thread()
    {
    	socket.close();
    }
    
	private void send_data_UDP() 
	{
		try 
		{			
        	ix_O = (short) (x_O);
        	iy_O = (short) (y_O);
        	iz_O = (short) (z_O);
        	ix_A = (short) (x_A);
        	iy_A = (short) (y_A);
        	iz_A = (short) (z_A);        	
			
			byte[] data = new byte[12];
			data[0] = (byte) (ix_O >> 8);
			data[1] = (byte) ix_O;    			
			data[2] = (byte) (iy_O >> 8);
			data[3] = (byte) iy_O;    			
			data[4] = (byte) (iz_O >> 8);
			data[5] = (byte) iz_O;    			
			data[6] = (byte) (ix_A >> 8);
			data[7] = (byte) ix_A;    			
			data[8] = (byte) (iy_A >> 8);
			data[9] = (byte) iy_A;    			
			data[10] = (byte) (iz_A >> 8);
			data[11] = (byte) iz_A;
			
			try 
			{			
				size_p = data.length;
				DatagramPacket packet = new DatagramPacket(data, size_p, serverAddr, 9001);
				socket.send(packet);
			} catch (Exception e) 
			{	
				Log.e(tag, "Error: ", e);
			}	
		}
		catch (Exception exception) 
		{
			Log.e(tag, "Error: ", exception);
		}
	}

	@Override
	public void onAccuracyChanged(int sensor, int accuracy) {}

	@Override
	public void onSensorChanged(int sensor, float[] values) 
	{
		// TODO Auto-generated method stub
        synchronized (this) 
        {
        	if (sensor == SensorManager.SENSOR_ORIENTATION) {
        		x_O = values[0] *100;
        		y_O = values[1] *100;
        		z_O = values[2] *100;
        	}
        	if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
        		x_A = values[0] *100;
        		y_A = values[1] *100;
        		z_A = values[2] *100;
        	} 
        }	
    	send_data_UDP();
	}
}

