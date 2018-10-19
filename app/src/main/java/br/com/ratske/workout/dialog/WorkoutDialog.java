package br.com.ratske.workout.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.ratske.workout.R;
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
                        DatabaseReference fbWorkout = FirebaseDatabase.getInstance().getReference("workouts");
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = mAuth.getCurrentUser();

                        Workout workout = new Workout();
                        workout.setName(txtWorkoutName.getText().toString());
                        workout.setUser(user.getUid());
                        workout.setOrder(1);
                        fbWorkout.setValue(workout);
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
