package com.elcont.pt100dev;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
//import com.ftdi.javad2xxdemo.*;

public class ElcontCI485ProtFragment extends Fragment{

	public String fecha;
	int reg_max;
	String value_txt;

	Spinner sp_tempScale;
	String[] tempScale = {"Celsius","Fahrenheit","Kelvin","Reaumur"};
	ProgressBar pb_valueMeter_pos, pb_valueMeter_neg;
	int PosTempScale;
	TextView ValueMin, ValueMax;

	private Switch Audio_state;
	String temptring = "";
	float value_float_temp;
	int value_int_temp, value_int_temp_max, value_int_temp_min, i = 0;
	Boolean bMeasure, sms_state = false, bAudioState;
	SharedPreferences prefe, dataF, dataM;
	SharedPreferences.Editor editor, edit_datalog;
	ImageView ledSms1, ledSms2;
	int count_temp_max = 0, count_temp_max_sms = 0;
	String stringPhone1, stringPhone2, sText;
	public MediaPlayer uno, dos, tres, cuatro;
	Button btn_sendSMS;
    int time_register = 1, time_register_second = 1000;

	public  String dia[] =
			{       "dia[0]","dia[1]","dia[2]","dia[3]","dia[4]","dia[5]","dia[6]","dia[7]","dia[8]","dia[9]",
					"dia[10]","dia[11]","dia[12]","dia[13]","dia[14]","dia[15]","dia[16]","dia[17]","dia[18]","dia[19]",
					"dia[20]","dia[21]","dia[22]","dia[23]","dia[24]","dia[25]","dia[26]","dia[27]","dia[28]","dia[29]",
					"dia[30]","dia[31]","dia[32]","dia[33]","dia[34]","dia[35]","dia[36]","dia[37]","dia[38]","dia[39]",
					"dia[40]","dia[41]","dia[42]","dia[43]","dia[44]","dia[45]","dia[46]","dia[47]","dia[48]","dia[49]",
					"dia[50]","dia[51]","dia[52]","dia[53]","dia[54]","dia[55]","dia[56]","dia[57]","dia[58]","dia[59]",
					"dia[60]","dia[61]","dia[62]","dia[63]","dia[64]","dia[65]","dia[66]","dia[67]","dia[68]","dia[69]",
					"dia[70]","dia[71]","dia[72]","dia[73]","dia[74]","dia[75]","dia[76]","dia[77]","dia[78]","dia[79]",
					"dia[80]","dia[81]","dia[82]","dia[83]","dia[84]","dia[85]","dia[86]","dia[87]","dia[88]","dia[89]",
					"dia[90]","dia[91]","dia[92]","dia[93]","dia[94]","dia[95]","dia[96]","dia[97]","dia[98]","dia[99]",
					"dia[100]","dia[101]","dia[102]","dia[103]","dia[104]","dia[105]","dia[106]","dia[107]","dia[108]","dia[109]",
					"dia[110]","dia[111]","dia[112]","dia[113]","dia[114]","dia[115]","dia[116]","dia[117]","dia[118]","dia[119]",
					"dia[120]","dia[121]","dia[122]","dia[123]","dia[124]","dia[125]","dia[126]","dia[127]","dia[128]","dia[129]",
					"dia[130]","dia[131]","dia[132]","dia[133]","dia[134]","dia[135]","dia[136]","dia[137]","dia[138]","dia[139]",
					"dia[140]","dia[141]","dia[142]","dia[143]","dia[144]","dia[145]","dia[146]","dia[147]","dia[148]","dia[149]",
					"dia[150]","dia[151]","dia[152]","dia[153]","dia[154]","dia[155]","dia[156]","dia[157]","dia[158]","dia[159]",
					"dia[160]","dia[161]","dia[162]","dia[163]","dia[164]","dia[165]","dia[166]","dia[167]","dia[168]","dia[169]",
					"dia[170]","dia[171]","dia[172]","dia[173]","dia[174]","dia[175]","dia[176]","dia[177]","dia[178]","dia[179]",
					"dia[180]","dia[181]","dia[182]","dia[183]","dia[184]","dia[185]","dia[186]","dia[187]","dia[188]","dia[189]",
					"dia[190]","dia[191]","dia[192]","dia[193]","dia[194]","dia[195]","dia[196]","dia[197]","dia[198]","dia[199]",
					"dia[200]","dia[201]","dia[202]","dia[203]","dia[204]","dia[205]","dia[206]","dia[207]","dia[208]","dia[209]",
					"dia[210]","dia[211]","dia[212]","dia[213]","dia[214]","dia[215]","dia[216]","dia[217]","dia[218]","dia[219]",
					"dia[220]","dia[221]","dia[222]","dia[223]","dia[224]","dia[225]","dia[226]","dia[227]","dia[228]","dia[229]",
					"dia[230]","dia[231]","dia[232]","dia[233]","dia[234]","dia[235]","dia[236]","dia[237]","dia[238]","dia[239]",
					"dia[240]","dia[241]","dia[242]","dia[243]","dia[244]","dia[245]","dia[246]","dia[247]","dia[248]","dia[249]",
					"dia[250]","dia[251]","dia[252]","dia[253]","dia[254]","dia[255]","dia[256]","dia[257]","dia[258]","dia[259]",
					"dia[260]","dia[261]","dia[262]","dia[263]","dia[264]","dia[265]","dia[266]","dia[267]","dia[268]","dia[269]",
					"dia[270]","dia[271]","dia[272]","dia[273]","dia[274]","dia[275]","dia[276]","dia[277]","dia[278]","dia[279]",
					"dia[280]","dia[281]","dia[282]","dia[283]","dia[284]","dia[285]","dia[286]","dia[287]","dia[288]","dia[289]",
					"dia[290]","dia[291]","dia[292]","dia[293]","dia[294]","dia[295]","dia[296]","dia[297]","dia[298]","dia[299]",
					"dia[300]","dia[301]","dia[302]","dia[303]","dia[304]","dia[305]","dia[306]","dia[307]","dia[308]","dia[309]",
					"dia[310]","dia[311]","dia[312]","dia[313]","dia[314]","dia[315]","dia[316]","dia[317]","dia[318]","dia[319]",
					"dia[320]","dia[321]","dia[322]","dia[323]","dia[324]","dia[325]","dia[326]","dia[327]","dia[328]","dia[329]",
					"dia[330]","dia[331]","dia[332]","dia[333]","dia[334]","dia[335]","dia[336]","dia[337]","dia[338]","dia[339]",
					"dia[340]","dia[341]","dia[342]","dia[343]","dia[344]","dia[345]","dia[346]","dia[347]","dia[348]","dia[349]",
					"dia[350]","dia[351]","dia[352]","dia[353]","dia[354]","dia[355]","dia[356]","dia[357]","dia[358]","dia[359]",
					"dia[360]","dia[361]","dia[362]","dia[363]","dia[364]","dia[365]","dia[366]","dia[367]","dia[368]","dia[369]",
					"dia[370]","dia[371]","dia[372]","dia[373]","dia[374]","dia[375]","dia[376]","dia[377]","dia[378]","dia[379]",
					"dia[380]","dia[381]","dia[382]","dia[383]","dia[384]","dia[385]","dia[386]","dia[387]","dia[388]","dia[389]",
					"dia[390]","dia[391]","dia[392]","dia[393]","dia[394]","dia[395]","dia[396]","dia[397]","dia[398]","dia[399]",
					"dia[400]","dia[401]","dia[402]","dia[403]","dia[404]","dia[405]","dia[406]","dia[407]","dia[408]","dia[409]",
					"dia[410]","dia[411]","dia[412]","dia[413]","dia[414]","dia[415]","dia[416]","dia[417]","dia[418]","dia[419]",
					"dia[420]","dia[421]","dia[422]","dia[423]","dia[424]","dia[425]","dia[426]","dia[427]","dia[428]","dia[429]",
					"dia[430]","dia[431]","dia[432]","dia[433]","dia[434]","dia[435]","dia[436]","dia[437]","dia[438]","dia[439]",
					"dia[440]","dia[441]","dia[442]","dia[443]","dia[444]","dia[445]","dia[446]","dia[447]","dia[448]","dia[449]",
					"dia[450]","dia[451]","dia[452]","dia[453]","dia[454]","dia[455]","dia[456]","dia[457]","dia[458]","dia[459]",
					"dia[460]","dia[461]","dia[462]","dia[463]","dia[464]","dia[465]","dia[466]","dia[467]","dia[468]","dia[469]",
					"dia[470]","dia[471]","dia[472]","dia[473]","dia[474]","dia[475]","dia[476]","dia[477]","dia[478]","dia[479]",
					"dia[480]","dia[481]","dia[482]","dia[483]","dia[484]","dia[485]","dia[486]","dia[487]","dia[488]","dia[489]",
					"dia[490]","dia[491]","dia[492]","dia[493]","dia[494]","dia[495]","dia[496]","dia[497]","dia[498]","dia[499]",
					"dia[500]","dia[501]","dia[502]","dia[503]","dia[504]","dia[505]","dia[506]","dia[507]","dia[508]","dia[509]",
					"dia[510]","dia[511]","dia[512]","dia[513]","dia[514]","dia[515]","dia[516]","dia[517]","dia[518]","dia[519]",
					"dia[520]","dia[521]","dia[522]","dia[523]","dia[524]","dia[525]","dia[526]","dia[527]","dia[528]","dia[529]",
					"dia[530]","dia[531]","dia[532]","dia[533]","dia[534]","dia[535]","dia[536]","dia[537]","dia[538]","dia[539]",
					"dia[540]","dia[541]","dia[542]","dia[543]","dia[544]","dia[545]","dia[546]","dia[547]","dia[548]","dia[549]",
					"dia[550]","dia[551]","dia[552]","dia[553]","dia[554]","dia[555]","dia[556]","dia[557]","dia[558]","dia[559]",
					"dia[560]","dia[561]","dia[562]","dia[563]","dia[564]","dia[565]","dia[566]","dia[567]","dia[568]","dia[569]",
					"dia[570]","dia[571]","dia[572]","dia[573]","dia[574]","dia[575]","dia[576]","dia[577]","dia[578]","dia[579]",
					"dia[580]","dia[581]","dia[582]","dia[583]","dia[584]","dia[585]","dia[586]","dia[587]","dia[588]","dia[589]",
					"dia[590]","dia[591]","dia[592]","dia[593]","dia[594]","dia[595]","dia[596]","dia[597]","dia[598]","dia[599]"
			};

