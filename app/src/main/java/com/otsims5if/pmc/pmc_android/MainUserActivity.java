package com.otsims5if.pmc.pmc_android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import api.place.PlaceServices;
import api.user.ChangeMacCallback;
import api.user.GetUserCallback;
import api.user.User;
import api.user.UserServices;


public class MainUserActivity extends ActionBarActivity implements ActionBar.TabListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;


    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] menutTitles;


    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    int menu_number = 5;
    int [] enable= new int[menu_number];

    FragmentManager fragmentManager;
    Fragment fragment;

    private Exception CreateException;
    private User getInformations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //    actionBar.hide();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        //Ajouter les onglets
        actionBar.addTab(actionBar.newTab()
                .setText(mSectionsPagerAdapter.getPageTitle(0))
                .setTabListener(this)
                .setIcon(R.drawable.map));


        actionBar.addTab(actionBar.newTab()
                        .setText(mSectionsPagerAdapter.getPageTitle(1))
                        .setTabListener(this)
                        .setIcon(R.drawable.bookmark)
        );


        ////
        mTitle = mDrawerTitle = getTitle();
        menutTitles = getResources().getStringArray(R.array.menu_array);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        //récuperation des données
        Bundle extras = getIntent().getExtras();
        String name_header = extras.getString("name_user" + " " + "macAddress");

        View header = getLayoutInflater().inflate(R.layout.header, null);
        TextView titleView_header = (TextView) header.findViewById(R.id.title_usr);
        titleView_header.setText(name_header);
        titleView_header.setTextSize(30);

        mDrawerList.addHeaderView(header);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mNavItems.add(new NavItem("Accueil", "", R.drawable.home));
        mNavItems.add(new NavItem("Compte", "Mon compte", R.drawable.user));
        mNavItems.add(new NavItem("Aide", "Besoin d'aide?", R.drawable.help));
        mNavItems.add(new NavItem("Commentaires", "Envoyez un e-mail", R.drawable.mail));
        mNavItems.add(new NavItem("A propos de", "Park my Car", R.drawable.action_about));


        for (int i = 0; i < menu_number; i++) {
            enable[i] = 1;
        }
        enable[0] = 0;

        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);
        // mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, menutTitles));

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        System.out.println(getSupportActionBar());
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (savedInstanceState == null) {
            selectItem(0);
        }

        /*button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //spinner.setVisibility(View.VISIBLE);
                ProgressDialog mDialog = new ProgressDialog(getApplicationContext());
                mDialog.setMessage("Loading...");
                mDialog.setCancelable(false);
                mDialog.show();
            }
        });*/

