package com.syp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.syp.MainActivity;
import com.syp.model.Singleton;
import com.syp.model.User;

import com.syp.R;

public class LoginFragment extends Fragment {

    private MainActivity mainActivity;
    private EditText email;
    private EditText password;
    private TextView error;
    private Button signInButton;
    private Button registerButton;
    private View v;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_login, container, false);

        // Get Data
        email = v.findViewById(R.id.emailSignIn);
        password = v.findViewById(R.id.passwordSignIn);
        signInButton = v.findViewById(R.id.signInButton);
        registerButton = v.findViewById(R.id.registerButton);
        error = v.findViewById(R.id.loginInvalid);
        mainActivity = (MainActivity) getActivity();


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkLogin();


            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                NavDirections action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });

        return v;
    }

    private void checkLogin(){
        DatabaseReference allUserRef = Singleton.get(mainActivity).getDatabase()
                .child(Singleton.firebaseUserTag);

        allUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    User user = child.getValue(User.class);
                    if(user.getEmail().equalsIgnoreCase(email.getText().toString())
                    && user.getPassword().equalsIgnoreCase(password.getText().toString())){
                        Singleton.get(mainActivity).setUserId(user.getId());
                        NavDirections action = LoginFragmentDirections.actionLoginFragmentToMapFragment();
                        Navigation.findNavController(v).navigate(action);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}