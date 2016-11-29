package com.example.jake.caastme;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by jake on 2016/11/24.
 */
public class SocketClient implements Runnable {
    private Socket s;
  /*  // 定义向UI线程发送消息的Handler对象
    Handler handler;
    // 定义接收UI线程的Handler对象
    Handler revHandler;*/
    // 该线程处理Socket所对用的输入输出流
    BufferedReader br = null;
    OutputStream os = null;


    @SuppressLint("HandlerLeak")
    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            s = new Socket();

//          s = new Socket("192.168.0.78", 8888);//此方法不能设定连接时限
            s.connect(new InetSocketAddress("192.168.0.114/pcgo", 4322), 5000);
            Log.d("socket__", "$$");

            //准备向服务端发送数据
            os = s.getOutputStream();
            os.write(("你好小微").getBytes("utf-8"));

            Log.d("socket__", "1555@@@@@@@@@@@@@@@@@@@@");


            //得到服务端的信息
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));

            Log.d("socket__", "22@@@@@@@@@@@@@@@@@@@@");
            // 启动一条子线程来读取服务器相应的数据
            new Thread() {

                @Override
                public void run() {
                    String content = null;
                    // 不断的读取Socket输入流的内容
                    try {
                        while ((content = br.readLine()) != null) {
                            // 每当读取到来自服务器的数据之后，发送的消息通知程序
                            Log.d("socket__", content);
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }

            }.start();

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }

    }
}
