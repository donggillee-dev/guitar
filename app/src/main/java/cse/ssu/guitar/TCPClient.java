package cse.ssu.guitar;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient implements Runnable {
    private final String serverIP = "210.89.178.111";
    //private final String serverIP = "10.0.2.15";
    private final int serverPort = 9190;    //포트번호
    private Socket inetSocket = null;
    private String msg;
    private String return_msg;

    private Boolean flag=false;
    // private String return_msg;
    public TCPClient(String _msg) {
        this.msg = _msg;
    }


    public String getReturnMessage() {
        return return_msg;
    }

    public void run() {
        // TODO Auto-generated method stub
        try {
            Log.d("TCP", "C: Connecting...");

            inetSocket = new Socket(serverIP, serverPort);
            //inetSocket.connect(socketAddr);
            Log.d("TCP", "ewewewewewewewe");

            try {
                Log.d("TCP", "C: Sending: '" + msg + "'");
                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(inetSocket.getOutputStream())), true);
                out.println(msg);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(inetSocket.getInputStream()));
                return_msg = in.readLine();
                Log.d("TCP", "C: Server send to me this message -->"
                        + return_msg);

            } catch (Exception e) {
                Log.d("TCP", "catch");
            } finally {
                Log.d("TCP", "finally");
                inetSocket.close();
            }
        } catch (Exception e) {
            Log.d("TCP", "catch22222222222");
        }
    }
}
