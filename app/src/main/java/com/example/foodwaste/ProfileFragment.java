package com.example.foodwaste;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Find the login and signup buttons
        Button login = view.findViewById(R.id.btnLOGIN);
        Button signup = view.findViewById(R.id.btnSIGNUP);

        // Set the click listener for the login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and show the CustomLogin dialog
                DialogFragment customLogin = new CustomLogin();
                customLogin.show(getParentFragmentManager(), "CustomLogin");
            }
        });

        // Set the click listener for the signup button
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start the SignUp activity
                Intent intent = new Intent(getActivity(), SignUp.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
