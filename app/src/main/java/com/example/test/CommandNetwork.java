package com.example.test;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

interface NetworkCallback {
    void onReceiveData(JSONObject json);
}

public class CommandNetwork {
    private static CommandNetwork instance = new CommandNetwork();
    private CommandNetwork() {}
    public static CommandNetwork getInstance() { return instance;}

    private ServerSocket mServerSocket;
    private Socket mSocket;
    private InputStream is;
    private InputStreamReader isr;
    private BufferedReader br;

    private NetworkCallback callback;
    public MainActivity mainActivity;

    public String typingResults = null;

    public void start(MainActivity activity) {
        this.mainActivity = activity;
        new ConnectThread().start();
    }

    private class recvThread extends Thread {
        @Override
        public void run() {
            super.run();

            while(mSocket != null){

                try{

                    is = mSocket.getInputStream();
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);


                    // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
                    String response = br.readLine();
                    if(response != null) {
                        typingResults = response;
                        try{
                            JSONObject json = new JSONObject(response);
                            if(callback != null) {
                                callback.onReceiveData(json);
                            }
                            Log.d("YueTing", response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    private class ConnectThread extends Thread {

        @Override
        public void run() {
            super.run();

            while(true) {
                try {
                    mServerSocket = new ServerSocket(7777);

                    while (true) {
                        Log.d("YueTing", "Waiting for connection");
                        mSocket = mServerSocket.accept();
                        Log.d("YueTing", "connected by : " + mSocket.getInetAddress().toString());
                        new recvThread().start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /** Send string(utf-8) to server */
    public void send(final String msg) {

        if(mSocket == null) { return; }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    OutputStream os = mSocket.getOutputStream();

                    os.write((msg + "\n" ).getBytes("utf-8"));

                    os.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void setCallback(NetworkCallback callback) {
        this.callback = callback;
    }
}
