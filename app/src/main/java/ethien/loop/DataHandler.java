package ethien.loop;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Christian on 2/4/2016.
 */
public class DataHandler
{
    //private ArrayList<String> titlesList = new ArrayList<String>();
    private LinkedHashMap<String, ArrayList<String>> storyMap = new LinkedHashMap<String, ArrayList<String>>();

    public DataHandler(ArrayList<String> titlesList, ArrayList<ArrayList<String>> stories)
    {
        for(int x = 0; x < titlesList.size(); x++)
        {
            Log.d("Title data", titlesList.get(x));
            storyMap.put(titlesList.get(x), stories.get(x));
        }
    }
    public LinkedHashMap<String, ArrayList<String>> preparedData()
    {
        return storyMap;
    }
}