	TextView sensorType_label;
	TextView value_label;


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

	private TimerTask mTimerTaskData;
	private Timer timerData = new Timer();
	Handler timerHandlerData;
    
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

		reg_max = dia.length - 1;
		
		//date_label = (TextView)view.findViewById(R.id.ReadValues);

		sensorType_label = (TextView)view.findViewById(R.id.sensorType_label);
		value_label = (TextView)view.findViewById(R.id.value_label);

		pb_valueMeter_pos = (ProgressBar)view.findViewById(R.id.pb_ir_pos);
		pb_valueMeter_neg = (ProgressBar)view.findViewById(R.id.pb_ir_neg);
		sp_tempScale = (Spinner)view.findViewById(R.id.sp_tempScale);
		Audio_state = (Switch)view.findViewById(R.id.sw_audio);
		btn_sendSMS = (Button)view.findViewById(R.id.btn_sendSMS);
		ledSms1 = (ImageView) view.findViewById(R.id.ivSMS1);
		ledSms2 = (ImageView) view.findViewById(R.id.ivSMS2);
		ValueMax = (TextView)view.findViewById(R.id.txtValueMax);
		ValueMin = (TextView)view.findViewById(R.id.txtValueMin);

        TextView t = (TextView) view.findViewById(R.id.date_label);
        t.setText(new Date().toString());
// Get the Drawable custom_progressbar
		Drawable customDrawable= getResources().getDrawable(R.drawable.customprogressbar);//res.getDrawable(R.drawable.customprogressbar);
		// set the drawable as progress drawavle
		pb_valueMeter_pos.setProgressDrawable(customDrawable);

