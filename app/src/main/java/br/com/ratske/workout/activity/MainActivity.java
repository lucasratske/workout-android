package br.com.ratske.workout.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.com.ratske.workout.R;
import br.com.ratske.workout.model.User;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference fbUsers;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };

        tvWelcome = findViewById(R.id.tvWelcome);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            fbUsers = FirebaseDatabase.getInstance().getReference("users");
            Query qUsers = fbUsers.orderByChild("authId").equalTo(user.getUid());

            ValueEventListener userListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            tvWelcome.setText(getResources().getString(R.string.info_welcome) + "  " + user.getName());
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w("addListenerForSingleValueEvent", "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            };
            qUsers.addListenerForSingleValueEvent(userListener);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.logout) {
            mAuth.signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    public void openWorkouts(View view) {
        Intent i = new Intent(MainActivity.this, WorkoutsActivity.class);
        startActivity(i);
    }

    public void openUser(View view) {
        Intent i = new Intent(MainActivity.this, UserActivity.class);
        startActivity(i);
    }
}
