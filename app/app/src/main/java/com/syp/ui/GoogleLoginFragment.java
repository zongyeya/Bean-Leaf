package com.syp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.maps.errors.ApiException;
import com.syp.MainActivity;
import com.syp.R;

public class GoogleLoginFragment extends Fragment {
    static final int GOOGLE_SIGN = 12345;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    Button manualLoginButton;
    private MainActivity mainActivity;

    Button signInButton;
    TextView text;
    ImageView image;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_google_login, container, false);
        mainActivity = (MainActivity) getActivity();

        signInButton = (Button) v.findViewById(R.id.signInButton);
        text = (TextView) v.findViewById(R.id.appName);
        image = (ImageView) v.findViewById(R.id.sypLogo);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
//        manualLoginButton = (Button) v.findViewById(R.id.manualLogin);
        setManualLoginButtonOnClick();

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(mainActivity, googleSignInOptions);

        signInButton.setOnClickListener(view -> SignInGoogle());

        if (mAuth.getCurrentUser() != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            updateUI(user);
        }


        return v;
    }

    void SignInGoogle() {
        progressBar.setVisibility(View.VISIBLE);
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, GOOGLE_SIGN);
    }

    void setManualLoginButtonOnClick(){
        Log.d("Moving", "Aasdf");

        manualLoginButton.setOnClickListener((View v) ->{
            Log.d("Moving", "Aasdf");

            NavDirections action = GoogleLoginFragmentDirections.actionGoogleLoginFragmentToLoginFragment();
            Navigation.findNavController(v).navigate(action);
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN) {
            if(resultCode == Activity.RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn
                        .getSignedInAccountFromIntent(data);

                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null) {
                        firebaseAuthWithGoogle(account);
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            } else if(resultCode == Activity.RESULT_CANCELED) {
                Log.d("GOOGLE", "GOOGLE");
            }

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("Tag", "firebaseAuthWithGoogle: " + account.getId());

        AuthCredential credential = GoogleAuthProvider
                .getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mainActivity, task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);

                        Log.d("Tag", "signin success");
                        FirebaseUser user = mAuth.getCurrentUser();
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);

                        Log.d("Tag", "signin failure");
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            String photo = String.valueOf(user.getPhotoUrl());

            Log.d("name", name);

            signInButton.setVisibility(View.INVISIBLE);
//            logoutButton.setVisibility(View.VISIBLE);
        } else {
            signInButton.setVisibility(View.VISIBLE);
//            logoutButton.setVisibility(View.INVISIBLE);
        }
    }

    void Logout() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(mainActivity,
                task -> updateUI(null));
    }
}
