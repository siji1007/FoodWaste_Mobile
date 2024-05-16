package com.example.foodwaste;

import static com.google.common.reflect.Reflection.getPackageName;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment {

    private TextView newsOneTitle, newsTwoTitle, newsThreeTitle;
    private String[] newsDescriptions;
    private String newsOneLink, newsTwoLink, newsThreeLink;

    private ImageView NewsOneImage, NewsTwoImage, NewsThreeImage;
    private VideoView videoPlay;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        videoPlay = view.findViewById(R.id.videoView);

        newsOneTitle = view.findViewById(R.id.FirstNewsTitle);
        newsTwoTitle = view.findViewById(R.id.SecondNewsTitle);
        newsThreeTitle = view.findViewById(R.id.ThirdNewsTitle);

        NewsOneImage = view.findViewById(R.id.ImageNewsOne);
        NewsTwoImage = view.findViewById(R.id.ImageNewsTwo);
        NewsThreeImage = view.findViewById(R.id.ImageNewsThree);



        // Initialize the news descriptions array
        newsDescriptions = new String[3];

        // Fetch and display the latest food waste news
        fetchAndDisplayLatestFoodWasteNews();

        // Set click listeners for each news title
        newsOneTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNewsDescription(newsOneTitle.getText().toString());
            }
        });

        newsTwoTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNewsDescription(newsTwoTitle.getText().toString());
            }
        });

        newsThreeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNewsDescription(newsThreeTitle.getText().toString());
            }
        });
        playVideo();


        return view;
    }

    private void playVideo() {
        String videoPath = "android.resource://" + requireContext().getPackageName() + "/" + R.raw.advertisement;
        videoPlay.setVideoURI(Uri.parse(videoPath));
        videoPlay.setOnPreparedListener(mp -> mp.setVolume(0f, 0f));
        videoPlay.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Restart the video playback
                videoPlay.start();
            }
        });
        videoPlay.start();
    }





    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void fetchAndDisplayLatestFoodWasteNews() {
        // Create a GenerativeModel instance
        GenerativeModel gm = new GenerativeModel("gemini-pro", "AIzaSyCFt0DlfKQH3lcaJ1UARInaWh6H-ma3cxk");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        // Generate content related to food waste news in the Philippines
        ListenableFuture<GenerateContentResponse> response = model.generateContent(
                new Content.Builder()
                        .addText("top 3 news about food waste in the Camarines Norte Philippines. Sort by number")
                        .build());

        // Handle the response
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    // Split the result into separate lines
                    String[] lines = result.getText().split("\\.\\s*\\*\\*");

                    // Ensure we have at least 3 news items
                    if (lines.length >= 3) {
                        // Extract news titles, descriptions, and image URLs from the lines
                        String[] newsTitles = new String[3];
                        String[] newsDescriptions = new String[3];
                        String[] newsLinks = new String[3];

                        for (int i = 1; i <= 3; i++) {
                            newsTitles[i - 1] = extractNewsTitle(lines[i]);
                            newsDescriptions[i - 1] = extractNewsDescription(lines[i]);
                            newsLinks[i - 1] = extractLink(lines[i]);
                        }

                        // Update the news titles in TextViews
                        setNonBlankText(newsOneTitle, newsTitles[0]);
                        setNonBlankText(newsTwoTitle, newsTitles[1]);
                        setNonBlankText(newsThreeTitle, newsTitles[2]);

                        // Set click listeners for image views
                        NewsOneImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handleImageClick(0, newsLinks[0], newsDescriptions[0]);
                            }
                        });

                        NewsTwoImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handleImageClick(1, newsLinks[1], newsDescriptions[1]);
                            }
                        });

                        NewsThreeImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handleImageClick(2, newsLinks[2], newsDescriptions[2]);
                            }
                        });
                    }
                }

                private void handleImageClick(int index, String link, String description) {
                    if (!TextUtils.isEmpty(link)) {
                        showToast(link);
                    } else {
                        showDialog("News Description", description);
                    }
                }

                private String extractLink(String line) {
                    String link = "";
                    // Define a regular expression pattern to match links
                    Pattern pattern = Pattern.compile("https?://\\S+\\b");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        link = matcher.group();
                    }
                    return link;
                }

                // Helper method to extract news title from a line
                private String extractNewsTitle(String line) {
                    // Remove leading index and any surrounding spaces
                    line = line.replaceAll("^\\d+\\.\\s*", "").trim();
                    // Remove surrounding "**"
                    line = line.replaceAll("\\*\\*(.*?)\\*\\*", "$1").trim();
                    return line;
                }

                // Helper method to extract news description from a line
                private String extractNewsDescription(String line) {
                    // Remove surrounding "**" and any leading or trailing spaces
                    line = line.replaceAll("^\\d+\\.\\s*\\*\\*(.*?)\\*\\*", "$1").trim();
                    return line;
                }

                @Override
                public void onFailure(Throwable t) {
                    // Display error message in case of failure
                    showDialog("Error", "Failed to fetch latest news: " + t.getMessage());
                }
            }, requireActivity().getMainExecutor());
        }
    }

    private void setNonBlankText(TextView textView, String text) {
        if (!TextUtils.isEmpty(text) && !text.trim().isEmpty()) {
            textView.setText(text);
        }
    }

    private void displayNewsDescription(String title) {
        // Find the index of the clicked title
        int index = -1;
        for (int i = 0; i < newsDescriptions.length; i++) {
            if (!TextUtils.isEmpty(newsDescriptions[i]) && newsDescriptions[i].equals(title)) {
                index = i;
                break;
            }
        }

        // If the title is found, display its description in a dialog
        if (index != -1) {
            showDialog("News Description", newsDescriptions[index]);
        }
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the dialog
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
