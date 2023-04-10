package com.example.zoo_appppe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zoo_appppe.DBLite.DBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements Serializable {

    EditText username;
    EditText password;
    Button login;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DBHelper instance
        dbHelper = new DBHelper(this);

        login = findViewById(R.id.login);

        login.setOnClickListener(v -> {
            username = findViewById(R.id.username);
            password = findViewById(R.id.password);
            String user = username.getText().toString();
            String pass = password.getText().toString();

            if (user.isEmpty() && pass.isEmpty()) {
                username.setError("Please enter username");
                password.setError("Please enter password");
            } else {
                // json REQUEST
                JSONObject json = new JSONObject();
                try {
                    json.put("identifiant", user);
                    json.put("password", pass);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // request
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                // Create client
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, json.toString());
                // Create request
                Request request = new Request.Builder()
                        .url("https://gr14.sio-cholet.fr/api-zoo/login/check.php")
                        .post(body)
                        .build();

                // callback
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                "Erreur de connexion au serveur", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String responseBody = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody);
                            // get message from json
                            String message = jsonObject.getString("message");
                            // get soignant from json
                            JSONObject soignantJson = jsonObject.getJSONObject("soignant");
                            // create a Bundle object
                            Bundle bundle = new Bundle();
                            bundle.putString("matricule", soignantJson.getString("matricule"));
                            bundle.putString("nom", soignantJson.getString("nom"));
                            bundle.putString("prenom", soignantJson.getString("prenom"));
                            bundle.putString("adresse", soignantJson.getString("adresse"));
                            bundle.putString("telephone", soignantJson.getString("telephone"));
                            bundle.putString("identifiant", soignantJson.getString("identifiant"));
                            bundle.putString("password", soignantJson.getString("password"));
                            bundle.putString("image", soignantJson.getString("image"));
                            bundle.putString("isAdmin", soignantJson.getString("isAdmin"));

                            // check if login is successful
                            if (message.equals("Login successful.")) {
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();

                                values.put(DBHelper.COLUMN_USER, user);
                                values.put(DBHelper.COLUMN_MATRICULE, soignantJson.getString("matricule"));
                                values.put(DBHelper.COLUMN_NOM, soignantJson.getString("nom"));
                                values.put(DBHelper.COLUMN_PRENOM, soignantJson.getString("prenom"));
                                values.put(DBHelper.COLUMN_ADRESSE, soignantJson.getString("adresse"));
                                values.put(DBHelper.COLUMN_TELEPHONE, soignantJson.getString("telephone"));
                                values.put(DBHelper.COLUMN_IDENTIFIANT, soignantJson.getString("identifiant"));
                                values.put(DBHelper.COLUMN_PASSWORD, soignantJson.getString("password"));
                                values.put(DBHelper.COLUMN_IMAGE, soignantJson.getString("image"));
                                values.put(DBHelper.COLUMN_ISADMIN, soignantJson.getString("isAdmin"));
                                db.insert(DBHelper.TABLE_NAME, null, values);
                                db.close();

                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                intent.putExtra("user", user);
                                intent.putExtra("soignant", bundle);
                                startActivity(intent);
                            } else {
                                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                        "Identifiant ou mot de passe incorrect", Toast.LENGTH_SHORT).show());
                            }
                            // catch error
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}