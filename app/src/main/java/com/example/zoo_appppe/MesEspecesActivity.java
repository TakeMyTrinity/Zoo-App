package com.example.zoo_appppe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MesEspecesActivity extends AppCompatActivity {

    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_especes);

        Intent intent = getIntent();
        String matricule = intent.getStringExtra("matricule");

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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // response in json array
                            JSONArray jsonArray = new JSONArray(responseBody);

                            // lire le json
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    // get espece nom
                                    String especeNom = jsonObject.getString("espece_nom");

                                    // identifier le TextView correspondant
                                    int textViewId = getResources().getIdentifier("species_name_textview_" + (i + 1), "id", getPackageName());
                                    TextView textView = findViewById(textViewId);

                                    // affecter la valeur
                                    textView.setText(especeNom);

                                    // identifier l'ImageView correspondante
                                    int imageViewId = getResources().getIdentifier("species_imageview_" + (i + 1), "id", getPackageName());
                                    ImageView imageView = findViewById(imageViewId);

                                    // Choisir l'image en fonction de l'espèce
                                    switch (especeNom.toLowerCase()) {
                                        case "lion":
                                            imageView.setImageResource(R.drawable.lion);
                                            break;
                                        case "serpent":
                                            imageView.setImageResource(R.drawable.serpend);
                                            break;
                                        case "elephant":
                                            imageView.setImageResource(R.drawable.elephant);
                                            break;
                                        case "tigre":
                                            imageView.setImageResource(R.drawable.tigre);
                                            break;
                                        default:
                                            // Afficher une image par défaut si l'espèce n'est pas reconnue
                                            imageView.setImageResource(R.drawable.image_default);
                                            break;
                                    }

                                    // ajouter un onClickListener sur la textView
                                    textView.setOnClickListener(v -> {
                                        Intent intent1 = new Intent(MesEspecesActivity.this, DetailsEspeceActivity.class);
                                        try {
                                            intent1.putExtra("id_espece", jsonObject.getString("id_espece"));
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                        startActivity(intent1);
                                    });

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }
}