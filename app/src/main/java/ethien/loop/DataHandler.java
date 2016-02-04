package ethien.loop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Christian on 2/4/2016.
 */
public class DataHandler
{
    //private ArrayList<String> titlesList = new ArrayList<String>();
    private HashMap<String, ArrayList<String>> storyMap = new HashMap<String, ArrayList<String>>();

    public DataHandler(ArrayList<String> titlesList, ArrayList<ArrayList<String>> stories)
    {
        for(int x = 0; x < titlesList.size(); x++)
        {
            storyMap.put(titlesList.get(x), stories.get(x));
        }
    }
    public HashMap<String, ArrayList<String>> preparedData()
    {
        return storyMap;
    }
}
