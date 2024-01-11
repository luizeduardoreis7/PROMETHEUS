package com.example.programa;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private CalendarView calendarView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList;
    private AppDatabase appDatabase;
    private TvProgramDao tvProgramDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        calendarView = findViewById(R.id.calendarView);
        dataList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-database").build();
        tvProgramDao = appDatabase.tvProgramDao();

        // Registrar um listener para a data selecionada no CalendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);

                // Limpar a lista existente antes de buscar os novos dados
                dataList.clear();
                adapter.notifyDataSetChanged();

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
                        try {
                            JSONObject entryObject = entriesArray.getJSONObject(i);
                            JSONObject programObject = entryObject.getJSONObject("program");

                            String startTime = formatTime(entryObject.getString("start_time"));
                            String programName = programObject.getString("name");

                            // Crie uma instância da sua entidade e insira no banco de dados usando o DAO
                            TvProgram tvProgram = new TvProgram();
                            tvProgram.startTime = startTime;  // Substitua pelos dados reais
                            tvProgram.programName = programName;  // Substitua pelos dados reais

                            tvProgramDao.insert(tvProgram);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // Buscando os dados do banco de dados usando o DAO.
                    List<TvProgram> tvPrograms = tvProgramDao.getTvPrograms(selectedDateString);

                    // Atualizar a lista no seu ListView
                    for (TvProgram tvProgram : tvPrograms) {
                        String entryText = tvProgram.startTime + " - " + tvProgram.programName + "\n";
                        dataList.add(entryText);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Lida com erros de requisição
                error.printStackTrace();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private String formatTime(String originalTime) {
        try {
            long timestamp = Long.parseLong(originalTime);
            Date date = new Date(timestamp * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return sdf.format(date);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return originalTime;
        }
    }
}