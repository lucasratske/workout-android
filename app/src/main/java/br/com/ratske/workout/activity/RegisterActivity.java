package br.com.ratske.workout.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.ratske.workout.R;
import br.com.ratske.workout.model.User;
import br.com.ratske.workout.utils.Utils;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText textEmail;
    private EditText textPassword;
    private EditText textName;
    private EditText textAge;
    private EditText textWeight;
    private EditText textHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in\
                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };

        textEmail = findViewById(R.id.textEmail);
        textPassword = findViewById(R.id.textPassword);
        textName = findViewById(R.id.textName);
        textAge = findViewById(R.id.textAge);
        textWeight = findViewById(R.id.textWeight);
        textHeight = findViewById(R.id.textHeight);

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void createUser(View view) {

        if (isSignInFormValid()) {
            String email = textEmail.getText().toString();
            String password = textPassword.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                        RegisterActivity.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                    DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
                                    String key = users.push().getKey();
                                    User user = new User();
                                    user.setId(key);
                                    user.setAuthId(firebaseUser.getUid());
                                    user.setName(textName.getText().toString());
                                    user.setEmail(firebaseUser.getEmail());
                                    user.setAge(Integer.parseInt(textAge.getText().toString()));
                                    user.setWeight(Float.parseFloat(textWeight.getText().toString()));
                                    user.setHeight(Integer.parseInt(textHeight.getText().toString()));

                                    users.child(key).setValue(user);

                                    Toast.makeText(
                                            RegisterActivity.this,
                                            getResources().getString(R.string.success_signed_in),
                                            Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(
                                            RegisterActivity.this,
                                            getResources().getString(R.string.error_register),
                                            Toast.LENGTH_SHORT).show();
                            }
                        });
        }
        else
            Toast.makeText(
                    this,
                    getResources().getString(R.string.error_all_fields_required),
                    Toast.LENGTH_LONG).show();
    }

    private boolean isSignInFormValid() {
        boolean isValid = true;

        if (TextUtils.isEmpty(textEmail.getText()) ||
            TextUtils.isEmpty(textPassword.getText()) ||
            TextUtils.isEmpty(textName.getText()) ||
            TextUtils.isEmpty(textAge.getText()) ||
            TextUtils.isEmpty(textHeight.getText()) ||
            TextUtils.isEmpty(textWeight.getText()))
            isValid = false;


        return isValid;
    }
}
