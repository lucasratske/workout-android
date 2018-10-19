package br.com.ratske.workout.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.ratske.workout.R;
import br.com.ratske.workout.model.User;
import br.com.ratske.workout.model.Workout;

public class WorkoutDialog extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.title_dialog_workout));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_add_workout, null);
        final EditText txtWorkoutName = view.findViewById(R.id.txtWorkoutName);
        builder.setView(view)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.prompt_add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final DatabaseReference fbWorkout = FirebaseDatabase.getInstance().getReference("workouts");
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        final FirebaseUser user = mAuth.getCurrentUser();

                        Query qWorkouts = fbWorkout.orderByChild("user").equalTo(user.getUid());

                        ValueEventListener workoutListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Log.i("workoutListener", user.getUid());
                                if (dataSnapshot.exists()) {
                                    Log.i("workoutListener", "existe");
                                    List<Workout> workouts = new ArrayList<>();

                                    Log.i("workoutListener", dataSnapshot.toString());
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Workout workout = snapshot.getValue(Workout.class);
                                        workouts.add((workout));
                                        Log.i("workoutListener", workout.getName());
                                    }

//                                    Workout workout = new Workout();
//                                    workout.setName(txtWorkoutName.getText().toString());
//                                    workout.setUser(user.getUid());
//                                    workout.setOrder(1);
//                                    fbWorkout.setValue(workout);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };
                        qWorkouts.addValueEventListener(workoutListener);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.prompt_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder.create();
    }
}
