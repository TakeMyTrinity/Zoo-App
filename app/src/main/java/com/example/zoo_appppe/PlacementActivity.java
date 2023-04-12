package com.example.zoo_appppe;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PlacementActivity extends AppCompatActivity {

    private OkHttpClient client;
    private String selectedSpeciesId;
    private String selectedEnclosId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement);

        Intent intent = getIntent();
        String matricule = intent.getStringExtra("matricule");

        // Create client
        client = new OkHttpClient();

        // create request for species
        Request speciesRequest = new Request.Builder()
                .url("https://gr14.sio-cholet.fr/api-zoo/soignants/especes.php?matricule=" + matricule)
                .build();

        // make species request
        client.newCall(speciesRequest).enqueue(new Callback() {
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
                    runOnUiThread(() -> {
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(PlacementActivity.this, android.R.layout.simple_spinner_item, new ArrayList<>(speciesIdMap.keySet()));
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Spinner spinner = findViewById(R.id.spinner1);
                        spinner.setAdapter(spinnerArrayAdapter);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selectedSpeciesId = speciesIdMap.get(parent.getItemAtPosition(position).toString());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                selectedSpeciesId = null;
                            }
                        });
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // create request for enclos
        Request enclosRequest = new Request.Builder()
                .url("https://gr14.sio-cholet.fr/api-zoo/enclos/read.php")
                .build();

        // make enclos request
        client.newCall(enclosRequest).enqueue(new Callback() {
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

                try {
                    // response in json object
                    JSONObject jsonObject = new JSONObject(responseBody);

                    // get records array
                    JSONArray jsonArray = jsonObject.getJSONArray("records");

                    // create list of enclos
                    List<Enclos> enclosList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject enclosObject = jsonArray.getJSONObject(i);
                        // get enclos name and ID
                        String enclosName = enclosObject.getString("nom");
                        String enclosId = enclosObject.getString("id");
                        // create Enclos object and add to list
                        Enclos enclos = new Enclos(enclosName, enclosId);
                        enclosList.add(enclos);
                    }

                    // populate spinner with enclos names
                    runOnUiThread(() -> {
                        ArrayAdapter<Enclos> spinnerArrayAdapter = new ArrayAdapter<>(PlacementActivity.this, android.R.layout.simple_spinner_item, enclosList);
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Spinner spinner = findViewById(R.id.spinner2);
                        spinner.setAdapter(spinnerArrayAdapter);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                                selectedEnclosId = ((Enclos) parent.getItemAtPosition(position)).getId();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                selectedEnclosId = null;
                            }
                        });
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // set up placement button
        Button placementButton = findViewById(R.id.valider_button);
        placementButton.setOnClickListener(v -> {
            if (selectedSpeciesId == null || selectedEnclosId == null) {
                Toast.makeText(PlacementActivity.this, "Please select a species and an enclosure", Toast.LENGTH_SHORT).show();
                return;
            }

            // create request body with enclos ID
            String json = "{\"id_enclos\": \"" + selectedEnclosId + "\"}";
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

            // create request
            Request request = new Request.Builder()
                    .url("https://gr14.sio-cholet.fr/api-zoo/emplacement/update.php?id_espece=" + selectedSpeciesId)
                    .post(requestBody)
                    .build();

            // make request
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
                        Toast.makeText(PlacementActivity.this, "Placement updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            });
        });

    }

    // Enclos class to store enclos information
    private static class Enclos {
        private String name;
        private String id;

        public Enclos(String name, String id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}