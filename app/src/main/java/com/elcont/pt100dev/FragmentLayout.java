package com.elcont.pt100dev;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;

import android.view.View;

import com.ftdi.j2xx.D2xxManager;



public class FragmentLayout extends Activity {
	public static D2xxManager ftD2xx = null;
	public static int currect_index = 0;
	public static int old_index = -1;
	
	private static Fragment currentFragment = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	try {
    		ftD2xx = D2xxManager.getInstance(this);
    	} catch (D2xxManager.D2xxException ex) {
    		ex.printStackTrace();
    	}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_layout);
        SetupD2xxLibrary();
        
		IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.setPriority(500);
        this.registerReceiver(mUsbReceiver, filter);   
    }

    @Override
	protected void onDestroy() {
    	this.unregisterReceiver(mUsbReceiver);
    	super.onDestroy();
	}


    private void SetupD2xxLibrary () {
    	/*
        PackageManager pm = getPackageManager();

        for (ApplicationInfo app : pm.getInstalledApplications(0)) {
          Log.d("PackageList", "package: " + app.packageName + ", sourceDir: " + app.nativeLibraryDir);
          if (app.packageName.equals(R.string.app_name)) {
        	  System.load(app.nativeLibraryDir + "/libj2xx-utils.so");
        	  Log.i("ftd2xx-java","Get PATH of FTDI JIN Library");
        	  break;
          }
        }
        */
    	// Specify a non-default VID and PID combination to match if required

    	if(!ftD2xx.setVIDPID(0x0403, 0xada1))
    		Log.i("ftd2xx-java","setVIDPID Error");

    }

    /**
     * This is the "top-level" fragment, showing a list of items that the
     * user can pick.  Upon picking an item, it takes care of displaying the
     * data to the user as appropriate based on the current UI layout.
     */

    public static class TitlesFragment extends ListFragment {
        boolean mDualPane;
        int mCurCheckPosition = 0;
       //  int mDualPaneIndex = -1;
        // public static D2xx ftD2xx;
        // Context TitlesFragmentContext = this.;
        Map<Integer, Fragment> map = new HashMap<Integer, Fragment>();

    	// public void setTitlesFragment(Context parentContext)
    	// {
    	//	TitlesFragmentContext = parentContext;
    		// ftD2xx = ftdid2xx;
    	// }

        public TitlesFragment() {

        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Populate list with our static array of titles.
            //setListAdapter(new ArrayAdapter<String>(getActivity(),
           //         android.R.layout.simple_list_item_activated_1, FtdiModeListInfo.TITLES));

            // Check to see if we have a frame in which to embed the details
            // fragment directly in the containing UI.
            View detailsFrame = getActivity().findViewById(R.id.details);

            mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

            if (savedInstanceState != null) {
                // Restore last state for checked position.
                mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
            }
            
            Fragment f = new ElcontCI485ProtFragment(getActivity() , ftD2xx);
            currentFragment = f;
            
        
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.details, f);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
           
            old_index = currect_index;

            if (mDualPane) {
                // In dual-pane mode, the list view highlights the selected item.
                //getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                // Make sure our UI is in the correct state.
            	
                //showDetails(mCurCheckPosition);
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("curChoice", mCurCheckPosition);
        }
/*
        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            showDetails(position);
        }*/

        /**
         * Helper function to show the details of a selected item, either by
         * displaying a fragment in-place in the current UI, or starting a
         * whole new activity in which it is displayed.
         */
        void showDetails(int index) {
            mCurCheckPosition = index;
            currect_index = index;
            if (mDualPane) {
            	
            	Fragment f = new ElcontCI485ProtFragment(getActivity() , ftD2xx);
                currentFragment = f;
                
            
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.details, f);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
               
                old_index = currect_index;
            }
            else
            {
                // Otherwise we need to launch a new activity to display
                // the dialog fragment with selected text.
                Intent intent = new Intent();
                intent.setClass(getActivity(), ElcontCI485ProtFragment.class);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        }
    }

    /**
     * This is the secondary fragment, displaying the details of a particular
     * item.
     */
//    public static class DetailsFragment extends Fragment {
//    	/*
//        public static DetailsFragment newInstance(int index) {
//            DetailsFragment f = new DetailsFragment();
//            // Supply index input as an argument.
//            Bundle args = new Bundle();
//            args.putInt("index", index);
//            f.setArguments(args);
//
//            return f;
//        }
//		*/
//    	public DetailsFragment() {
//
//    	}
//
//        public int getShownIndex() {
//            return getArguments().getInt("index", -1);
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                Bundle savedInstanceState) {
//            if (container == null) {
//                // We have different layouts, and in one of them this
//                // fragment's containing frame doesn't exist.  The fragment
//                // may still be created from its saved state, but there is
//                // no reason to try to create its view hierarchy because it
//                // won't be displayed.  Note this is not needed -- we could
//                // just run the code below, where we would create and return
//                // the view hierarchy; it would just never be used.
//                return null;
//            }
//
//            ScrollView scroller = new ScrollView(getActivity());
//            TextView text = new TextView(getActivity());
//            int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//                    4, getActivity().getResources().getDisplayMetrics());
//            text.setPadding(padding, padding, padding, padding);
//            scroller.addView(text);
//            //text.setText(FtdiModeListInfo.DIALOGUE[getShownIndex()]);
//            
//            
//            return scroller;
//        }
//    }

	@Override
	protected void onNewIntent(Intent intent)
	{
		//String action = intent.getAction();
	}
    
	/***********USB broadcast receiver*******************************************/
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			String TAG = "FragL";			
			String action = intent.getAction();
			if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action))
			{
				Log.i(TAG,"DETACHED...");
				
	            if (currentFragment != null)
	            {
	            	switch (currect_index) 
	            	{

	        		case 5:
	        		//	((DeviceUARTFragment)currentFragment).notifyUSBDeviceDetach();
	        			break;
	            	default:
	            		//((DeviceInformationFragment)currentFragment).onStart();
	            		break;
	            	}
	            }         	
			}
		}	
	};
}
