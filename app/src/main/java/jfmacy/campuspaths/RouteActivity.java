package jfmacy.campuspaths;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import hw5.Node;
import hw7.Path;
import hw8.Building;
import hw8.MapData;
import hw8.MapElement;
import hw8.TextView;

/**Aids in finding the shortest path between buildings on campus
 *
 * Created by John Forrest Macy on 8/13/2017.
 */

public class RouteActivity extends AppCompatActivity {

    private MapData mapData;
    ListView sourceList;
    ListView destList;
    Building sourceBuilding;
    Building destBuilding;
    Path shortPath = null;
    DrawView drawView;
    private final int MAP_WIDTH = 4330;
    private final int MAP_HEIGHT = 2964;
    int xShift;
    int yShift;
    float scale;



    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_route);

        // load the map data
        InputStream pathInputStream = this.getResources().openRawResource(R.raw.campus_paths);
        InputStream buildingInputStream = this.getResources().openRawResource(R.raw.campus_buildings);
        try {
            mapData = new MapData(pathInputStream, buildingInputStream, new File(""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // buttons
        Button mainMenuButton = (Button) findViewById(R.id.MainMenuButton);
        mainMenuButton.setOnClickListener(mainMenuButtonClick);

        Button listButton = (Button) findViewById(R.id.listButton);
        listButton.setOnClickListener(listButtonClick);

        Button resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(resetButtonClick);

        // map display
        drawView = (DrawView) findViewById(R.id.routeMapView);

        // building lists
        sourceList = (ListView) findViewById(R.id.sourceListView);
        sourceList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        destList = (ListView) findViewById(R.id.destListView);
        destList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        populateBuildings(sourceList);
        populateBuildings(destList);

        sourceList.setOnItemClickListener(sourceItemClick);
        destList.setOnItemClickListener(destItemClick);
    }


    /**
     * returns the user to the main menu
     */
    private View.OnClickListener mainMenuButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
            finish();
        }
    };

    private View.OnClickListener listButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
            if (shortPath != null) {
                // directions to pass to view
                String[] directions;

                if (!sourceBuilding.equals(destBuilding)) { // non zero path
                    LinkedList<Node<MapElement>> theNodes = shortPath.getNodeList();
                    LinkedList<Double> theCosts = shortPath.getCostList();
                    directions = new String[shortPath.getCostList().size() + 1];

                    // clear first cost to self
                    theCosts.poll();
                    Node<MapElement> sourceNode = theNodes.poll();
                    directions[0] = "Start from " + sourceBuilding.getShort();
                    int i = 1;
                    while (!theCosts.isEmpty()) {
                        Node<MapElement> destNode = theNodes.poll();

                        Double deltaX = destNode.getData().getX() -
                                sourceNode.getData().getX();
                        Double deltaY = sourceNode.getData().getY() -
                                destNode.getData().getY();

                        String direction = TextView.convertThetaToDirection(Math.atan2(deltaY, deltaX));
                        directions[i] = "Walk " + Math.round(theCosts.poll()) + " feet " + direction;

                        sourceNode = destNode;

                        i++;
                    }
                    directions[i] = "You walked " + Math.round(shortPath.getTotalCost()) + " feet to " + destBuilding.getShort();
                } else { // zero path
                    directions = new String[1];
                    directions[0] = "You're already in " + destBuilding.getShort();
                }

                Intent theIntent = new Intent(RouteActivity.this, DirectionsActivity.class);
                theIntent.putExtra("directions", directions);
                startActivity(theIntent);
            }
        }
    };

    private View.OnClickListener resetButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
            finish();
            startActivity(new Intent(RouteActivity.this, RouteActivity.class));
        }
    };

    /**
     *  Source building listener
     *  calls updateRoute();
     */
    private ListView.OnItemClickListener sourceItemClick = new ListView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> sourceAdapter, View v, int position, long id) {
            String shortName = (String) sourceList.getItemAtPosition(position);
            sourceBuilding = mapData.getBuilding(shortName);
            sourceList.setSelection(position);
            updateRoute();
        }
    } ;

    /**
     *  Destination building listener
     *  calls updateRoute()
     */
    private ListView.OnItemClickListener destItemClick = new ListView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> destAdapter, View v, int position, long id) {
            String shortName = (String) destList.getItemAtPosition(position);
            destBuilding = mapData.getBuilding(shortName);
            destList.setSelection(position);
            updateRoute();
        }
    } ;


    /**
     * populates a listview with all the shortnames in the buildings data
     * @modifies theList
     * @effects adds all of the short names of the buildings to the list
     * @param theList A listVIew Object to populate with building short names
     */
    private void populateBuildings(ListView theList){
        ArrayAdapter<String> theAdapter = new ArrayAdapter<String> (
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                new ArrayList<String>()
        );

        // add building shorts to buildings list view
        String[] theShorts = mapData.listShorts();
        Arrays.sort(theShorts);
        for (String s : theShorts){
            theAdapter.add(s);
        }
        theList.setAdapter(theAdapter);
    }

    /**
     * updates the route drawn on the map
     * also marks buildings as dest and source
     */
    private void updateRoute(){

        ScrollView vScroll = (ScrollView)findViewById(R.id.vScroll);

        // converts referenceMap pixels to location in the view
        Double drawCorrection = (double)vScroll.getWidth()/MAP_WIDTH;

        // move the dot to the currently source building
        if (sourceBuilding != null) {
            int intX = (int)Math.round(sourceBuilding.getX()*drawCorrection);
            int intY = (int)Math.round(sourceBuilding.getY()*drawCorrection);
            drawView.markSource(intX, intY);

        }

        // move the dot to the currently dest building
        if (destBuilding != null) {
            int intX = (int)Math.round(destBuilding.getX()*drawCorrection);
            int intY = (int)Math.round(destBuilding.getY()*drawCorrection);
            drawView.markDest(intX, intY);
        }

        // draw the path between buildings
        if (sourceBuilding != null &&
                destBuilding != null &&
                !sourceBuilding.equals(destBuilding)) {

            shortPath = mapData.getShortestPath(
                    new Node<MapElement>(sourceBuilding),
                    new Node<MapElement>(destBuilding)
            );

            LinkedList<Node<MapElement>> theNodes = shortPath.getNodeList();
            // *4 because line format is x0, y0, x1, y1 for each segment
            float[] pointsList = new float[theNodes.size()*4];

            // start of the segment
            MapElement start = theNodes.poll().getData();

            // build proper format
            int i = 0; // index
            while (!theNodes.isEmpty()) {
                MapElement end = theNodes.poll().getData();
                pointsList[i] = (float)(start.getX()*drawCorrection);
                i ++;
                pointsList[i] = (float)(start.getY()*drawCorrection);
                i ++;
                pointsList[i] = (float)(end.getX()*drawCorrection);
                i ++;
                pointsList[i] = (float)(end.getY()*drawCorrection);
                i ++;

                start = end;
            }
            drawView.drawPath(pointsList);

            // change in position from start to end of segment
            int xDelta = (int) Math.round(Math.abs(
                    destBuilding.getX() - sourceBuilding.getX()
            ));
            int yDelta = (int) Math.round(Math.abs(
                    destBuilding.getY() - sourceBuilding.getY()
            ));

            // center of the box that the path makes
            int xCenter = (int)Math.round(
                    (destBuilding.getX()+sourceBuilding.getX())/2
            );
            int yCenter = (int)Math.round(
                    (destBuilding.getY()+sourceBuilding.getY())/2
            );

            // translate from image pixel to device pixel
            float pxCoef = (float)drawView.getMeasuredWidth()/MAP_WIDTH;

            // zoom the map
            scale = Math.min(MAP_WIDTH/(xDelta + 80), MAP_HEIGHT/(yDelta + 80));

            // center view on route
            xShift = (int) Math.round((xCenter-MAP_WIDTH / 2) * pxCoef);
            yShift = (int) Math.round((yCenter-MAP_HEIGHT / 2) * pxCoef);
        } else { // there is no path to be drawn, clear any previous
            xShift = 0;
            yShift = 0;
            scale = 1;
            drawView.drawPath(null);
        }

        // apply shift and zoom
        drawView.setShift(xShift, yShift);
        drawView.setScaleFactor(scale);
    }
}
