package com.zuk0.gaijinsmash.riderz.ui.fragment.bart_results;

import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.zuk0.gaijinsmash.riderz.R;
import com.zuk0.gaijinsmash.riderz.data.local.database.FavoriteDbHelper;
import com.zuk0.gaijinsmash.riderz.data.local.entity.Favorite;
import com.zuk0.gaijinsmash.riderz.data.local.entity.station_response.Station;
import com.zuk0.gaijinsmash.riderz.data.local.entity.trip_response.Trip;
import com.zuk0.gaijinsmash.riderz.ui.adapter.trip.TripViewRecyclerAdapter;
import com.zuk0.gaijinsmash.riderz.ui.fragment.trip.TripFragment;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;

public class BartResultsFragment extends Fragment {

    @BindView(R.id.results_recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.bart_results_progressBar) ProgressBar mProgressBar;

    @Inject
    BartResultsViewModelFactory mBartResultsViewModelFactory;

    private BartResultsViewModel mViewModel;

    //todo: save in onSavedInstance
    private String mOrigin, mDestination, mDate, mTime;

    private MenuItem mFavoriteIcon, mFavoritedIcon;
    private Favorite mFavoriteObject;
    //private ArrayList<String> mTrainHeaders;
    private static boolean IS_FAVORITED_ON = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.view_results, container, false);
        ButterKnife.bind(this, inflatedView);
        return inflatedView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initDagger();
        initViewModel();
        initBundleFromTripFragment();
        initFavoriteIcon();
        initLiveData(mOrigin, mDestination);
        // Checks database and displays appropriate view in toolbar
        //new SetFavoritesTask(this, FavoritesTask.CHECK_CURRENT_TRIP).execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.favorite, menu);
        inflater.inflate(R.menu.favorited, menu);
        mFavoriteIcon = menu.findItem(R.id.action_favorite);
        mFavoritedIcon = menu.findItem(R.id.action_favorited);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_favorite:
                new SetFavoritesTask(this, FavoritesTask.ADD_FAVORITE).execute();
                return true;
            case R.id.action_favorited:
                initAlertDialog();
                return true;
        }
        return false;
    }

    private void initDagger() {
        AndroidSupportInjection.inject(this);
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mBartResultsViewModelFactory).get(BartResultsViewModel.class);
    }

    private void initBundleFromTripFragment() {
        Bundle bundle = getArguments();
        if(bundle != null) {
            mOrigin = bundle.getString(TripFragment.TripBundle.ORIGIN.getValue());
            mDestination = bundle.getString(TripFragment.TripBundle.DESTINATION.getValue());
            mDate = bundle.getString(TripFragment.TripBundle.DATE.getValue());
            mTime = bundle.getString(TripFragment.TripBundle.TIME.getValue());
            //mTrainHeaders = mBundle.getStringArrayList(TripFragment.TripBundle.TRAIN_HEADERS.getValue());
        }
    }

    private void initLiveData(String origin, String destination) {
        //the get livedata list
        LiveData<List<Station>> liveData = mViewModel.getStationsFromDb(getActivity(), origin, destination);

        liveData.observe(this, data -> {
            String depart;
            String arrive;
            if (data != null) {
                if(data.get(0).getName().equals(origin)){
                    depart = data.get(0).getAbbr();
                    arrive = data.get(1).getAbbr();
                } else {
                    depart = data.get(1).getAbbr();
                    arrive = data.get(0).getAbbr();
                }
                initTripCall(depart, arrive, mDate, mTime);
            }
        });
    }

    private void initTripCall(String originAbbr, String destAbbr, String date, String time) {
        mViewModel.getTrip(originAbbr, destAbbr, date, time)
                .observe(this, tripXmlResponse -> {
                    if(tripXmlResponse != null) {
                        List<Trip> list = tripXmlResponse.getSchedule().getRequest().getTripList();
                        initRecylerView(list);
                    }
        });
    }

    private void initRecylerView(List<Trip> tripList) {
        if(tripList != null) {
            TripViewRecyclerAdapter adapter = new TripViewRecyclerAdapter(tripList);
            mRecyclerView.setAdapter(adapter);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void initFavoriteIcon() {
        if(mOrigin != null && mDestination != null) {
            mFavoriteObject = new Favorite();

            //check db if first entry
            mFavoriteObject.setPriority(Favorite.Priority.ON);
            mFavoriteObject.setOrigin(mOrigin);
            mFavoriteObject.setDestination(mDestination);

            //mFavoriteObject.setColors(BartRoutesUtils.getColorsSetFromFullTrip(mFullTripList));
            //mFavoriteObject.setTrainHeaderStations(mTrainHeaders);
        }
    }

    private void initAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getResources().getString(R.string.alert_dialog_remove_favorite_title));
        alertDialog.setMessage(getResources().getString(R.string.alert_dialog_confirmation));
        //alertDialog.setPositiveButton(getResources().getString(R.string.alert_dialog_yes), (dialog, which) -> new SetFavoritesTask(mBartResultsFragment, FavoritesTask.DELETE_FAVORITE).execute());
        alertDialog.setNegativeButton(getResources().getString(R.string.alert_dialog_no), (dialog, which) -> dialog.cancel());
        alertDialog.show();
    }









    //---------------------------------------------------------------------------------------------
    // AsyncTask
    //---------------------------------------------------------------------------------------------
    private enum FavoritesTask {
        CHECK_CURRENT_TRIP, ADD_FAVORITE, DELETE_FAVORITE
    }

    private static class SetFavoritesTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<BartResultsFragment> mWeakRef;
        private FavoritesTask mTask;

        private SetFavoritesTask(BartResultsFragment context, FavoritesTask task) {
            mWeakRef = new WeakReference<>(context);
            mTask = task;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            BartResultsFragment frag = mWeakRef.get();
            FavoriteDbHelper db = new FavoriteDbHelper(frag.getActivity());
            switch(mTask) {
                case CHECK_CURRENT_TRIP:
                    return db.doesFavoriteExistAlready(frag.mFavoriteObject);
                case ADD_FAVORITE:
                    if (db.getFavoritesCount() < 2) {
                        frag.mFavoriteObject.setPriority(Favorite.Priority.ON);
                    }
                    db.addToFavorites(frag.mFavoriteObject);

                    // This adds the inverse of the Trip object
                    Favorite favoriteReversed = new Favorite();
                    favoriteReversed.setOrigin(frag.mDestination);
                    favoriteReversed.setDestination(frag.mOrigin);
                    if (db.getFavoritesCount()< 2) {
                        favoriteReversed.setPriority(Favorite.Priority.ON);
                    }
                    //List<FullTrip> tripList = getFullTripList(frag.getActivity(), frag.mDestination, frag.mOrigin);
                    //favoriteReversed.setColors(BartRoutesUtils.getColorsSetFromFullTrip(tripList));
                    //favoriteReversed.setTrainHeaderStations(BartRoutesUtils.getTrainHeadersListFromFullTrip(tripList));
                    db.addToFavorites(favoriteReversed);
                    return true;
                case DELETE_FAVORITE:
                    db.removeFromFavorites(frag.mFavoriteObject);
                    return false;
            }
            db.closeDb();
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            BartResultsFragment frag = mWeakRef.get();
            if(result) {
                IS_FAVORITED_ON = true;
                frag.mFavoritedIcon.setVisible(true);
                frag.mFavoriteIcon.setVisible(false);
                if(mTask == FavoritesTask.ADD_FAVORITE) {
                    Toast.makeText(frag.getActivity(), frag.getResources().getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();
                }
            } else {
                IS_FAVORITED_ON = false;
                frag.mFavoriteIcon.setVisible(true);
                frag.mFavoritedIcon.setVisible(false);
                if(mTask == FavoritesTask.DELETE_FAVORITE) {
                    Toast.makeText(frag.getActivity(), frag.getResources().getString(R.string.favorite_deleted), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
