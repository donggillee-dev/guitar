package cse.ssu.guitar;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.String.valueOf;

public class LoginActivity extends AppCompatActivity {
    Button signUpButton;
    Button loginButton;
    private String return_msg;
    private String sendMessage;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        loginButton = (Button) findViewById(R.id.loginButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);

        final EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
        final EditText pwEditText = (EditText) findViewById(R.id.pwEditText);
        //final EditText myNumberEditText = (EditText) findViewById(R.id.myNumberEditText);
        //final EditText partnerNumberEditText = (EditText) findViewById(R.id.partnerNumberEditText);

        //로그인 버튼 이벤트 리스너
        loginButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                // EditText로부터 입력값을 받아오는 부분
                email = emailEditText.getText().toString();
                password = pwEditText.getText().toString();

                // 서버에 보낼 형식에 맞춰 만드는 부분
                int nullSize = 50 - (email.length());
                for(int i=0; i<nullSize; i++)
                    email += " ";

                nullSize = 12 - (password.length());
                for(int i=0; i<nullSize; i++)
                    password += " ";

                logIn();
            }
        });

        //회원가입 버튼 이벤트 리스너
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void logIn() {
        // 서버에 보낼 메세지
        sendMessage = "00" + email + password;
        password.length();
        String a = valueOf(password.length());
        Log.d("TCP", a);

        // TCP 쓰레드 생성
        TCPClient tcpThread = new TCPClient(sendMessage);
        Thread thread = new Thread(tcpThread);
        thread.start();

        try {
            thread.join();
            Log.d("TCP", "try");
        } catch (Exception e) {
            Log.d("TCP", "error");
        }
        return_msg = tcpThread.getReturnMessage();
        //로그인 성공
        if ("00SUCCESS".equals(return_msg)) {
//            Intent intent = new Intent(LoginActivity.this, FragmentBaseActivity.class);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            //intent.putExtra(“text”,String.valueOf(editText.getText()));
            startActivity(intent);
            finish();
        }
        //로그인 실패
        else if ("00FAILURE".equals(tcpThread.getReturnMessage())) {
            Log.d("TCP", "failure" + return_msg);

            //연동 할 것이라고 알려주는 다이얼로그
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    LoginActivity.this);

            // 제목셋팅
            alertDialogBuilder.setTitle("로그인 실패");

            // AlertDialog 셋팅
            alertDialogBuilder
                    .setMessage("로그인에 실패하였습니다.")
                    .setCancelable(false)
                    .setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // 다이얼로그 생성
            AlertDialog alertDialog = alertDialogBuilder.create();

            // 다이얼로그 보여주기
            alertDialog.show();

        } else {
            //연동 할 것이라고 알려주는 다이얼로그
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    LoginActivity.this);

            // 제목셋팅
            alertDialogBuilder.setTitle("서버가 꺼져있음");

            // AlertDialog 셋팅
            alertDialogBuilder
                    .setMessage("서버가 꺼져있습니다.\n문의 : 010-9350-0510")
                    .setCancelable(false)
                    .setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // 다이얼로그 생성
            AlertDialog alertDialog = alertDialogBuilder.create();

            // 다이얼로그 보여주기
            alertDialog.show();
        }
    }
}