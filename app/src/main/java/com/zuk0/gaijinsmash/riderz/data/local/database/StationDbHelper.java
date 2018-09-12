package com.zuk0.gaijinsmash.riderz.data.local.database;

import android.content.Context;
import android.util.Log;

import com.zuk0.gaijinsmash.riderz.utils.debug.DebugController;
import com.zuk0.gaijinsmash.riderz.data.local.entity.station_response.Station;
import com.zuk0.gaijinsmash.riderz.utils.BartApiUtils;
import com.zuk0.gaijinsmash.riderz.ui.adapter.station.StationXmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class StationDbHelper {

    private StationDatabase mDatabase;
    // Update this number when new Stations are built
    private static final int mNumOfBartStations = 48;

    public StationDbHelper(Context context) {
        mDatabase = StationDatabase.getRoomDB(context);
    }

    public void closeDb() {
        mDatabase.close();
    }

    public void initStationDb(Context context) throws IOException, XmlPullParserException {
        int count = getStationCount();
        if(count < mNumOfBartStations) {
            List<Station> stationList;
            StationXmlParser parser = new StationXmlParser(context);
            stationList = parser.getList(BartApiUtils.getAllStations());
            for(Station x : stationList) {
                Log.i(x.getName(), x.getAbbr());
                mDatabase.getStationDAO().addStation(x);
            }
        } else {
            Log.i("STATIONS DB", "IS FILLED");
        }
    }

    private int getStationCount() {
        int count = mDatabase.getStationDAO().countStations();
        if(DebugController.DEBUG)
            Log.i("count", String.valueOf(count));
        return count;
    }
}
