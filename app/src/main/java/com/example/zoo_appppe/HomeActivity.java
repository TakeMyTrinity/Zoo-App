package com.example.zoo_appppe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    private Button logoutButton;
    private TextView userNameTextView;
    private TextView userMatriculeTextView;
    private ImageView profileImageView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // get bundle
        Bundle bundle = getIntent().getBundleExtra("soignant");

        // get login + set text on the view
        String login = bundle.getString("identifiant");

        userNameTextView = findViewById(R.id.user_name);
        userNameTextView.setText(login);

        // get matricule + set text on the view
        String matricule = bundle.getString("matricule");

        userMatriculeTextView = findViewById(R.id.user_matricule);
        userMatriculeTextView.setText("Votre matricule est le : " + matricule + ".");

        // get image + set image on the view
        String image = bundle.getString("image");

        profileImageView = findViewById(R.id.profile_image);

        Picasso.get().load(image).into(profileImageView, new Callback() {
            @Override
            public void onSuccess() {
                // L'image a été chargée avec succès
                profileImageView.setVisibility(ImageView.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                // Une erreur s'est produite lors du chargement de l'image
                Toast.makeText(HomeActivity.this, "Erreur lors du chargement de " +
                        "l'image", Toast.LENGTH_SHORT).show();

                // Utiliser une image de remplacement
                profileImageView.setImageResource(R.drawable.placeholder);
            }
        });

        // mes espèces button
        Button mesEspecesButton = findViewById(R.id.btn_mes_especes);
        mesEspecesButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MesEspecesActivity.class);
            intent.putExtra("matricule", matricule);
            startActivity(intent);
        });

        // Button logout
        logoutButton = findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(v -> {
            // go back to login page
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister views to prevent memory leaks
        userNameTextView = null;
        userMatriculeTextView = null;
        profileImageView = null;
        logoutButton = null;
    }
}