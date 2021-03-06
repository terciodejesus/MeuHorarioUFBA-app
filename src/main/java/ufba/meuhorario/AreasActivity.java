package ufba.meuhorario;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ufba.meuhorario.adapter.ListAreasAdapter;
import ufba.meuhorario.dao.AreaDAO;
import ufba.meuhorario.model.Area;

public class AreasActivity extends AppCompatActivity {

    private String meuHorarioJson = "https://gist.githubusercontent.com/dnovaes/b0f2f75a18831b024ab85a5c25a1b2fe/raw/37b1c40209c2519a93bf89829ae12c68d2ee995e/meuhorario_areas.json";
    private String jsonArrayName =  "areas";
    private ListView areasList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_areas);

        areasList = (ListView) findViewById(R.id.list_areas);

        areasList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> list, View view, int position, long id) {
                    Area area = (Area) list.getItemAtPosition(position);
                    //Toast.makeText(AreasActivity.this, "você clicou em "+area.getName(), Toast.LENGTH_SHORT).show();

                    Intent intentCourses = new Intent(AreasActivity.this.getApplicationContext(), CoursesActivity.class);
                    intentCourses.putExtra("area", area);
                    startActivity(intentCourses);
                }
            }
        );
        //check if there is jsonData on the database(sqlite)
        checkJsonData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.json_download_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.jsonDwnld:
                // Creating a anonymous JsonParser instance
                // download json and insert into SQLite
                //TODO: (MAYBE) trade third parameter later so JsonParser can update the whole database and not just 'areas'
                checkJsonData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadList();
    }

    public void loadList() {
        AreaDAO dao = new AreaDAO(this);

        //Select all the areas on the SQLite and form a List<Area>
        List<Area> areas = dao.getListAreas();
        dao.close();

        ListAreasAdapter adapter = new ListAreasAdapter(areas, this);
        areasList.setAdapter(adapter);
    }

    public void checkJsonData() {
        AreaDAO dao = new AreaDAO(this);

        //Select all the areas on the SQLite and form a List<Area>
        List<Area> areas = dao.getListAreas();

        if (areas.isEmpty()) {
            // Creating a anonymous JsonParser instance
            // download json and insert into SQLite
            new JSONParser(this, meuHorarioJson, jsonArrayName).execute();
            //reloadList
            loadList();
        }
        dao.close();
    }

}
