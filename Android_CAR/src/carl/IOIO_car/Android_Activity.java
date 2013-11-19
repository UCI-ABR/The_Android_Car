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

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

// ----------------------------------------------------------------------

public class Android_Activity extends Activity 
{   	
	Main_thread simulator;
	ToggleButton togglebutton;
	EditText ip_text;
    SensorManager sm = null;
    SurfaceView view;
    Sensors_thread the_sensors=null;
    String IP_address;
	Android_Activity the_app;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		view = new SurfaceView(this);		
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		ip_text = (EditText) findViewById(R.id.IP_edit_txt);
		togglebutton = (ToggleButton) findViewById(R.id.CameraButton);
		togglebutton.setOnClickListener(new btn_listener());		
		the_app = this;
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
	}

	protected void onStop()
	{
		super.onStop();
		simulator.stop_simu();
		this.finish();
	}
	
	private class btn_listener implements OnClickListener 
	{
	    public void onClick(View v) 
	    {		
	        // Perform action on clicks
	        if (togglebutton.isChecked()) 
	        {
	    		IP_address = ip_text.getText().toString(); 
	    		
	            simulator = new Main_thread(the_app, view, sm, IP_address);
	            the_sensors = simulator.the_sensors;	
	            sm.registerListener(the_sensors, 
	                    SensorManager.SENSOR_ORIENTATION |SensorManager.SENSOR_ACCELEROMETER,
	                    SensorManager.SENSOR_DELAY_UI);
	                        
	            simulator.start();
	        	
	            Toast.makeText(Android_Activity.this, "Start streaming", Toast.LENGTH_SHORT).show();
	        } else 
	        {
	        	simulator.stop_simu();
	            sm.unregisterListener(the_sensors);
	            Toast.makeText(Android_Activity.this, "Stop streaming", Toast.LENGTH_SHORT).show();
	        }
	    }
	}
}

