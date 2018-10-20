package br.com.ratske.workout.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

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

public class UserActivity extends AppCompatActivity {

    private DatabaseReference fbUsers;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        EditText textName = findViewById(R.id.textName);
        EditText textEmail = findViewById(R.id.textEmail);
        EditText textAge = findViewById(R.id.textAge);
        EditText textWeight = findViewById(R.id.textWeight);
        EditText textHeight = findViewById(R.id.textHeight);

        FirebaseUser user = mAuth.getCurrentUser();

        fbUsers = FirebaseDatabase.getInstance().getReference("users");
        Query qUsers = fbUsers.orderByChild("authId").equalTo(user.getUid());
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        qUsers.addListenerForSingleValueEvent(userListener);
    }
}
