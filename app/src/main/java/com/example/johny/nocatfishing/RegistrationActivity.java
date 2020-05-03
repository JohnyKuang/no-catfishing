package com.example.johny.nocatfishing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private Button regButton;
    private EditText emailInput, passInput, nameInput;
    private RadioGroup mRadioGroup;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener fireBaseAuthStateListener;
    private final static String TAG = "RegistrationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        //if user is logged in we can continue
        fireBaseAuthStateListener = new FirebaseAuth.AuthStateListener() {//listener checks for the login status
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null){ //this means user logged in and we can move on to next page
                    Intent intent = new Intent(RegistrationActivity.this,Home.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        regButton = (Button) findViewById(R.id.register_button2);
        emailInput = (EditText)findViewById(R.id.email_input);
        passInput = (EditText)findViewById(R.id.password_input_registration);
        nameInput = (EditText)findViewById(R.id.name_input);
        mRadioGroup = (RadioGroup)findViewById(R.id.radioGroup);


        regButton.setOnClickListener(new View.OnClickListener() { //for when the register button is clicked
            @Override
            public void onClick(View v) {
                int selectID = mRadioGroup.getCheckedRadioButtonId();//get which button got selected of the two
                final RadioButton radioButton = (RadioButton) findViewById(selectID);
                if (radioButton == null) { //check if user chose a button "male" or "female"
                    return; // if not then just return without continuing
                }
                final String email = emailInput.getText().toString();
                final String password = passInput.getText().toString();
                final String mName = nameInput.getText().toString();
                //someFunction(email,password,radioButton); //function registers user

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    Toast.makeText(RegistrationActivity.this, "Registration Successful",
                                            Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    //if creation of user is successful then we will add uid info to database
                                    String userID = user.getUid();
                                    //line below points to the "name" child
                                    DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(radioButton.getText().toString()).child(userID).child("name");
                                    currentUserDb.setValue(mName);
                                    //set profileImageURL to default
                                    DatabaseReference currentUserDbP = FirebaseDatabase.getInstance().getReference().child("Users").child(radioButton.getText().toString()).child(userID).child("profileImageUrl");
                                    currentUserDbP.setValue("default"); //


                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegistrationActivity.this, "Authentication failed yo.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    //this is to make sure the firebase auth state listener starts when activity is opened
    @Override
    public void onStart() {//follows onCreate
        super.onStart();
        mAuth.addAuthStateListener(fireBaseAuthStateListener); //to call and start fireBaseAuthStateListener above
    }
    //this is to remove it when activity is closed
    //as in remove the listener
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(fireBaseAuthStateListener);
    }
}
