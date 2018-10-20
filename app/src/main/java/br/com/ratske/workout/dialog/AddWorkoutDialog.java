package br.com.ratske.workout.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
import java.util.UUID;

import br.com.ratske.workout.R;
import br.com.ratske.workout.model.Workout;

public class AddWorkoutDialog extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.title_dialog_workout));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_add_workout, null);
        final EditText txtWorkoutName = view.findViewById(R.id.txtWorkoutName);
        final EditText txtWorkoutDescription = view.findViewById(R.id.txtWorkoutDescription);
        final RadioGroup rgDays = view.findViewById(R.id.rgDays);
        builder.setView(view)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.prompt_add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (rgDays.getCheckedRadioButtonId() != -1 && !TextUtils.isEmpty(txtWorkoutName.getText())) {

                            final DatabaseReference fbWorkout = FirebaseDatabase.getInstance().getReference("workouts");
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            final FirebaseUser user = mAuth.getCurrentUser();

                            Query qWorkouts = fbWorkout.orderByChild("user").equalTo(user.getUid());

                            ValueEventListener workoutListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    int day = 0;
                                    switch (rgDays.getCheckedRadioButtonId()) {
                                        case R.id.rbMonday:
                                            day = 1;
                                            break;
                                        case R.id.rbTuerday:
                                            day = 2;
                                            break;
                                        case R.id.rbWednesday:
                                            day = 3;
                                            break;
                                        case R.id.rbThursday:
                                            day = 4;
                                            break;
                                        case R.id.rbFriday:
                                            day = 5;
                                            break;
                                        case R.id.rbSaturday:
                                            day = 6;
                                            break;
                                        case R.id.rbSunday:
                                            day = 0;
                                            break;
                                    }

                                    //Get the key to set it into id and workouts' child
                                    String key = fbWorkout.push().getKey();

                                    if (dataSnapshot.exists()) {
                                        List<Workout> workouts = new ArrayList<>();

                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            Workout workout = snapshot.getValue(Workout.class);
                                            workouts.add((workout));
                                        }

                                        Workout workout = new Workout();
                                        workout.setId(key);
                                        workout.setName(txtWorkoutName.getText().toString());
                                        workout.setUser(user.getUid());
                                        workout.setOrder(workouts.size() + 1);
                                        workout.setDayOfWeek(day);
                                        workout.setDescription(txtWorkoutDescription.getText().toString());
                                        fbWorkout.child(key).setValue(workout);

                                    } else {
                                        Workout workout = new Workout();
                                        workout.setId(key);
                                        workout.setName(txtWorkoutName.getText().toString());
                                        workout.setUser(user.getUid());
                                        workout.setOrder(1);
                                        workout.setDayOfWeek(day);
                                        workout.setDescription(txtWorkoutDescription.getText().toString());
                                        fbWorkout.child(key).setValue(workout);
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                            qWorkouts.addListenerForSingleValueEvent(workoutListener);
                        }
                        else {
                            dialogInterface.cancel();
                        }
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
