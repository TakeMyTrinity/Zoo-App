package com.example.zoo_appppe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetailsEspeceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_espece);

        // Récupération de l'intent qui a lancé l'activité
        Intent intent = getIntent();

        // Récupération de la chaîne de caractères passée avec putExtra()
        String idEspece = intent.getStringExtra("id_espece");

    }
}