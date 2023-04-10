package com.example.zoo_appppe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    Button logout;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // get bundle
        Bundle bundle = getIntent().getBundleExtra("soignant");

        // get login + set text on the view
        String login = bundle.getString("identifiant");

        TextView usernameView = findViewById(R.id.user_name);

        usernameView.setText(login);

        // get matricule + set text on the view
        String matricule = bundle.getString("matricule");

        TextView matriculeView = findViewById(R.id.user_matricule);

        matriculeView.setText("Votre matricule est le : " + matricule + ".");

        // get image + set image on the view
        String image = bundle.getString("image");

        ImageView imageView = findViewById(R.id.profile_image);

        Picasso.get().load(image).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                // L'image a été chargée avec succès
                imageView.setVisibility(ImageView.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                // Une erreur s'est produite lors du chargement de l'image
                Toast.makeText(HomeActivity.this, "Erreur lors du chargement de " +
                        "l'image", Toast.LENGTH_SHORT).show();

                // Utiliser une image de remplacement
                imageView.setImageResource(R.drawable.placeholder);
            }
        });

        // mes espèces button
        Button mesEspeces = findViewById(R.id.btn_mes_especes);

        mesEspeces.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MesEspecesActivity.class);
            intent.putExtra("matricule", matricule);
            startActivity(intent);
        });

        // Button logout
        logout = findViewById(R.id.btn_logout);

        logout.setOnClickListener(v -> {
            // go back to login page
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

}