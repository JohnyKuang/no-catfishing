package com.example.johny.nocatfishing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwrdInput;
    private String tempName = "johnyk";
    private String tempPass = "CMPT";
    private TextView errMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameInput = (EditText) findViewById(R.id.username_input);
        passwrdInput = (EditText) findViewById(R.id.password_input);
        errMsg = (TextView) findViewById(R.id.error_msg);
    }

    public void onClickLogin(View v) {
        /*
        String name = usernameInput.getText().toString();
        String passwrd = passwrdInput.getText().toString();
        if(verifyLogin(name,passwrd)){
          goToHome();
        } else {
          errMsg.setVisibility(View.VISIBLE);
        }
        */
        goToHome();
    }

    private void goToHome(){
        Intent intent = new Intent(this,Home.class);
        startActivity(intent);
    }

    private boolean verifyLogin(String uname, String pass){
        return (tempName.equals(uname) && tempPass.equals(pass));
    }
}
