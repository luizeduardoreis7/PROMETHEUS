package com.example.exemplo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private CalendarView calendarView;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        calendarView = findViewById(R.id.calendarView);
        requestQueue = Volley.newRequestQueue(this);

        // Registrar um listener para a data selecionada no CalendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                getData(selectedDate);
            }
        });
    }

    private void getData(Calendar selectedDate) {
        // Converter a data selecionada para o formato yyyy-MM-dd
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDateString = dateFormat.format(selectedDate.getTime());

        String URL = "https://epg-api.video.globo.com/programmes/1337?date=" + selectedDateString;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject programmeObject = response.getJSONObject("programme");
                    JSONArray entriesArray = programmeObject.getJSONArray("entries");

                    // Iterar sobre os objetos JSON na matriz "entries"
                    StringBuilder dataText = new StringBuilder();

                    for (int i = 0; i < entriesArray.length(); i++) {
                        JSONObject entryObject = entriesArray.getJSONObject(i);
                        JSONObject programObject = entryObject.getJSONObject("program");

                        // Agora, obtenha a URL da logo dentro do objeto custom_info -> Graficos -> LogoURL
                        JSONObject customInfoObject = entryObject.getJSONObject("custom_info");
                        JSONObject graphicsObject = customInfoObject.getJSONObject("Graficos");
                        String logoURL = graphicsObject.getString("LogoURL");


                        // Obter horários de início e término
                        String startTime = formatTime(entryObject.getString("start_time"));


                        // Agora, obtenha o nome do programa dentro do objeto program
                        String programName = programObject.getString("name");

                        // Construir a string a ser exibida
                        String entryText = logoURL + startTime + " - " + programName + "\n";

                        // Adicionar a entrada ao texto geral
                        dataText.append(entryText);

                    }

                    textView.setText(dataText.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private String formatTime(String originalTime) {
        try {
            // Converte o timestamp em milissegundos para uma representação de data
            long timestamp = Long.parseLong(originalTime);
            Date date = new Date(timestamp * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return sdf.format(date);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return originalTime; // Retorna o valor original se houver um erro de formato
        }
    }
}