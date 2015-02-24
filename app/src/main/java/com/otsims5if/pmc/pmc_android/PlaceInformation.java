package com.otsims5if.pmc.pmc_android;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

import api.place.*;


public class PlaceInformation extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_information);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Call the service getPlaceById and execute the callback ShowPlaceCallback with the result
        //PlaceServices.getInstance().getPlaceById(2, new ShowPlaceCallback()).execute();

        PlaceServices.getInstance().getListPlacesByPosition(45.78166386726485, 4.872752178696828, 5, new ShowListPlacesCallback()).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_place_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_place_information, container, false);
            return rootView;
        }
    }

    /*Method for displaying a place received by a service*/
    private class ShowPlaceCallback extends GetPlaceByIdCallback {
        protected void callback(Exception e, Place place){
            TextView placeIdText = (TextView) findViewById(R.id.id_place_value);
            if(e != null || place == null) {
                Log.e("MainActivity", e.getMessage(), e);
                placeIdText.setText("Une erreur est survenu");
            }
            else{
                placeIdText.setText(place.getId());
            }
        }
    }

    /*Method for displaying a place received by a service*/
    private class ShowListPlacesCallback extends GetListPlacesByPositionCallback {
        protected void callback(Exception e, Place[] places){
            if(e != null || places == null) {
                Log.e("MainActivity", e.getMessage(), e);
                Log.e("erreur", "Une erreur est survenu");
            }
            else{
                Log.i("listPlaces", "listPlaces");
                for(int i=0; i<places.length; i++){
                    Log.i("place", "lat: " + places[i].getLatitude() + " lng: " + places[i].getLongitude());
                }
            }
        }
    }
}
