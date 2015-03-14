package com.elcont.pt100dev;

import java.util.ArrayList;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Fragment;
import android.content.Context;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.Button;

import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
//import com.ftdi.javad2xxdemo.*;



public class ElcontCI485ProtFragment extends Fragment{
	
	
	TextView date_label;
	TextView tempsymbol_label;
	TextView sensorType_label;
	TextView value_label;
	TextView milivoltString_label;
	TextView milivoltValue_label;
	TextView tcRef_label;
	TextView channelString_label;
	TextView minValueString_label;
	TextView minValue_label;
	TextView valueString_label;
	TextView valueSm_label;
	TextView maxValueString_label;
	TextView maxValue_label;
	TextView companyStringName_label;
	TextView devModel_label;
	TextView minScale_label;
	TextView maxScale_label;
	TextView website_label;
	
	EditText channelnumber;
	
	RadioGroup tempscale;
	
	RadioButton celsiusradio;
	RadioButton farenradio;
	RadioButton kelvinradio;
	RadioButton reaumurradio;
	
	Button gettemp_button;
	
	
	static Context DeviceUARTContext;
	D2xxManager ftdid2xx;
	FT_Device ftDev = null;
	int DevCount = -1;
    int currentIndex = -1;
    int openIndex = 0;
    int tScale = 1; //Escala por defecto centígrados
    static int iEnableReadFlag = 1;
    
    /*local variables*/
    int baudRate; /*baud rate*/
    byte stopBit; /*1:1stop bits, 2:2 stop bits*/
    byte dataBit; /*8:8bit, 7: 7bit*/
    byte parity;  /* 0: none, 1: odd, 2: even, 3: mark, 4: space*/
    byte flowControl; /*0:none, 1: flow control(CTS,RTS)*/
    int portNumber; /*port number*/
    ArrayList<CharSequence> portNumberList;


    public static final int readLength = 512;
    public static final int bufferLength = 5;
    public int readcount = 0;
    public int iavailable = 0;
    byte[] readData;
    char[] readDataToText;
    float[] databuffer;
    
    public boolean bReadThreadGoing = false;
    public readThread read_thread;

    boolean uart_configured = false;
    
    boolean timer_started = false;
    
   // View view;
    

    //Timer Declaration
    private TimerTask mTimerTask;
    private Timer timer = new Timer();  
    Handler timerHandler;
    
// Empty Constructor
	public ElcontCI485ProtFragment()
	{
	}

	/* Constructor */
	public ElcontCI485ProtFragment(Context parentContext , D2xxManager ftdid2xxContext)
	{
		DeviceUARTContext = parentContext;
		ftdid2xx = ftdid2xxContext;
	}

	public int getShownIndex() {
        return getArguments().getInt("index", 9);
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
    		Bundle savedInstanceState) {    
    	if (container == null) {
            return null;
        }		

		super.onCreate(savedInstanceState);

		View view = inflater.inflate(R.layout.elcont_ci_485_protocol, container, false);
		 
		readData = new byte[readLength];
		readDataToText = new char[readLength];
		databuffer = new float[bufferLength];
		
		
		//date_label = (TextView)view.findViewById(R.id.ReadValues);
			
		
		sensorType_label = (TextView)view.findViewById(R.id.sensorType_label);
		value_label = (TextView)view.findViewById(R.id.value_label);
		milivoltString_label = (TextView)view.findViewById(R.id.milivoltString_label);
		milivoltValue_label = (TextView)view.findViewById(R.id.milivoltValue_label);
		tcRef_label = (TextView)view.findViewById(R.id.tcRef_label);
		//channelString_label = (TextView)view.findViewById(R.id.channelString_label);
		minValueString_label = (TextView)view.findViewById(R.id.minValueString_label);
		minValue_label = (TextView)view.findViewById(R.id.minValue_label);
		valueString_label = (TextView)view.findViewById(R.id.valueString_label);
		valueSm_label = (TextView)view.findViewById(R.id.valueSm_label);
		maxValueString_label = (TextView)view.findViewById(R.id.maxValueString_label);
		maxValue_label = (TextView)view.findViewById(R.id.maxValue_label);
		companyStringName_label = (TextView)view.findViewById(R.id.companyStringName_label);
		devModel_label = (TextView)view.findViewById(R.id.devModel_label);
		minScale_label = (TextView)view.findViewById(R.id.minScale_label);
		maxScale_label = (TextView)view.findViewById(R.id.maxScale_label);
		website_label = (TextView)view.findViewById(R.id.website_label);
		
		//channelnumber = (EditText)view.findViewById(R.id.channelnumber);
		
		tempscale = (RadioGroup)view.findViewById(R.id.tempscale);
		
		celsiusradio = (RadioButton)view.findViewById(R.id.celsiusradio);
		farenradio = (RadioButton)view.findViewById(R.id.farenradio);
		kelvinradio = (RadioButton)view.findViewById(R.id.kelvinradio);
		reaumurradio = (RadioButton)view.findViewById(R.id.reaumurradio);
		
		//gettemp_button = (Button)view.findViewById(R.id.gettemp_button);
		
		celsiusradio.setOnClickListener(next_Listener);  
		farenradio.setOnClickListener(next_Listener);  
		kelvinradio.setOnClickListener(next_Listener);  
		reaumurradio.setOnClickListener(next_Listener);  
		
		TextView t = (TextView) view.findViewById(R.id.date_label);
	    t.setText(new Date().toString());
	    
	    /* by default it is 19200 */
		baudRate = 19200;
		
		/* default is stop bit 1 */
		stopBit = 1;
		
		/* default data bit is 8 bit */
		dataBit = 8;
		
		/* default is none */
		parity = 0;
		
		/* default flow control is is none */
		flowControl = 0;
		
		portNumber = 1; 
		
		timerHandler=new Handler();
		
		onTimerTick();
        timer.schedule(mTimerTask, 10, 500);
	   
		
		
		
	    
		return view;
    }
    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