/*IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter1);
        this.registerReceiver(mReceiver, filter2);
        this.registerReceiver(mReceiver, filter3);*/

        //if (extras.getString("macAddress") == " " || extras.getString("macAddress") == null) {
            //BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            // Si connecté à un appareil en Bluetooth
            // Proposer de s'y appareiller
            /*IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
            IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
            IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            UserMapFragment umf = new UserMapFragment();
            UserMapFragment test = ((UserMapFragment)(this.mSectionsPagerAdapter.getItem(0)));

            /*this.registerReceiver(test.mReceiver, filter1);
            this.registerReceiver(test.mReceiver, filter2);
            this.registerReceiver(test.mReceiver, filter3);*/

            // Sinon proposer recherche + pairing

            /*String deviceName = "My_Device_Name";

            BluetoothDevice result = null;

            Set<BluetoothDevice> devices = null;// = mBluetoothAdapter.get les devices autour
            if (devices != null) {
                for (BluetoothDevice device : devices) {
                    if (deviceName.equals(device.getName())) {
                        result = device;
                        break;
                    }
                }
            }*/
        //}


    }

    //The BroadcastReceiver that listens for bluetooth broadcasts
    /*private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            UserMapFragment umf = new UserMapFragment();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //Device found
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                System.out.println("device just connected");
                String macAddress = device.getAddress();
                String status;

                status = "paired device just connected";
                Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();

                //if (macAddress.equals(getInformations.getMacAddress())) {

                    // rentre dans sa voiture (libère la place)
                    status = "place liberee";
                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
                    //umf.leave();

                    //leaveButton.setVisibility(View.GONE);
                    //parkButton.setVisibility(View.VISIBLE);
                    //currentPositionMarker.remove();
                    //PlaceServices.getInstance().releasePlace(myParkingLocation.latitude,
                            //myParkingLocation.longitude, new ReleaseAndRemovePlace()).execute();

                    //intent.putExtra("macAddress", "" + getInformations.getMacAddress());
               // }
                //else {
                    // appareille le gadget bluetooth de la voiture
                    status = "appareillé";
                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
                    //sendtoserver(device.getAddress());
                    //intent.putExtra("macAddress", ""+getInformations.getMacAddress());
                //}
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Done searching
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                //Device is about to disconnect
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Toast.makeText(getApplicationContext(), "a device just disconnected", Toast.LENGTH_LONG).show();
                String macAddress = device.getAddress();
                //if (macAddress.equals(getInformations.getMacAddress())) {
                    // est garé
                    Toast.makeText(getApplicationContext(), "garé", Toast.LENGTH_LONG).show();
                    //umf.park();
                //}
            }
        }
    };*/

    private class ChangeMac extends ChangeMacCallback {
        protected void callback(Exception e, User user){
            getInformations = user;
            System.out.println("Exception e :" +e);

        }
    }

    public void sendtoserver(final String macAddress) {

        final Intent intent = new Intent(this, MainUserActivity.class);

        System.out.println("test sendtoserver macaddress");
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                UserServices.getInstance().changemacaddress(getInformations, macAddress, new ChangeMac()).execute();
            }

            @Override
            protected Void doInBackground(Void... arg0) {return null;}
            @Override
            protected void onPostExecute(Void result) {
                System.out.println("new macAddress: " + macAddress);
                intent.putExtra("macAddress", macAddress);
                //  startActivity(intent);
                finish();
            }

        };task.execute((Void[]) null);


    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            System.out.println("click");
            selectItem(position);
        }
    }

    private class Information  extends GetUserCallback {
        protected void callback(Exception e, User user){
            System.out.println("exceptionnn "+e);
            CreateException = e;
            getInformations = user;
            //System.out.println("User name" + user.getUsername());
            System.out.println("Exception e GetUser :" +e);
        }
    }

    private void selectItem(int position) {
        mDrawerList.setItemChecked(position, true);

        // update the main content by replacing fragments
   /*     if (position == 1) {
            if(enable[position]==0) {


           }
        }
        if(position!=1 && position!=0){  enable[1]=0;
          // getSupportActionBar().removeAllTabs();
            Intent intent = new Intent(this, swit.class);
            startActivity(intent);
        }*/
        final Intent intent;
        switch(position){
            case 2:
                 System.out.println("case2");
                 intent = new Intent(this, InformationUser.class);

                 AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        System.out.println("prexecute");
                        UserServices.getInstance().getUser(new Information()).execute();
                        System.out.println("after "+ CreateException);
                    }

                    @Override
                    protected Void doInBackground(Void... arg0) {return null;}
                    @Override
                    protected void onPostExecute(Void result) {
                        System.out.println("aaaaaaaaaa");
                        if(CreateException == null) {
                            intent.putExtra("name", getInformations.getUsername());
                            intent.putExtra("score", ""+getInformations.getScore());
                            intent.putExtra("level_name", ""+getInformations.getLevel().getLevelName());
                            intent.putExtra("Start_Score", ""+getInformations.getLevel().getStartScore());
                            intent.putExtra("NextLevelScore", ""+getInformations.getLevel().getNextLevelScore());
                            //intent.putExtra("macAddress", ""+getInformations.getMacAddress());

                            startActivity(intent);
                        }

                    }

                };task.execute((Void[]) null);
                    break;
            case 3: //aide
                    intent = new Intent(this, HelpActivity.class);
                    startActivity(intent);
                    break;
            case 4: intent = new Intent(this, MailActivity.class);
                    startActivity(intent);
                    break;
            case 5: //About
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            default: ;
        }


        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(menutTitles[0]);
        mDrawerLayout.closeDrawer(mDrawerList);

    }

    public static class PlanetFragment extends Fragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public PlanetFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
            int i = getArguments().getInt(ARG_PLANET_NUMBER);
            String planet = getResources().getStringArray(R.array.menu_array)[i];

            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                    "drawable", getActivity().getPackageName());
            ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
            getActivity().setTitle(planet);
            return rootView;
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

/*    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            System.out.println("logout");
            showDialog(DIALOG_ALERT);
            return true;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_websearch:
                // create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_user, menu);
        return true;
    }*/
   /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }*/



    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            switch(position){
                case 0 :
                    return new UserMapFragment();
                case 1 :
                    /*BookMarkFragment newBookMarkFragment = new BookMarkFragment();
                    newBookMarkFragment.setViewPager(mViewPager);
                    return newBookMarkFragment;*/
                    return new BookMarkFragment();
                default:
                    return new PlaceholderFragment();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    public void displayPlaceInformation(View v){
        Intent intent = new Intent(this, PlaceInformation.class);
        startActivity(intent);
    }

    private static final int DIALOG_ALERT = 10;

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_ALERT:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Voulez-vous vous déconnecter?");
                builder.setCancelable(true);
                builder.setPositiveButton("Oui", new OkOnClickListener());
                builder.setNegativeButton("Non", new CancelOnClickListener());
                AlertDialog dialog = builder.create();
                dialog.show();
        }
        return super.onCreateDialog(id);
    }

    private final class CancelOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            Toast.makeText(getApplicationContext(), "Activity will continue",
                    Toast.LENGTH_LONG).show();
        }
    }

    private final class OkOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            MainUserActivity.this.finish();
        }
    }

    @Override
    public void onBackPressed() {
     showDialog(DIALOG_ALERT);

    //   new LogOutWindow().show(getFragmentManager(),"Diag");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }



}
