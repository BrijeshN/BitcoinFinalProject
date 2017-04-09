package edu.temple.bitcoinfinalproject;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class ApiUrl extends AsyncTask<Void,Void,String> {

    // Strng to store api url
    String apiLink;

    public ApiUrl(String apiLink) {
        this.apiLink = apiLink;

    }

    @Override
    protected String doInBackground(Void... params) {

        String content = "";
        URL link = null;


        try {
            link = new URL(apiLink);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            link.openStream()));

            String tmpResponse = "";

            tmpResponse = reader.readLine();
            while (tmpResponse != null) {
                content = content + tmpResponse;
                tmpResponse = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;
    }


}

