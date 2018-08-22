package cse.ssu.guitar;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignInActivity extends AppCompatActivity {
    Button joinButton;
    private String return_msg;
    private String sendMessage;
    private String email;
    private String name;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        joinButton = (Button) findViewById(R.id.joinButton);
        final EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
        final EditText pwEditText = (EditText) findViewById(R.id.pwEditText);
        final EditText nameEditText = (EditText) findViewById(R.id.nameEditText);

        joinButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                // EditText로부터 입력값을 받아오는 부분
                email = emailEditText.getText().toString();
                name = nameEditText.getText().toString();
                password = pwEditText.getText().toString();

                // 서버에 보낼 형식에 맞춰 만드는 부분
                int nullSize = 50 - (email.length());
                for(int i=0; i<nullSize; i++)
                    email += " ";

                nullSize = 11 - (name.length());
                for(int i=0; i<nullSize; i++)
                    name += " ";

                nullSize = 12 - (password.length());
                for(int i=0; i<nullSize; i++)
                    password += " ";

                signIn();
            }
        });
    }

    private void signIn() {
        // 서버에 보낼 메세지
        sendMessage = "11" + email + password + name;

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
        Log.d("TCP", "ret" + tcpThread.getReturnMessage());

        //회원가입 성공 시 연동 화면으로 넘어감
        if ("11SUCCESS".equals(return_msg)) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            //intent.putExtra(“text”,String.valueOf(editText.getText()));
            startActivity(intent);
            finish();
        }
        //회원가입 실패
        else if("11FAILURE".equals(tcpThread.getReturnMessage())){
            Log.d("TCP", "failure" + return_msg);

            //연동 할 것이라고 알려주는 다이얼로그
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    SignInActivity.this);

            // 제목셋팅
            alertDialogBuilder.setTitle("회원가입 실패");

            // AlertDialog 셋팅
            alertDialogBuilder
                    .setMessage("이미 가입된 이메일입니다.")
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
        else
        {
            //연동 할 것이라고 알려주는 다이얼로그
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    SignInActivity.this);

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