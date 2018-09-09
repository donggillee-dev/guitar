package cse.ssu.guitar;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import Network.PostLogin;

public class LoginActivity extends AppCompatActivity {
    private Button signUpButton;
    private Button loginButton;
    private RelativeLayout rl;
    private InputMethodManager imm;
    public static String id;
    public static String token;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(token != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        loginButton = (Button) findViewById(R.id.loginButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        rl = (RelativeLayout)findViewById(R.id.loginLayout);

        final EditText idEditText = (EditText) findViewById(R.id.idEditText);
        final EditText pwEditText = (EditText) findViewById(R.id.pwEditText);

        //로그인 버튼 이벤트 리스너
        loginButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                id = idEditText.getText().toString();
                password = pwEditText.getText().toString();

                LoginTask task = new LoginTask();
                task.execute();
            }
        });

        //회원가입 버튼 이벤트 리스너
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(idEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(pwEditText.getWindowToken(), 0);
            }
        });
    }

    private class LoginTask extends AsyncTask<Void, Void, String> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... voids) {

            PostLogin postLogin = new PostLogin();
            String response = null;
            try {
                response = postLogin.post(MainActivity.serverUrl + "login", id, password);
            } catch(TimeoutException e) {
                Toast.makeText(getApplicationContext(), "Time out. Server Off", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            JSONObject jObject = null;
            if(response == null)
                Toast.makeText(getApplicationContext(), "Server off", Toast.LENGTH_SHORT).show();
            else {
                try {
                    //회원가입 성공
                    jObject = new JSONObject(response);
                    String returnValue = jObject.getString("status");
                    if (returnValue.compareTo("ok") == 0) {
                        Toast.makeText(getApplicationContext(), "성공적으로 로그인했습니다. ", Toast.LENGTH_SHORT).show();
                        token = jObject.getString("token");
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                        this.cancel(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}