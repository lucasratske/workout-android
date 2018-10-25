package br.com.ratske.workout.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.com.ratske.workout.R;
import br.com.ratske.workout.activity.LoginActivity;
import br.com.ratske.workout.activity.MainActivity;
import br.com.ratske.workout.model.User;
import br.com.ratske.workout.utils.Utils;

public class UserEmailFragment extends Fragment {


    private DatabaseReference fbUsers;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_email, container, false);


        final TextView textEmailAtual = v.findViewById(R.id.textEmailAtual);
        final TextView tvUserEmailError = v.findViewById(R.id.tvUserEmailError);
        final EditText textEmail = v.findViewById(R.id.textEmail);
        final EditText textPassword = v.findViewById(R.id.textPassword);
        final Button btnSalvar = v.findViewById(R.id.btnSalvar);

        final FirebaseUser fbUser = mAuth.getCurrentUser();

        fbUsers = FirebaseDatabase.getInstance().getReference("users");
        Query qUsers = fbUsers.orderByChild("authId").equalTo(fbUser.getUid());
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        textEmailAtual.setText(user.getEmail());
                        userId = user.getId();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        qUsers.addValueEventListener(userListener);

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signInWithEmailAndPassword(
                        textEmailAtual.getText().toString(),
                        textPassword.getText().toString()).addOnCompleteListener(
                        getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful())
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            getResources().getString(R.string.error_incorrect_user_password),
                                            Toast.LENGTH_SHORT).show();
                                else
                                {
                                    if (TextUtils.isEmpty(textEmail.getText())) {
                                        Toast.makeText(getActivity().getApplicationContext(),
                                                getResources().getString(R.string.error_user_change_fill),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        if (!Utils.isEmailValid(textEmail.getText().toString())) {
                                            Toast.makeText(getActivity().getApplicationContext(),
                                                    getResources().getString(R.string.error_invalid_email),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        else {

                                            fbUser.updateEmail(textEmail.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {

                                                                fbUsers.child(userId).child("email").setValue(textEmail.getText().toString());

                                                                Toast.makeText(getActivity().getApplicationContext(),
                                                                        getResources().getString(R.string.success_user_email),
                                                                        Toast.LENGTH_LONG).show();

                                                                mAuth.signOut();
                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.i("updateEmail", e.getMessage());
                                                            tvUserEmailError.setText(getResources().getString(R.string.error_change_email));
                                                        }
                                                    });

                                        }
                                    }

                                }
                            }
                        });



            }
        });

        return v;
    }
}
