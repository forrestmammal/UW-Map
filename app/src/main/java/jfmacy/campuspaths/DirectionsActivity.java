package jfmacy.campuspaths;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import hw7.Path;

/** this activity displays a text version of the directions to get from one building
 * to another.
 *
 * Created by John Forrest Macy on 8/16/2017.
 */

public class DirectionsActivity extends AppCompatActivity {

    // this is not an ADT

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // for receiving route info from caller
        Bundle b = this.getIntent().getExtras();
        String[] directions = b.getStringArray("directions");

        setContentView (R.layout.activity_directions);

        // button to return to previous activity
        Button backButton = (Button) findViewById(R.id.BackButton);
        backButton.setOnClickListener(backButtonClick);

        // holds the direcitons
        ListView directionsList = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> theAdapter = new ArrayAdapter<String> (
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                new ArrayList<String>()
        );
        directionsList.setAdapter(theAdapter);

        // add directions to list view
        for (String s : directions) {
            theAdapter.add(s);
        }
    }

    // ends activity to return to previous
    private View.OnClickListener backButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
            finish();
        }
    };
}
