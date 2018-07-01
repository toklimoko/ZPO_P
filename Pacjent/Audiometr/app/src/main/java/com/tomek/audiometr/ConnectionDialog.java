package com.tomek.audiometr;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.UUID;


public class ConnectionDialog extends Dialog {


        private static final String TAG = "ConnectionDialog";

        private BluetoothAdapter mBluetoothAdapter;
        private Context mContext;

        private BluetoothChatService mBluetoothConnection;

        private static final UUID MY_UUID_SECURE =
                UUID.fromString("5580181b-4330-427d-b7d7-6f2aabb10550");

        private BluetoothDevice mBTDevice;

        private ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
        private DeviceListAdapter mDeviceLstAdapter;
        private ListView lvNewDevices;

        public ConnectionDialog(Context context, final ActivityCallback callback){
            super(context);
            setContentView(R.layout.activity_connection_dialog);
            mContext = context;
            setTitle("Connection Menu");
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            mContext.registerReceiver(mBroadcastReceiver, discoverDevicesIntent);
            Button btnONOFF = (Button) findViewById(R.id.btnONOFF);

            btnONOFF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mBluetoothAdapter == null){
                        Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
                    }
                    if(!mBluetoothAdapter.isEnabled()){
                        Log.d(TAG, "enableDisableBT: enabling BT.");
                        Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        mContext.startActivity(enableBTIntent);
                    }
                    if(mBluetoothAdapter.isEnabled()){
                        Log.d(TAG, "enableDisableBT: disabling BT.");
                        mBluetoothAdapter.disable();
                    }
                }
            });

            Button btnEnableDiscoverable = (Button) findViewById(R.id.btnDiscoverble_on_off);

            btnEnableDiscoverable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Log.d

                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    mContext.startActivity(discoverableIntent);

                    mBluetoothConnection = new BluetoothChatService(mContext, callback);
                }
            });

            Button btnDiscover = (Button) findViewById(R.id.btnFindUnpairedDevices);

            btnDiscover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Log.d
                    if(mDeviceLstAdapter != null){
                        mDeviceLstAdapter.clear();
                    }
                    if(mBluetoothAdapter.isDiscovering()){
                        mBluetoothAdapter.cancelDiscovery();
                        //Log.d

                        checkBTPermissions();

                        mBluetoothAdapter.startDiscovery();
                    }
                    if(!mBluetoothAdapter.isDiscovering()){
                        checkBTPermissions();

                        mBluetoothAdapter.startDiscovery();

                        IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                        mContext.registerReceiver(mBroadcastReceiver, discoverDevicesIntent);
                    }
                }
            });

            Button btnStartConnection = (Button) findViewById(R.id.btnStartConnection);

            btnStartConnection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBluetoothConnection.startClient(mBTDevice, MY_UUID_SECURE);
                }
            });

            lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);

            lvNewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mBluetoothAdapter.cancelDiscovery();

                    String deviceName = mBTDevices.get(i).getName();

                    Log.d(TAG, "Trying to pair with " + deviceName);

                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {

                        Log.d(TAG, "Trying to pair with " + deviceName);
                        mBTDevices.get(i).createBond();


                        mBTDevice = mBTDevices.get(i);
                    }

                }
            });

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        }

        private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                if(action.equals(BluetoothDevice.ACTION_FOUND)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(!mBTDevices.contains(device)){
                        mBTDevices.add(device);

                        mDeviceLstAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                        lvNewDevices.setAdapter(mDeviceLstAdapter);
                    }
                }
            }
        };

        public void dismiss(){
            mContext.unregisterReceiver(mBroadcastReceiver);
            super.dismiss();
        }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1){
            int permissionCheck = mContext.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += mContext.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                ((Activity)mContext).requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }


}