    @Override
	public void onStart() {
    	super.onStart();
    	createDeviceList();
    }

	@Override
	public void onStop()
	{
		disconnectFunction();
		timer.cancel();
		super.onStop();
	}
  
    private OnClickListener next_Listener = new OnClickListener() {
        public void onClick(View v) {

        //xml find out which radio button has been checked ...
        //RadioGroup tempscale=(RadioGroup)getActivity().findViewById(R.id.tempscale); //change or leave out this line (I've put it in because you might find it useful later )
        
        	
        	
        RadioButton celsiusradio=(RadioButton)getActivity().findViewById(R.id.celsiusradio);  //you dont need to do this again if global
        if(celsiusradio.isChecked() == true) {
        	setScaleLabel(1);
        	       	
       }
        else if(farenradio.isChecked() == true) {
        	setScaleLabel(2);
        	
        }
        else if(kelvinradio.isChecked() == true) {
        	setScaleLabel(3);
        }
        else if(reaumurradio.isChecked() == true) {
        	setScaleLabel(4);
        }
        }
		
    };
   
    
    //timer tick on every 1 s 
    public void onTimerTick() {
        mTimerTask = new TimerTask() {
            //this method is called every 1ms
            public void run() {                 
                 timerHandler.post(new Runnable() {
                     public void run() {  
                        //update textView
                        //ERROR:textView2 cannot be resolved
                    	 if(DevCount <= 0)
         				{
         					//createDeviceList();
         				}
         				else
         				{
         					//connectFunction();
         				}
         				

         				if(DevCount <= 0 || ftDev == null)
         		    	{
         		    	//	Toast.makeText(DeviceUARTContext, "Device not open yet...", Toast.LENGTH_SHORT).show();		    	
         		    	}
         				else if( uart_configured == false)
         		    	{
         		    		Toast.makeText(DeviceUARTContext, "UART not configure yet...", Toast.LENGTH_SHORT).show();
         		    		return;
         		    	}
         		    	else
         				{
         					SendMessage();
         				}
                        //Log.d("tag", "Hello from timer fragment");
                     }
                 });                    
            }};      
    } 
    

 
			
public void setScaleLabel(int scale){
		
	CharSequence text = "Escala cambiada = " + scale;
	int duration = Toast.LENGTH_SHORT;
	Toast toast = Toast.makeText(DeviceUARTContext, text, duration);
	//toast.show();
	System.out.println("Hay alguien?");
	
	try {
		TextView tempsymbol_label = (TextView)getView().findViewById(R.id.tempsymbol_label);
		switch(scale){
		case 1:
			tempsymbol_label.setText("ºC");
			tScale = 1;
			break;
		case 2:
			tempsymbol_label.setText("ºF");
			tScale = 2;
			break;
		case 3:
			tempsymbol_label.setText("ºK");
			tScale = 3;
			break;
		case 4:
			tempsymbol_label.setText("ºR");
			tScale = 4;
			break;
		}
		
		
		//System.out.println(textView);
		//textView.setText(Integer.toString(scale));
		toast = Toast.makeText(DeviceUARTContext, "Nueva escala", duration);
		toast.show();
	} catch (Exception e) {
		
		toast = Toast.makeText(DeviceUARTContext,"error", duration);
		toast.show();
		e.printStackTrace();
		
	}
	
	//tempsymbol_label.setText("C");
	//tempsymbol_label.setText("f");
	//tempsymbol_label.setText("k");
	//tempsymbol_label.setText("R");
}
 
public void notifyUSBDeviceAttach()
{
	createDeviceList();
}

public void notifyUSBDeviceDetach()
{
	disconnectFunction();
}	

public void createDeviceList()
{
	int tempDevCount = ftdid2xx.createDeviceInfoList(DeviceUARTContext);
	
	if (tempDevCount > 0)
	{
		if( DevCount != tempDevCount )
		{
			DevCount = tempDevCount;
			
		}
	}
	else
	{
		DevCount = -1;
		currentIndex = -1;
	}
}

public void disconnectFunction()
{
	DevCount = -1;
	currentIndex = -1;
	bReadThreadGoing = false;
	try {
		Thread.sleep(50);
	}
	catch (InterruptedException e) {
		e.printStackTrace();
	}
	
	if(ftDev != null)
	{
		synchronized(ftDev)
		{
			if( true == ftDev.isOpen())
			{
				ftDev.close();
			}
		}
	}
}

public void connectFunction()
{
	int tmpProtNumber = openIndex + 1;

	if( currentIndex != openIndex )
	{
		if(null == ftDev)
		{
			ftDev = ftdid2xx.openByIndex(DeviceUARTContext, openIndex);
		}
		else
		{
			synchronized(ftDev)
			{
				ftDev = ftdid2xx.openByIndex(DeviceUARTContext, openIndex);
			}
		}
		uart_configured = false;
	}
	else
	{
		Toast.makeText(DeviceUARTContext,"Device port " + tmpProtNumber + " is already opened",Toast.LENGTH_LONG).show();
		return;
	}

	if(ftDev == null)
	{
		Toast.makeText(DeviceUARTContext,"open device port("+tmpProtNumber+") NG, ftDev == null", Toast.LENGTH_LONG).show();
		return;
	}
		
	if (true == ftDev.isOpen())
	{
		currentIndex = openIndex;
		Toast.makeText(DeviceUARTContext, "open device port(" + tmpProtNumber + ") OK", Toast.LENGTH_SHORT).show();
			
		if(false == bReadThreadGoing)
		{
			read_thread = new readThread(handler);
			read_thread.start();
			bReadThreadGoing = true;
		}
	}
	else 
	{			
		Toast.makeText(DeviceUARTContext, "open device port(" + tmpProtNumber + ") NG", Toast.LENGTH_LONG).show();
		//Toast.makeText(DeviceUARTContext, "Need to get permission!", Toast.LENGTH_SHORT).show();			
	}
}

public void SetConfig(int baud, byte dataBits, byte stopBits, byte parity, byte flowControl)
{
	if (ftDev.isOpen() == false) {
		Log.e("j2xx", "SetConfig: device not open");
		return;
	}

	// configure our port
	// reset to UART mode for 232 devices
	ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);

