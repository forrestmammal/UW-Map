package jfmacy.campuspaths;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import hw8.*;

public class MainActivity extends AppCompatActivity {

    DrawView view;
    ListView buildingList;
    private String buttonOut = "empty";
    private MapData mapData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // buttons for different commands/modes
        Button routeButton = (Button) findViewById(R.id.RouteButton);
        routeButton.setOnClickListener(routeButtonClick);

    }

    // launch to find route around campus with standard map and list of directions
    private View.OnClickListener routeButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, RouteActivity.class));
        }
    };

}
