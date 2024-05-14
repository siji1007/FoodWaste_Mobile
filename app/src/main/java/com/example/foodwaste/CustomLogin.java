package com.example.foodwaste;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class CustomLogin extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_login, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        view.setBackgroundColor(Color.parseColor("#0A443D"));
        builder.setView(view);

        EditText Username = view.findViewById(R.id.Username);
        EditText Password = view.findViewById(R.id.Password);

        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String username = Username.getText().toString();
                String password = Password.getText().toString();
                Toast.makeText(getActivity(), "Username: " + username + ", Password: " + password, Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }
}
