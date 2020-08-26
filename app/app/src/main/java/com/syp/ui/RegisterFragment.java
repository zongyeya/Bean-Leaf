package com.syp.ui;

import android.graphics.Color;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.syp.model.Cafe;
import com.syp.model.Singleton;
import com.syp.model.User;
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

import java.util.regex.Pattern;

import com.syp.MainActivity;
import com.syp.R;

public class RegisterFragment extends Fragment {

    private MainActivity mainActivity;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private TextView error;
    private Button createAccount;
    private EditText gender;
    private View v;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_register, container, false);

        // Get Data
        firstName = v.findViewById(R.id.registerFirstName);
        email = v.findViewById(R.id.registerEmail);
        password = v.findViewById(R.id.registerPassword);
        createAccount = v.findViewById(R.id.registerCreateAccountButton);
        error = v.findViewById(R.id.registerError);
        gender = v.findViewById(R.id.registerGender);
        mainActivity = (MainActivity) getActivity();

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstNameString = firstName.getText().toString();
                String lastNameString = lastName.getText().toString();
                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();
                String genderString = gender.getText().toString();

                boolean firstNameInvalid = false;
                boolean firstNameContainsInvalid = false;
                boolean lastNameInvalid = false;
                boolean lastNameContainsInvalid = false;
                boolean emailInvalid = false;
                boolean passwordInvalid = false;

                if(firstNameString.trim().length()==0 || !onlyLetters(firstNameString)) {
                    firstName.setHintTextColor(Color.RED);
                    firstName.setHint("Invalid Display Name");
                    firstName.setText("");
                }
                if(!isEmailFormat(emailString) || emailString.trim().length() == 0) {
                    email.setHintTextColor(Color.RED);
                    email.setHint("Invalid Email");
                    email.setText("");
                }
                if(passwordString.trim().length()==0) {
                    password.setHintTextColor(Color.RED);
                    password.setHint("Invalid Email");
                    password.setText("");
                }
                if(genderString.trim().length()==0){
                    gender.setHintTextColor(Color.RED);
                    gender.setHint("Invalid Gender");
                    gender.setText("");
                }

                checkEmail();
            }
        });

        return v;
    }

    private void checkEmail(){
        DatabaseReference allUserRef = Singleton.get(mainActivity).getDatabase()
                .child(Singleton.firebaseUserTag);

        allUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    User user = child.getValue(User.class);
                    if(user.getEmail().equalsIgnoreCase(email.getText().toString())){
                        email.setHintTextColor(Color.RED);
                        email.setHint("Email in Use");
                        email.setText("");
                        return;
                    }
                }

                User u = new User();
                u.setDisplayName(firstName.getText().toString() + " " + lastName.getText().toString());
                u.setEmail(email.getText().toString());
                u.setPassword(email.getText().toString());
                u.setGender(gender.getText().toString());
                u.setMerchant(false);

                String userID = allUserRef.push().getKey();
                u.setId(userID);

                allUserRef.child(userID).setValue(u);

                Singleton.get(mainActivity).setUserId(userID);
                NavDirections action = RegisterFragmentDirections.actionRegisterFragmentToMapFragment();
                Navigation.findNavController(v).navigate(action);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean onlyLetters(String s){
        char[] chars = s.toCharArray();
        for(char c: chars){
            if(!Character.isLetter(c) && c != '-' && c != ' ')
                return false;
        }
        return true;
    }

    private boolean isEmailFormat(String s){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (s == null)
            return false;
        return pat.matcher(s).matches();
    }
}