	ftDev.setBaudRate(baud);

	switch (dataBits) {
	case 7:
		dataBits = D2xxManager.FT_DATA_BITS_7;
		break;
	case 8:
		dataBits = D2xxManager.FT_DATA_BITS_8;
		break;
	default:
		dataBits = D2xxManager.FT_DATA_BITS_8;
		break;
	}

	switch (stopBits) {
	case 1:
		stopBits = D2xxManager.FT_STOP_BITS_1;
		break;
	case 2:
		stopBits = D2xxManager.FT_STOP_BITS_2;
		break;
	default:
		stopBits = D2xxManager.FT_STOP_BITS_1;
		break;
	}

	switch (parity) {
	case 0:
		parity = D2xxManager.FT_PARITY_NONE;
		break;
	case 1:
		parity = D2xxManager.FT_PARITY_ODD;
		break;
	case 2:
		parity = D2xxManager.FT_PARITY_EVEN;
		break;
	case 3:
		parity = D2xxManager.FT_PARITY_MARK;
		break;
	case 4:
		parity = D2xxManager.FT_PARITY_SPACE;
		break;
	default:
		parity = D2xxManager.FT_PARITY_NONE;
		break;
	}

	ftDev.setDataCharacteristics(dataBits, stopBits, parity);

	short flowCtrlSetting;
	switch (flowControl) {
	case 0:
		flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
		break;
	case 1:
		flowCtrlSetting = D2xxManager.FT_FLOW_RTS_CTS;
		break;
	case 2:
		flowCtrlSetting = D2xxManager.FT_FLOW_DTR_DSR;
		break;
	case 3:
		flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
		break;
	default:
		flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
		break;
	}

	// TODO : flow ctrl: XOFF/XOM
	// TODO : flow ctrl: XOFF/XOM
	ftDev.setFlowControl(flowCtrlSetting, (byte) 0x0b, (byte) 0x0d);

	uart_configured = true;
	Toast.makeText(DeviceUARTContext, "Config done", Toast.LENGTH_SHORT).show();
}

