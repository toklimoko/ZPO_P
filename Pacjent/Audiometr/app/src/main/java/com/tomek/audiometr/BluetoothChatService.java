package com.tomek.audiometr;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class BluetoothChatService {

    public enum ConnectionStatus{
        CONNECTED,
        DISCONNECTED
    }

    private static final String TAG = "BluetoothChatServ";

    private static final String appName = "BTApp";

    private static final UUID MY_UUID_SECURE =
            UUID.fromString("5580181b-4330-427d-b7d7-6f2aabb10550");

    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;

    private AcceptThread mSecureAcceptThread;
    private ConnectThread mConnectThread;
    private BluetoothDevice mDevice;
    private UUID deviceUUID;
    private ProgressDialog mProgressDialog;
    private ConnectedThread mConnectedThread;
    private ActivityCallback mCallback;


    public BluetoothChatService(Context mContext, ActivityCallback callback) {
        mCallback = callback;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mContext = mContext;
        start();
    }

    private class AcceptThread extends Thread {

        private final BluetoothServerSocket mServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_SECURE);
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }
            mServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            try {
                socket = mServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
            }

            if (socket != null) {
                manageMyConnectedSocket(socket, mDevice);
            }
        }

        private void cancel() {
            try {
                mServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }

    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            mDevice = device;
            deviceUUID = uuid;
        }

        public void run() {

            BluetoothSocket tmp = null;
            try {
                tmp = mDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mSocket = tmp;

            mBluetoothAdapter.cancelDiscovery();

            try {
                mSocket.connect();
            } catch (IOException connectException) {
                try {
                    mSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + MY_UUID_SECURE );
            }

            manageMyConnectedSocket(mSocket, mDevice);
        }

        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    public synchronized void start() {
        Log.d(TAG, "start");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread();
            mSecureAcceptThread.start();
        }

    }

    public void startClient(BluetoothDevice device, UUID uuid) {

        Log.d(TAG, "startClient: Started.");

        mProgressDialog = ProgressDialog.show(mContext, "Connecting Bluetooth",
                "Please wait...", true);

        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();

    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mSocket;
        private final InputStream mInputStream;
        private final OutputStream mOutputStream;

        public ConnectedThread(BluetoothSocket socket){
            mSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                mProgressDialog.dismiss();
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            try {
                tmpIn = mSocket.getInputStream();
            }catch (IOException e){
                Log.e(TAG, "Error occurred when creating input stream", e);
            }

            try {
                tmpOut = mSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mInputStream = tmpIn;
            mOutputStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];

            int bytes;
            while (true){
                try {
                    bytes = mInputStream.read(buffer);
                    Log.d(TAG, "Bytes received " + bytes + " bytes: " + buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    mCallback.setReceivedBytes(incomingMessage);

                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    mCallback.setConnectionStatus(ConnectionStatus.DISCONNECTED);
                    break;
                }
            }
        }

        public void write(byte[] bytes){
            try {
                mOutputStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);
                mCallback.setConnectionStatus(ConnectionStatus.DISCONNECTED);
            }
        }

        public void cancel(){
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket mSocket, BluetoothDevice mDevice){
        mCallback.setConnectionStatus(ConnectionStatus.CONNECTED);
        mCallback.setBluetoothConnectionInstance(this);
        mCallback.dismissConnectionDialog();

        mConnectedThread = new ConnectedThread(mSocket);
        mConnectedThread.start();
    }

    public void write(byte[] out){

        mConnectedThread.write(out);
    }

}
