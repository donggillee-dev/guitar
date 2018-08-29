package cse.ssu.guitar;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import Network.PostRegister;
import VO.UserVO;

public class SignInActivity extends AppCompatActivity {
    Button joinButton;
    private String return_msg;
    private String sendMessage;
    private String email;
    private String name;
    private String password;
    private String id;
    private UserVO user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        joinButton = (Button) findViewById(R.id.joinButton);
        final EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
        final EditText pwEditText = (EditText) findViewById(R.id.pwEditText);
        final EditText idEditText = (EditText) findViewById(R.id.idEditText);
        final EditText nameEditText = (EditText) findViewById(R.id.nameEditText);


        joinButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                // EditText로부터 입력값을 받아오는 부분
                email = emailEditText.getText().toString();
                name = nameEditText.getText().toString();
                password = pwEditText.getText().toString();
                id = idEditText.getText().toString();

                Log.v("sign in", email + " " + name + " "+ password + " " + id);

                user = new UserVO(name, email, id, password);

                SigninTask task = new SigninTask();
                task.execute();
            }
        });
    }

    private class SigninTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {

            PostRegister postRegister = new PostRegister();
            String response = null;
            try {
                response = postRegister.post(MainActivity.serverUrl+"register", user); //http://ttac.neinsys.io:8000/signup
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            JSONObject jObject = null;

            {
                try {
                    //회원가입 성공
                    jObject = new JSONObject(response);
                    String returnValue = jObject.getString("status");
                    if (returnValue.compareTo("OK") == 0) {
                        Toast.makeText(getApplicationContext(), "회원가입이 성공 했습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "중복된 아이디 입니다.", Toast.LENGTH_SHORT).show();
                        this.cancel(true);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}