public void EnableRead (){    	
	iEnableReadFlag = (iEnableReadFlag + 1)%2;
	    	
	if(iEnableReadFlag == 1) {
		ftDev.purge((byte) (D2xxManager.FT_PURGE_TX));
		ftDev.restartInTask();
		//readEnButton.setText("Read Enabled");
	}
	else{
		ftDev.stopInTask();
		//readEnButton.setText("Read Disabled");
	}
}

public void SendMessage() {
	if (ftDev.isOpen() == false) {
		Log.e("j2xx", "SendMessage: device not open");
		return;
	}

	ftDev.setLatencyTimer((byte) 16);
//	ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));

	ci485ProtWrite();
	
}

final Handler handler =  new Handler()

{
	@Override
	public void handleMessage(Message msg)
	{
		if(iavailable > 0)
		{
			ci485ProtRead();
			//value_label.append(String.copyValueOf(readDataToText, 0, iavailable));
		}
	}
};


private class readThread  extends Thread
{
	Handler mHandler;

	readThread(Handler h){
		mHandler = h;
		this.setPriority(Thread.MIN_PRIORITY);
	}

	@Override
	public void run()
	{
		int i;

		while(true == bReadThreadGoing)
		{
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}

			synchronized(ftDev)
			{
				iavailable = ftDev.getQueueStatus();				
				if (iavailable > 0) {
					
					if(iavailable > readLength){
						iavailable = readLength;
					}
					
					ftDev.read(readData, iavailable);
					for (i = 0; i < iavailable; i++) {
						readDataToText[i] = (char) readData[i];
					}
					Message msg = mHandler.obtainMessage();
					mHandler.sendMessage(msg);
				}
			}
		}
	}

}

public void ci485ProtWrite(){
	
	
	try {
		byte[] OutData = {5,1,0};
		ftDev.write(OutData, 2);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		disconnectFunction();
		timer.purge();
		
		e.printStackTrace();
	}
}

public void ci485ProtRead(){
	
	
	
	
	int i;
	int pointer = 0;
	
	for (i=1;i<10;i++){
		if(readData[i] == -54 &&readData[i+1] == -54){
			pointer = i-2;
		}
	}
	
	if (pointer>0){
	int val5 = readData[pointer-1];
	int val6 = readData[pointer];
	
	value_label.setText(convertValue(val5,val6));
	}
	
	
	
}

public String convertValue(int hbyte, int lbyte)
{
	//long v5 = hbyte & 0xffffffffl;
	//long v6 = lbyte & 0xffffffffl;
	//If estado(chnldly) > 32567 Then
    //estado(chnldly) = estado(chnldly) - 65536
	
	if (lbyte<0){
		lbyte = 256+lbyte;
	}
	
	//return (hbyte+"::"+lbyte);
	return ViewTempByScale(hbyte*256+lbyte);
}

public String ViewTempByScale(float rvalue){
	
		String svalue;

		if (rvalue < 2560 && rvalue > -200) {
			rvalue = getFilteredValue(rvalue);
			switch (tScale) {
			case 1:
				// svalue = Float.toString(rvalue);
				break;
			case 2:
				rvalue = ((float) 9 / 5) * rvalue + 32;
				break;
			case 3:
				rvalue = rvalue + 273;
				break;
			case 4:
				rvalue = rvalue * ((float) 4 / 5);
				break;
			}
			rvalue = (int)(rvalue * 10);

			svalue = Float.toString(rvalue / 10);
		}

		else {
			svalue = value_label.getText().toString();
		}
		return svalue;
}

public float getFilteredValue(float rvalue){
	
	int i = 0;
 	
	for (i = 0;i<bufferLength-1;i++){
			databuffer[i]=databuffer[i+1];
	}
	
	System.out.println(databuffer[0]+":"+databuffer[1]+":"+databuffer[2]+":"+databuffer[3]+":"+databuffer[4]);
	
	if(rvalue==458.0){
		databuffer[i]=databuffer[i-1];
	}
	else{
		databuffer[i]=rvalue;
	}
	
	
	System.out.println("Salida");
	System.out.println(databuffer[2]);
	System.out.println(databuffer[2]/10);
	System.out.println("*****");
	return databuffer[2]/10;
}


/**
 * Hot plug for plug in solution
 * This is workaround before android 4.2 . Because BroadcastReceiver can not
 * receive ACTION_USB_DEVICE_ATTACHED broadcast
 */

@Override
public void onResume() {
    super.onResume();
	DevCount = 0;
	createDeviceList();
	if(DevCount > 0)
	{
		connectFunction();
		SetConfig(baudRate, dataBit, stopBit, parity, flowControl);
	}	    
} 





}