		Drawable customDrawable_neg= getResources().getDrawable(R.drawable.customprogressbar_neg);//res.getDrawable(R.drawable.customprogressbar);
		// set the drawable as progress drawavle
		pb_valueMeter_neg.setProgressDrawable(customDrawable_neg);
		pb_valueMeter_neg.setProgress(1);
		pb_valueMeter_pos.setProgress(0);

		prefe = getActivity().getSharedPreferences("Data", Context.MODE_PRIVATE);
		dataF = getActivity().getSharedPreferences("Fecha", Context.MODE_PRIVATE);
		dataM = getActivity().getSharedPreferences("Medida", Context.MODE_PRIVATE);
		bMeasure = prefe.getBoolean("bMeasure", true);
		bAudioState = prefe.getBoolean("bAudioState", true);
		i = prefe.getInt("i", 0);
        time_register = prefe.getInt("time_register", 1);
        time_register_second = time_register * 60000;

		stringPhone1 = prefe.getString("phone1", "");
		stringPhone2 = prefe.getString("phone2", "");
		sText = prefe.getString("txtSMS", "");
		count_temp_max_sms = prefe.getInt("count_temp_max_sms", 0);
		PosTempScale = prefe.getInt("PosTempScale", 0);
		tScale = PosTempScale + 1;

