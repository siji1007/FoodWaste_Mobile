package com.example.foodwaste;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class NetworkUtils {

    private static final String BASE_URL = "http://192.168.100.198:5000/"; // Replace with your server address
    //private static final String BASE_URL = "http://192:168:100:198:5000";

    public interface DataCallback {
        void onSuccess(JSONArray data);
        void onFailure(Exception e);
    }

    public static void fetchData(String endpoint, final DataCallback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    try {
                        JSONArray data = new JSONArray(jsonResponse);
                        callback.onSuccess(data);
                    } catch (JSONException e) {
                        callback.onFailure(e);
                    }
                } else {
                    callback.onFailure(new IOException("Unexpected code " + response));
                }
            }
        });
    }
}
