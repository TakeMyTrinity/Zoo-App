package com.example.zoo_appppe;


import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlacementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement);

        Intent intent = getIntent();
        String matricule = intent.getStringExtra("matricule");

        // get species names in spinner
        this.getSpeciesNames(matricule);

        // get enclos names in spinner
        this.getEnclosNames();
    }

    private void getSpeciesNames(String matricule) {
        // Create client
        OkHttpClient client = new OkHttpClient();

        // create request
        Request request = new Request.Builder()
                .url("https://gr14.sio-cholet.fr/api-zoo/soignants/especes.php?matricule=" + matricule)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String responseBody = response.body().string();

                runOnUiThread(() -> {
                    try {
                        // response in json array
                        JSONArray jsonArray = new JSONArray(responseBody);

                        // create map of species names to IDs
                        Map<String, String> speciesIdMap = new HashMap<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                // get species name and ID
                                String speciesName = jsonObject.getString("espece_nom");
                                String speciesId = jsonObject.getString("id_espece");
                                // add to map
                                speciesIdMap.put(speciesName, speciesId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // populate spinner with species names
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(PlacementActivity.this, android.R.layout.simple_spinner_item, new ArrayList<>(speciesIdMap.keySet()));
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Spinner spinner = findViewById(R.id.spinner1);
                        spinner.setAdapter(spinnerArrayAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    // méthod to get enclos names in spinner 2
    private void getEnclosNames() {
        // Create client
        OkHttpClient client = new OkHttpClient();

        // create request
        Request request = new Request.Builder()
                .url("https://gr14.sio-cholet.fr/api-zoo/enclos/read.php")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String responseBody = response.body().string();

                runOnUiThread(() -> {
                    try {
                        // response in json object
                        JSONObject jsonObject = new JSONObject(responseBody);

                        // get records array
                        JSONArray jsonArray = jsonObject.getJSONArray("records");

                        // create list of enclos names and IDs
                        List<String> enclosNamesList = new ArrayList<>();
                        Map<String, String> enclosIdMap = new HashMap<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject enclosObject = jsonArray.getJSONObject(i);
                            // get enclos name
                            String enclosName = enclosObject.getString("nom");
                            // get enclos ID
                            String enclosId = enclosObject.getString("id");
                            // add name and ID to lists
                            enclosNamesList.add(enclosName);
                            enclosIdMap.put(enclosName, enclosId);
                        }

                        // populate spinner with enclos names
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(PlacementActivity.this, android.R.layout.simple_spinner_item, enclosNamesList);
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Spinner spinner = findViewById(R.id.spinner2);
                        spinner.setAdapter(spinnerArrayAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

}