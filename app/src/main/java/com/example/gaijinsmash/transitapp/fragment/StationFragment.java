package com.example.gaijinsmash.transitapp.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.gaijinsmash.transitapp.R;
import com.example.gaijinsmash.transitapp.view_adapter.StationCustomViewAdapter;
import com.example.gaijinsmash.transitapp.model.bart.Station;
import com.example.gaijinsmash.transitapp.network.xmlparser.StationXMLParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;



public class StationFragment extends Fragment {

    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.station_view, container, false);
        mListView = inflatedView.findViewById(R.id.station_listView);

        new GetStationsTask(getActivity()).execute();

        return inflatedView;
    }

    private class GetStationsTask extends AsyncTask<Void, Void, List<Station>> {

        private StationXMLParser stationXMLParser = null;
        private List<Station> stationList = null;
        private Context mContext;

        public GetStationsTask(Context context) {
            mContext = context;
        }

        protected void onPreExecute() {
            // TODO: set progress bar
            // progressBar = (ProgressBar) findViewById.(R.id.progressBar);
            // progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Station> doInBackground(Void... voids) {
            try {
                if(stationList == null && stationXMLParser == null) {
                    stationXMLParser = new StationXMLParser(mContext);
                    stationList = stationXMLParser.getAllStations();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return stationList;
        }

        protected void onPostExecute(List<Station> stations) {
            if(stationList != null) {
                StationCustomViewAdapter adapter = new StationCustomViewAdapter(stationList, mContext);
                mListView.setAdapter(adapter);
            } else {
                // TODO: Handle error gracefully for use - possibly use a cute img?
                Log.e("onPostExecute()", "stationList is NULL");
            }
        }
    }
}
