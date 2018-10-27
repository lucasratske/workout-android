package br.com.ratske.workout.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import br.com.ratske.workout.dialog.AddWorkoutDialog;
import br.com.ratske.workout.dialog.WorkoutDialog;
import br.com.ratske.workout.model.Workout;
import br.com.ratske.workout.utils.RecyclerItemClickListener;

public class WorkoutsActivity extends AppCompatActivity {

    private Spinner spinner;
    private RecyclerView recyclerView;
    private List<Workout> workouts = new ArrayList<>();
    private TextView tvNoItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);

        tvNoItems = findViewById(R.id.tvNoItems);

        spinner = findViewById(R.id.spinnerDays);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
                .createFromResource(
                this,
                        R.array.days, R.layout.support_simple_spinner_dropdown_item);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("setOnItemSelectedListener", Integer.toString(i));
                getWorkouts(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        recyclerView = findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration( new DividerItemDecoration(this, LinearLayout.VERTICAL));

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;

        spinner.setSelection(dayOfWeek);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Workout workout = workouts.get( position );
                                WorkoutDialog workoutDialog = new WorkoutDialog();
                                workoutDialog.setValue(workout.getId(), workout.getName(), workout.getDescription());
                                workoutDialog.show(getSupportFragmentManager(), "AddWorkoutDialog");
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

    public void btnAdcWorkoutClick(View view) {
        AddWorkoutDialog addWorkoutDialog = new AddWorkoutDialog();
        addWorkoutDialog.show(getSupportFragmentManager(), "AddWorkoutDialog");
    }
}
