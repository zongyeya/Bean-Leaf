package com.syp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.syp.LoginActivity;
import com.syp.MainActivity;
import com.syp.R;

public class LogoutFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate View with logout fragment
        View v = inflater.inflate(R.layout.fragment_logout, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();

        FirebaseAuth.getInstance().signOut();
        Toast.makeText(mainActivity, "User logged out", Toast.LENGTH_SHORT).show();

        NavDirections action = LogoutFragmentDirections.actionLogoutFragmentToMapFragment();
        Navigation.findNavController(mainActivity, R.id.nav_host_fragment).navigate(action);

        Intent intToLogin = new Intent(mainActivity, LoginActivity.class);
        startActivity(intToLogin);

        return v;
    }
}
