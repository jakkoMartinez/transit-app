package com.zuk0.gaijinsmash.riderz.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zuk0.gaijinsmash.riderz.R;
import com.zuk0.gaijinsmash.riderz.database.FavoriteDbHelper;
import com.zuk0.gaijinsmash.riderz.debug.DebugController;
import com.zuk0.gaijinsmash.riderz.model.bart.Advisory;
import com.zuk0.gaijinsmash.riderz.model.bart.Estimate;
import com.zuk0.gaijinsmash.riderz.model.bart.Favorite;
import com.zuk0.gaijinsmash.riderz.model.bart.Trip;
import com.zuk0.gaijinsmash.riderz.network.FetchInputStream;
import com.zuk0.gaijinsmash.riderz.utils.SharedPreferencesHelper;
import com.zuk0.gaijinsmash.riderz.xml_adapter.advisory.AdvisoryXmlParser;
import com.zuk0.gaijinsmash.riderz.utils.BartApiStringBuilder;
import com.zuk0.gaijinsmash.riderz.utils.TimeAndDate;
import com.zuk0.gaijinsmash.riderz.xml_adapter.advisory.AdvisoryViewAdapter;
import com.zuk0.gaijinsmash.riderz.xml_adapter.estimate.EstimateViewAdapter;
import com.zuk0.gaijinsmash.riderz.xml_adapter.estimate.EstimateXmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView mBsaTimeTv, mEstimateErrorTv, mEtdTitle;
    private ListView mBsaListView, mEstimateListView;
    private View mInflatedView;
    private ProgressBar mEtdProgressBar;

    //---------------------------------------------------------------------------------------------
    // Lifecycle Events
    //---------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInflatedView = inflater.inflate(R.layout.view_home, container, false);
        return mInflatedView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mEtdProgressBar = mInflatedView.findViewById(R.id.home_etd_progressBar);
        mBsaTimeTv = mInflatedView.findViewById(R.id.home_view_timeTv);
        mBsaListView = mInflatedView.findViewById(R.id.home_bsa_listView);
        mEstimateListView = mInflatedView.findViewById(R.id.home_etd_listView);
        mEstimateErrorTv = mInflatedView.findViewById(R.id.home_etd_error);
        mEtdTitle = mInflatedView.findViewById(R.id.home_etd_title);
        ImageView imageView = mInflatedView.findViewById(R.id.home_banner_imageView);
        initTimeAndPic(imageView);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        new GetAdvisoryTask( this).execute();
        new GetFavoritesTask(this).execute();
        mEtdProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initTimeAndPic(ImageView imageView) {
        int hour = TimeAndDate.getCurrentHour();
        if(hour < 6 || hour >= 21) {
            // show night picture
            Glide.with(getActivity())
                    .load(R.drawable.sf_night)
                    .into(imageView);
        } else if(hour >= 17) {
            // show dusk picture
            Glide.with(getActivity())
                    .load(R.drawable.sf_dusk)
                    .into(imageView);
        } else {
            Glide.with(getActivity())
                    .load(R.drawable.sf_day)
                    .into(imageView);
        }
    }
    //---------------------------------------------------------------------------------------------
    // AsyncTask
    //---------------------------------------------------------------------------------------------
    //TODO: need to save state onPause(), etc to prevent too many calls everytime the app opens.

    private static class GetFavoritesTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<HomeFragment> mHomeRef;
        private List<Trip> mFinalList;

        private GetFavoritesTask(HomeFragment context) {
            mHomeRef = new WeakReference<>(context);
            mFinalList = new ArrayList<>();
        }

        @Override
        protected Boolean doInBackground(Void...voids) {
            if (DebugController.DEBUG) Log.i("GetFavoritesTask", "STARTING");
            HomeFragment homeFrag = mHomeRef.get();
            if(homeFrag != null && homeFrag.isAdded()) {
                mFinalList = getTripList(homeFrag.getActivity());
            }
            return mFinalList.size() > 0;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            HomeFragment homeFrag = mHomeRef.get();
            homeFrag.mEtdTitle.setVisibility(View.VISIBLE);
            if(result) {
                homeFrag.mEstimateListView.setVisibility(View.VISIBLE);
                EstimateViewAdapter adapter = new EstimateViewAdapter(mFinalList, homeFrag.getActivity(), homeFrag);
                homeFrag.mEstimateListView.setAdapter(adapter);
            } else {
                homeFrag.mEstimateErrorTv.setVisibility(View.VISIBLE);
                homeFrag.mEstimateErrorTv.setText(homeFrag.getResources().getString(R.string.estimate_not_available));
            }

            if(homeFrag.isAdded()){
                Log.i("visibility", "gone");
                homeFrag.mEtdProgressBar.setVisibility(View.GONE);
            }
        }
    }

    public static List<Trip> getTripList(Context context) {
        List<Trip> tripList = new ArrayList<>();
        int count = FavoriteDbHelper.getFavoritesCount(context);
        if(count > 0) {
            // should return a max of 2 favorite objects
            List<Favorite> favoritesList = FavoriteDbHelper.getFavoritesByPriority(context);
            tripList = getConsolidatedTripList(context, favoritesList);
        }
        return tripList;
    }

    public static List<Trip> getListFromApi(Context context) {
        List<Trip> tripList = new ArrayList<>();
        EstimateXmlParser parser = new EstimateXmlParser(context);

        return null;
    }

    public static List<Trip> getConsolidatedTripList(Context context, List<Favorite> favoritesList) {
        List<Trip> finalTripList = new ArrayList<>();
        EstimateXmlParser parser = new EstimateXmlParser(context);
        for(Favorite fav : favoritesList) {
            String url = BartApiStringBuilder.getRealTimeEstimates(fav.getOrigin());
            List<Trip> tempTripList = new ArrayList<>();
            try {
                // make call to api and store results in list
                tempTripList = parser.getList(url);

                // stores colors in HashSet
                HashSet<String> colorsSet = fav.getColors();
                if(DebugController.DEBUG) Log.i("colorSet", colorsSet.toString());

                //PARSE estimate list and modify each trip object
                List<Trip> tripRemovalList = new ArrayList<>();
                for(Trip trip : tempTripList) {
                    List<Estimate> estimateList = trip.getEstimateList();

                    // for each estimate object, check if route colors are matching
                    List<Estimate> removalList = new ArrayList<>();
                    for(Estimate estimate : estimateList) {
                        Log.i("est color", estimate.getColor());
                        if(!colorsSet.contains(estimate.getColor())) {
                            removalList.add(estimate);
                            Log.i("etd removal size", String.valueOf(removalList.size()));
                        }
                    }

                    estimateList.removeAll(removalList);
                    Log.i("estimateList new size", String.valueOf(estimateList.size()));
                    // remove any trips that don't have an estimate list to prevent NPE in adapter
                    if(trip.getEstimateList().size() == 0) {
                        tripRemovalList.add(trip);
                    }
                }
                tempTripList.removeAll(tripRemovalList);
            } catch (IOException | XmlPullParserException e) {
                Log.e("Exception", e.toString());
            }
            finalTripList.addAll(tempTripList);
            Log.i("finalTripList", String.valueOf(finalTripList.size()));
        }
        return finalTripList;
    }

    private static class GetAdvisoryTask extends AsyncTask<Void, Integer, Boolean> {
        private WeakReference<HomeFragment> mHomeRef;
        private List<Advisory> mList;
        private boolean mIs24HrTimeOn;

        private GetAdvisoryTask(HomeFragment context) {
            mList = new ArrayList<>();
            mHomeRef = new WeakReference<>(context);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            HomeFragment homeFrag = mHomeRef.get();
            if(homeFrag != null && homeFrag.isAdded()) {
                mIs24HrTimeOn = SharedPreferencesHelper.getTimePreference(homeFrag.getActivity());
                try {
                    FetchInputStream is = new FetchInputStream(homeFrag.getActivity());
                    InputStream in = is.connectToApi(BartApiStringBuilder.getBSA());
                    AdvisoryXmlParser parser = new AdvisoryXmlParser(homeFrag.getActivity());
                    mList = parser.parse(in);
                } catch (IOException | XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
            return mList != null;
        }

        protected void onPostExecute(Boolean result) {
            HomeFragment homeFrag = mHomeRef.get();
            if(result) {
                for(Advisory adv : mList) {
                    if(adv.getTime() != null && homeFrag != null) {
                        String time = getFormattedTime(adv, mIs24HrTimeOn);
                        String message = homeFrag.getResources().getString(R.string.last_update) + " " + time;
                        homeFrag.mBsaTimeTv.setText(message);
                    }
                }

                AdvisoryViewAdapter adapter;
                if (homeFrag != null && homeFrag.isAdded()) {
                    adapter = new AdvisoryViewAdapter(mList, homeFrag.getActivity());
                    homeFrag.mBsaListView.setAdapter(adapter);
                }
            } else {
                if(homeFrag != null && homeFrag.isAdded()) {
                    homeFrag.mBsaTimeTv.setText(homeFrag.getResources().getString(R.string.update_unavailable));
                }
            }
        }
    }

    public static String getFormattedTime(Advisory adv, boolean is24HrTimeOn) {
        String time;
        if(is24HrTimeOn) {
            time = TimeAndDate.format24hrTime(adv.getTime());
        } else {
            time = TimeAndDate.convertTo12Hr(adv.getTime());
        }
        return time;
    }

}
