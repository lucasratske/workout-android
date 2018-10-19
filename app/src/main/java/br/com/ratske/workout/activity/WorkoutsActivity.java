package br.com.ratske.workout.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.ratske.workout.R;
import br.com.ratske.workout.adapter.WorkoutAdapter;
import br.com.ratske.workout.dialog.WorkoutDialog;
import br.com.ratske.workout.model.Workout;
import br.com.ratske.workout.utils.RecyclerItemClickListener;

public class WorkoutsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Workout> workouts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);

        recyclerView = findViewById(R.id.recyclerView);

        WorkoutAdapter adapter = new WorkoutAdapter(this.workouts);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration( new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setAdapter( adapter );

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Workout workout = workouts.get( position );
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Item pressionado: " + workout.getName(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                Workout workout = workouts.get( position );
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Click longo: "  + workout.getName(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );
    }

    public void btnAdcWorkoutClick(View view) {
        WorkoutDialog workoutDialog = new WorkoutDialog();
        workoutDialog.show(getSupportFragmentManager(), "WorkoutDialog");
    }
}