		uno = MediaPlayer.create(DeviceUARTContext, R.raw.uno);
		dos = MediaPlayer.create(DeviceUARTContext, R.raw.dos);
		tres = MediaPlayer.create(DeviceUARTContext, R.raw.tres);
		cuatro = MediaPlayer.create(DeviceUARTContext, R.raw.cuatro);

		uno.setLooping(true);

		dos.setLooping(true);
		tres.setLooping(true);
		cuatro.setLooping(true);


		uno.start();
		uno.pause();

		dos.start();
		dos.pause();
		tres.start();
		tres.pause();
		cuatro.start();
		cuatro.pause();

		leds_states();

		if(stringPhone1.equals("")&&stringPhone2.equals("")){
			sms_state = false;
		}else {
			sms_state = true;
		}
        //gettemp_button = (Button)view.findViewById(R.id.gettemp_button);


	    /* by default it is 19200 */
        baudRate = 19200;
//		baudRate = 9600;
        stopBit = 1;
        dataBit = 8;
        parity = 0;
        flowControl = 0;
        portNumber = 1;

        timerHandler=new Handler();
        onTimerTick();
        timer.schedule(mTimerTask, 10, 500);

        timerHandlerData=new Handler();
        onTimerTickData();
        timerData.schedule(mTimerTaskData, 1000, time_register_second);
//		value_label.setText("Open");

		sp_tempScale.setAdapter(new ArrayAdapter<String>(DeviceUARTContext, android.R.layout.simple_spinner_item, tempScale));
		sp_tempScale.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				TextView tempsymbol_label = (TextView) getView().findViewById(R.id.tempsymbol_label);

				editor = prefe.edit();
				editor.putInt("PosTempScale", position);
				editor.commit();

