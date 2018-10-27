package br.com.ratske.workout.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.ratske.workout.R;
import br.com.ratske.workout.adapter.WorkoutAdapter;
import br.com.ratske.workout.dialog.WorkoutDialog;
import br.com.ratske.workout.model.User;
import br.com.ratske.workout.model.Workout;
import br.com.ratske.workout.utils.RecyclerItemClickListener;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference fbUsers;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView tvWelcome;
    private RecyclerView recyclerView;
    private List<Workout> workouts = new ArrayList<>();
    private TextView tvNoItems;

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
        tvNoItems = findViewById(R.id.tvNoItems);

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


            recyclerView = findViewById(R.id.recyclerView);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.addItemDecoration( new DividerItemDecoration(this, LinearLayout.VERTICAL));

            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;

            getWorkouts(dayOfWeek);

            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(
                            getApplicationContext(),
                            recyclerView,
                            new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                }

                                @Override
                                public void onLongItemClick(View view, int position) {

                                }

                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                }
                            }
                    )
            );
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

    private void getWorkouts(int day) {

        DatabaseReference fbWorkout = FirebaseDatabase.getInstance().getReference("workouts");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        Query qWorkouts = fbWorkout
//                            .orderByChild("user")
//                            .equalTo(user.getUid())
                .orderByChild("dayOfWeek")
                .startAt(day)
                .endAt(day);

        ValueEventListener workoutListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Workout> workouts = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Workout workout = snapshot.getValue(Workout.class);
                        workouts.add((workout));
                        Log.i("workoutListener", workout.getName());
                    }
                }
                setRecyclerView(workouts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("workoutListener", databaseError.getMessage());
            }
        };
        qWorkouts.addValueEventListener(workoutListener);
    }

    private void setRecyclerView(List<Workout> workouts) {
        this.workouts = workouts;
        WorkoutAdapter adapter = new WorkoutAdapter(workouts);
        recyclerView.setAdapter( adapter );
        tvNoItems.setText(
                (workouts.size() == 0) ?
                        getResources().getString(R.string.info_no_workout) : "");
    }
}
