package com.tomek.audiometr;

/**
 * Created by Patrycja on 2018-04-03.
 */

public interface ActivityCallback {

    void setReceivedBytes(String data);

    void setBluetoothConnectionInstance(BluetoothChatService instance);

    void dismissConnectionDialog();

    void setConnectionStatus(BluetoothChatService.ConnectionStatus status);

}
