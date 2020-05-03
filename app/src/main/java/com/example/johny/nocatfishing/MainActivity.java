package com.example.johny.nocatfishing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button loginButton, regButton;
    private EditText emailInput, passInput;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener fireBaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        fireBaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) { //this means user got through
                    Intent intent = new Intent(MainActivity.this, Home.class); //switch to Home activity
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };
        loginButton = (Button) findViewById(R.id.login_button);
        emailInput = (EditText) findViewById(R.id.username_input);
        passInput = (EditText) findViewById(R.id.password_input);
        loginButton.setOnClickListener(new View.OnClickListener() { //for when the login button is clicked
            @Override
            public void onClick(View v) {
                final String email = emailInput.getText().toString();
                final String password = passInput.getText().toString();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) { //if registration is unsucessful
                            Toast.makeText(MainActivity.this, "Sign in error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        regButton = (Button) findViewById(R.id.register_button);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegistration(v);
            }
        });
    }

    //this is to make sure the firebase auth state listener starts when activity is opened
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(fireBaseAuthStateListener);
    }

    //this is to remove it when activity is closed
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(fireBaseAuthStateListener);
    }

    public void goToRegistration(View view){
        Intent intent = new Intent(this,RegistrationActivity.class);
        startActivity(intent);
    }
}



