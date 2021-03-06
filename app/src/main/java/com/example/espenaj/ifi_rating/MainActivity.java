package com.example.espenaj.ifi_rating;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.example.espenaj.ifi_rating.model.ChessMatch;
import com.example.espenaj.ifi_rating.model.Match;
import com.example.espenaj.ifi_rating.model.Player;
import com.example.espenaj.ifi_rating.util.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PlayersFragment.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    String url = "http://api.chess-rating.vegarm.svc.tutum.io:3000/squash/";
    public ArrayList<Match> matches;

    public static List<ChessMatch> MATCHES = new ArrayList<>();
    public static List<Player> PLAYERS = new ArrayList<>();

    private FragmentComm fragmentComm;

    String LogTag = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Empty", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        setFabForPlayersFragment();
                        break;
                    case 1:
                        setFabForMatchFragment();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        matches = new ArrayList<>();
        MATCHES = new ArrayList<>();

        Context context = this;

        //dummyData();
        new JSONParse().execute();

    }

    public void setFragmentComm(FragmentComm fc) {
        this.fragmentComm = fc;
    }

    @Override
    public void onAttachFragment(android.app.Fragment fragment) {
        super.onAttachFragment(fragment);
    }


    public void dummyData() {
        for(int i = 0; i < 10; i ++) {
            MATCHES.add(new ChessMatch(""+i, "Vegar", "Jones", "Black wins"));
        }

        Log.d("MAIN", "check first : " + MATCHES.get(0).getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void getMatches() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class JSONParse extends AsyncTask<String, String, ArrayList<JSONArray>> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected ArrayList<JSONArray> doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL

            ArrayList<JSONArray> jsonArrays = new ArrayList<>(2);
            JSONArray json_games = jParser.getJson(url + "games");
            JSONArray json_players = jParser.getJson(url + "players");

            jsonArrays.add(json_games);
            jsonArrays.add(json_players);

            return jsonArrays;
        }

        @Override
        protected void onPostExecute(ArrayList<JSONArray> jsonArrays) {

            JSONArray jsonArray = jsonArrays.get(0);
            JSONArray players = jsonArrays.get(1);

            System.out.println(jsonArray);
            System.out.println(players);

            // Getting JSON Array
            // match = json.getJSONArray();
            //JSONObject c = user.getJSONObject(0);

            JSONObject jsonObject;
            JSONObject playerJson;

            if(jsonArray == null) {
                return;
            }

            /*create match objects */
            for(int i = 0; i < jsonArray.length(); i++) {
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                    ChessMatch match = new ChessMatch(jsonObject.getString("_id"), jsonObject.getString("white"), jsonObject.getString("black"), jsonObject.getString("result"));
                    Log.d("Main", "Created : " + match.getId());
                    MATCHES.add(match);
                    Log.d("Main", "Added : "  + MATCHES.get(i).getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            /* Create player objects */
            for(int i = 0; i < players.length(); i++) {
                try {
                    playerJson = players.getJSONObject(i);
                    String rating = playerJson.getString("rating");
                    Log.d(LogTag, rating);
                    Player player = new Player(playerJson.getString("name"), playerJson.getString("_id"),(int) Double.parseDouble(rating));
                    PLAYERS.add(player);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            Log.d("MAIN", "check first : " + MATCHES.get(0).getId());
            Log.d("MAIN", "check first Player: " + PLAYERS.get(0).getId());

            MatchFragment matchFragment;

            matchFragment = (MatchFragment)
                    getSupportFragmentManager().findFragmentById(R.id.list);

            PlayersFragment.newInstance();

            Log.d(LogTag, "" + MATCHES);

            Log.d(LogTag, "New MatchFragment from JSONParser");
            MatchFragment.newInstance((ArrayList) MATCHES);

            if(matchFragment != null) {
                matchFragment.updateMatchList();
            } else {
                /*
                matchFragment = new MatchFragment();
                Bundle args  = new Bundle();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.list, matchFragment);
                transaction.addToBackStack(null);

                transaction.commit();
                */
            }

            pDialog.dismiss();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
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

            switch (position) {
                case 0:
                    Log.d(LogTag, "new PlayersFragment from slectionAdapter");
                    setFabForPlayersFragment();
                    return PlayersFragment.newInstance();
                case 1:
                    Log.d(LogTag, "new MathcFragment from slectionAdapter");
                    setFabForMatchFragment();
                    return MatchFragment.newInstance((ArrayList) MATCHES);
                default:
                    return PlaceholderFragment.newInstance(position + 1);
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Players";
                case 1:
                    return "Matches";
                case 2:
                    return "Table";
            }
            return null;
        }
    }

    private void setFabForMatchFragment() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Matches", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setFabForPlayersFragment() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Create player");
                builder.setView(R.layout.dialog_create_player)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Snackbar.make(view, "Created player \"TODO\" ", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
                builder.show();

            }
        });
    }

    public interface FragmentComm {
        void onMatchDataDownloaded(List<ChessMatch> matches);
    }


}
