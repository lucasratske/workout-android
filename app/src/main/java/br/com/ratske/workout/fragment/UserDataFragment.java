package br.com.ratske.workout.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import br.com.ratske.workout.utils.Utils;

public class UserDataFragment extends Fragment {

    private DatabaseReference fbUsers;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_data, container, false);

        final EditText textName = v.findViewById(R.id.textName);
        final EditText textAge = v.findViewById(R.id.textAge);
        final EditText textWeight = v.findViewById(R.id.textWeight);
        final EditText textHeight = v.findViewById(R.id.textHeight);
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
                        textName.setText(user.getName());
                        textAge.setText(Integer.toString(user.getAge()));
                        textWeight.setText(Utils.numberToBrLocale(user.getWeight()));
                        textHeight.setText(Integer.toString(user.getHeight()));
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
                fbUsers.child(userId).child("name").setValue(textName.getText().toString());
                fbUsers.child(userId).child("age").setValue(Integer.parseInt(textAge.getText().toString()));
                fbUsers.child(userId).child("weight").setValue(Utils.commaToPointSeparator( textWeight.getText().toString()));
                fbUsers.child(userId).child("height").setValue(Integer.parseInt(textHeight.getText().toString()));

                Toast.makeText(getActivity().getApplicationContext(),
                        getResources().getString(R.string.success_user_data),
                        Toast.LENGTH_LONG).show();
            }
        });

        return v;
    }

}
