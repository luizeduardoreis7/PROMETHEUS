package com.example.exercicio15;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        dataList = new ArrayList<>();

        fetchDataFromApi();
    }

    private void fetchDataFromApi() {
        String apiUrl = "https://658b520eba789a962238a813.mockapi.io/users";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                apiUrl,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseApiResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Erro ao obter dados da API", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void parseApiResponse(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                String nome = response.getJSONObject(i).getString("nome");
                int numero = response.getJSONObject(i).getInt("numero");
                String id = response.getJSONObject(i).getString("id");

                String info = "ID: " + id + ", Nome: " + nome + ", Número: " + numero;
                dataList.add(info);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Preencher o TextView (ou ListView, se preferir voltar a usá-lo)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
    }
}
