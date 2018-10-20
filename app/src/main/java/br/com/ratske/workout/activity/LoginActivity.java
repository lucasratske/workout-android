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

import br.com.ratske.workout.R;
import br.com.ratske.workout.utils.Utils;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText editEmail;
    private EditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in\
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
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

    public void signIn(View view) {
        if (isSignInFormValid()) {
            mAuth.signInWithEmailAndPassword(
                    editEmail.getText().toString(),
                    editPassword.getText().toString()).addOnCompleteListener(
                    LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful())
                                Toast.makeText(LoginActivity.this,
                                        getResources().getString(R.string.error_incorrect_password),
                                        Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(LoginActivity.this,
                                        getResources().getString(R.string.success_signed_in),
                                        Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void register(View view) {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }

    private boolean isSignInFormValid() {

        boolean isValid = true;

        if (TextUtils.isEmpty(editEmail.getText())) {
            isValid = false;
            editEmail.setError(getResources().getString(R.string.error_field_required));
        }

        if (!Utils.isEmailValid(editEmail.getText().toString())){
            isValid = false;
            editEmail.setError(getResources().getString(R.string.error_invalid_email));
        }

        if (TextUtils.isEmpty(editPassword.getText())){
            isValid = false;
            editPassword.setError(getResources().getString(R.string.error_field_required));
        }

        if (!Utils.isPasswordValid(editPassword.getText().toString())){
            isValid = false;
            editPassword.setError(getResources().getString(R.string.error_invalid_password));
        }


        return isValid;
    }
}
