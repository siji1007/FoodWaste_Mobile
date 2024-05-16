package com.example.foodwaste;

import android.annotation.SuppressLint; //here
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CustomLogin extends DialogFragment {
    private EditText usernameView;
    private EditText passwordView;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (context == null) {
            // If context is null, use the application context as fallback
            context = getActivity().getApplicationContext();
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_login, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        view.setBackgroundColor(Color.parseColor("#0A443D"));
        builder.setView(view);

        usernameView = view.findViewById(R.id.Username);
        passwordView = view.findViewById(R.id.Password);

        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                submit(view);
            }
        });

        return builder.create();
    }

    public void submit(View v) {
        String username = usernameView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();

        if (username.length() == 0 || password.length() == 0) {
            Toast.makeText(getActivity(), "Something is wrong. Please check your inputs.", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject loginForm = new JSONObject();
        try {
            loginForm.put("subject", "login");
            loginForm.put("username", username);
            loginForm.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(loginForm.toString(), MediaType.parse("application/json; charset=utf-8"));
        postRequest(MainActivity.postUrl, body);
    }

    public void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                Log.d("FAIL", e.getMessage());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"Failed to Connect to Server. Please Try Again.", Toast.LENGTH_LONG).show();
                    }
                });
            }


            @SuppressLint("StaticFieldLeak")
            public void onResponse(Call call, final Response response) throws IOException {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        try {
                            return response.body().string().trim();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(String loginResponseString) {
                        super.onPostExecute(loginResponseString);
                        if(getActivity() != null){
                            if (loginResponseString != null) {
                                Log.d("LOGIN", "Response from the server : " + loginResponseString);
                                if (loginResponseString.equals("success")) {
                                    Toast.makeText(getActivity(), "Login Success.", Toast.LENGTH_LONG).show();
                                    // Handle successful login (e.g., start new activity, dismiss dialog, etc.)
                                } else if (loginResponseString.equals("failure")) {
                                    Toast.makeText(getActivity(), "Login Failed. Invalid username or password.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Something went wrong. Please try again later.", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Log.d("LOGIN", "Login Failed. Invalid username or password.");
                        }
                    }
                }.execute();
            }



        });
    }
}