				switch (position) {
					case 0:
						tempsymbol_label.setText("ºC");
						pb_valueMeter_pos.setMax(250);
						pb_valueMeter_neg.setMax(35);
						ValueMax.setText("Temperature Maximun 250ºC");
						ValueMin.setText("Temperature Minimun -35ºC");
						tScale = 1;
						break;
					case 1:
						tempsymbol_label.setText("ºF");
						pb_valueMeter_pos.setMax(482);
						pb_valueMeter_neg.setMax(31);
						ValueMax.setText("Temperature Maximun 482ºF");
						ValueMin.setText("Temperature Minimun -31ºF");
						tScale = 2;
						break;
					case 2:
						tempsymbol_label.setText("ºK");
						pb_valueMeter_pos.setMax(523);
						pb_valueMeter_neg.setMax(40);
						ValueMax.setText("Temperature Maximun 523ºK");
						ValueMin.setText("Temperature Minimun 238ºK");
						tScale = 3;
						break;
					case 3:
						tempsymbol_label.setText("ºR");
						pb_valueMeter_pos.setMax(200);
						pb_valueMeter_neg.setMax(25);
						ValueMax.setText("Temperature Maximun 200ºR");
						ValueMin.setText("Temperature Minimun -25ºR");
						tScale = 4;
						break;
				}
				Toast.makeText(adapterView.getContext(), (String) adapterView.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// vacio

			}
		});
		sp_tempScale.setSelection(PosTempScale);

		switch (PosTempScale) {
			case 0:
				value_int_temp_max = prefe.getInt("Cval_temp_max", 0);
				value_int_temp_min = prefe.getInt("Cval_temp_min", 0);
				tScale = 1;
				break;
			case 1:
				value_int_temp_max = prefe.getInt("Fval_temp_max", 0);
				value_int_temp_min = prefe.getInt("Fval_temp_min", 0);
				tScale = 2;
				break;
			case 2:
				value_int_temp_max = prefe.getInt("Kval_temp_max", 0);
				value_int_temp_min = prefe.getInt("Kval_temp_min", 0);
				tScale = 3;
				break;
			case 3:
				value_int_temp_max = prefe.getInt("Rval_temp_max", 0);
				value_int_temp_min = prefe.getInt("Rval_temp_min", 0);
				tScale = 4;
				break;
		}

		if(bAudioState == true){
			Audio_state.setChecked(true);
		}else {
			Audio_state.setChecked(false);
		}

		Audio_state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked) {
					bAudioState = true;
					editor = prefe.edit();
					editor.putBoolean("bAudioState",true);
					editor.commit();
				} else {
					bAudioState = false;
					editor = prefe.edit();
					editor.putBoolean("bAudioState",false);
					editor.commit();
				}
			}
		});

		btn_sendSMS.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(count_temp_max_sms<2){
					SendSMS();
					if((!stringPhone1.equals(""))||(!stringPhone2.equals(""))) {
						count_temp_max_sms++;
						editor = prefe.edit();
						editor.putInt("count_temp_max_sms", count_temp_max_sms);
						editor.commit();
						leds_states();
					}
				}
			}
		});

		return view;
    }

	public void leds_states() {
		switch (count_temp_max_sms){
			case 0:
				ledSms1.setImageResource(R.drawable.ledgreen);
				ledSms2.setImageResource(R.drawable.ledgreen);
				break;
			case 1:
				ledSms1.setImageResource(R.drawable.ledred);
				break;
			case 2:
				ledSms1.setImageResource(R.drawable.ledred);
				ledSms2.setImageResource(R.drawable.ledred);
				break;
		}
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
		timerData.cancel();
		uno.pause();
		dos.pause();
		tres.pause();
		cuatro.pause();
		super.onStop();
	}

	@Override
	public void onPause()
	{
		uno.pause();
		dos.pause();
		tres.pause();
		cuatro.pause();
		super.onPause();
	}

	@Override
	public void onDestroy()
	{
		uno.stop();
		uno.release();
		dos.stop();
		dos.release();
		tres.stop();
		tres.release();
		cuatro.stop();
		cuatro.release();
		super.onDestroy();
	}


   
    
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

	public void onTimerTickData() {
		mTimerTaskData = new TimerTask() {
			//this method is called every 1ms
			public void run() {
				timerHandlerData.post(new Runnable() {
					public void run() {
						value_txt = value_label.getText().toString();
						if(!value_txt.equals("Open")){
							DataLog();
						}
					}
				});
			}};
	}

	public void DataLog(){
		fecha = fechaHoraActual();
		//				Toast.makeText(getActivity(),"Fecha: " + fecha + " i: " + i, Toast.LENGTH_SHORT).show();

		edit_datalog = dataF.edit();
		edit_datalog.putString(dia[i],fecha);
		edit_datalog.commit();

		edit_datalog = dataM.edit();
		edit_datalog.putString(dia[i], ""+value_float_temp);
		edit_datalog.commit();

		if(i<reg_max){
			i++;
		}else{
			i = 0;
		}
		editor = prefe.edit();
		editor.putInt("i", i);
		editor.commit();
	}

	public String fechaHoraActual(){
		return new SimpleDateFormat( "yyyy-MM-dd' 'HH:mm:ss", java.util.Locale.getDefault()).format(Calendar.getInstance().getTime());
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
	temptring = convertValue(val5, val6);
	value_float_temp = Float.parseFloat(temptring);
	value_int_temp = Math.round(value_float_temp);
	value_label.setText(convertValue(val5,val6));

		if(value_int_temp>=0){
			pb_valueMeter_pos.setProgress(value_int_temp);
			if(tScale<=3){
				pb_valueMeter_neg.setProgress(40);
			}
			else {
				pb_valueMeter_neg.setProgress(32);
			}
		}
		else {
			pb_valueMeter_pos.setProgress(0);
			if(tScale<=3){
				pb_valueMeter_neg.setProgress(40+value_int_temp);
			}
			else {
				pb_valueMeter_neg.setProgress(32+value_int_temp);
			}
		}
		if(value_int_temp <value_int_temp_min){
			if(bAudioState == true){
				uno.start();
			}else {
				uno.pause();
			}
			dos.pause();
			tres.pause();
			cuatro.pause();
		}
		else if((value_int_temp >value_int_temp_max)){
			if(bAudioState == true){
				cuatro.start();
			}else {
				cuatro.pause();
			}
			dos.pause();
			tres.pause();
			uno.pause();
		}
		else {
			uno.pause();
			dos.pause();
			tres.pause();
			cuatro.pause();
		}

		if ((value_int_temp_min>0)&&(value_int_temp_max>0)){
			comparison();
		}
	}
}

	private void comparison() {
//		if((value_int_gas >= value_int_gas_co_max)||(value_int_gas >= value_int_gas_ch4_max)){
		if((value_int_temp >= value_int_temp_max)||(value_int_temp < value_int_temp_min)){
			count_temp_max++;
			if(count_temp_max > 60){
				count_temp_max = 0;
				count_temp_max_sms++;
				if(count_temp_max_sms<=2){
					if(sms_state == true){
						SendSMS();
//							Toast.makeText(getActivity(),"Warning! Temperature is: " + value_float_temp + " Hr is: " + value_float_hr, Toast.LENGTH_LONG).show();
					}
					editor = prefe.edit();
					editor.putInt("count_temp_max_sms", count_temp_max_sms);
					editor.commit();
					switch (count_temp_max_sms){
						case 0:
							ledSms1.setImageResource(R.drawable.ledgreen);
							ledSms2.setImageResource(R.drawable.ledgreen);
							break;
						case 1:
							ledSms1.setImageResource(R.drawable.ledred);
							break;
						case 2:
							ledSms2.setImageResource(R.drawable.ledred);
							break;
					}
				}
			}
		}
		else {
			count_temp_max = 0;
			//count_temp_max_sms = 0;
		}
	}

	public void SendSMS() {
		SmsManager manager=SmsManager.getDefault();
		if(!stringPhone1.equals("")){
//			manager.sendTextMessage(stringPhone1, null, "Warning! Temperature is: " + value_float_temp + "and Hr is: " + value_float_hr, null, null);
			manager.sendTextMessage(stringPhone1, null, sText + "\nTemp is: " + value_int_temp, null, null);
			Toast.makeText(getActivity(),"Send SMS to " + stringPhone1, Toast.LENGTH_SHORT).show();
		}
		if(!stringPhone2.equals("")){
//			manager.sendTextMessage(stringPhone2, null, "Warning! Temperature is: " + value_float_temp + "and Hr is: " + value_float_hr, null, null);
			manager.sendTextMessage(stringPhone2, null, sText + "\nTemp is: " + value_int_temp, null, null);
			Toast.makeText(getActivity(),"Send SMS to " + stringPhone2, Toast.LENGTH_SHORT).show();
		}
		//Toast.makeText(getActivity(),"Warning! the temperature value is greater than " + value_int_temp_max, Toast.LENGTH_LONG).show();
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

