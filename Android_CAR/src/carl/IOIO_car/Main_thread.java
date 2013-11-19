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

import android.hardware.SensorManager;
import android.view.SurfaceView;

public class Main_thread extends Thread
{
	SurfaceView parent_context;
	SensorManager mSensorManager = null;
	Cam_thread the_cam;
	Sensors_thread the_sensors;	
	IOIO_Thread ioio_thread_;	
	String ip_address;
	Android_Activity the_app;
	
	public Main_thread(Android_Activity app, SurfaceView v, SensorManager m, String ip)
	{
		super();
		parent_context = v;		
		mSensorManager = m;
		ip_address = ip;
		the_app = app;
		the_cam = new Cam_thread(parent_context,ip_address);
		the_sensors = new Sensors_thread(mSensorManager,ip_address);
		ioio_thread_ = new IOIO_Thread(the_app, ip_address);
	}
	
    public void run() 
    {    			
		ioio_thread_.start();	
		the_cam.start_thread();
    }
    
    public void stop_simu()
    {
    	the_cam.stop_thread();
    	the_sensors.stop_thread();
		ioio_thread_.abort();
    }	
}
