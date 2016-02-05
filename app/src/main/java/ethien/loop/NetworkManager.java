package ethien.loop;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Christian on 2/5/2016.
 */
public class NetworkManager
{
    private static final String TAG = "NetworkManager";
    private static final String author = "Intrinsic";
    private static final String tType = "t3";
    private static final String prefixURL =
            "https://reddit.com/r/tldr/new.json?limit=10&raw_json=1&after=";
    private static final String storyUrlSuffix = ".json?raw_json=1";
    private static final String[] filters = {"/r/IAmA", "/r/AskReddit"};

    private static NetworkManager instance = null;
    private static String afterID = "";
    private static ArrayList<String> titleCache = new ArrayList<>();
    private static ArrayList<ArrayList<String>> storiesCache = new ArrayList<>();



    public RequestQueue requestQueue;

    private NetworkManager(Context context)
    {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized NetworkManager getInstance(Context context)
    {
        if (null == instance)
        {
            instance = new NetworkManager(context);
        }
        return instance;
    }

    public static synchronized NetworkManager getInstance()
    {
        if (null == instance)
        {
            throw new IllegalStateException(NetworkManager.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    public void fetchContent(final NetworkListener<HashMap> listener)
    {

        String url = prefixURL + afterID;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.d("Response: ", response);
                        try
                        {
                            JSONObject initJSON = new JSONObject(response);
                            JSONObject data = initJSON.getJSONObject("data");
                            JSONArray children = data.getJSONArray("children");
                            JSONObject selected;
                            JSONObject childData;
                            int x = 0;
                            String HTML;
                            String kind;
                            for(x = 0; x < children.length(); x++)
                            {
                                selected = children.getJSONObject(x);
                                kind = selected.getString("kind");
                                if(kind.equals(tType))
                                {
                                    childData = selected.getJSONObject("data");
                                    HTML = childData.getString("selftext_html");

                                    String postTitle = childData.getString("title");
                                    titleCache.add(postTitle);

                                    Element doc = Jsoup.parseBodyFragment(HTML).body();
                                    Elements all = doc.select("*");

                                    ArrayList<String> subreddit = new ArrayList<>();
                                    boolean filtered = false;
                                    for(Element e : all)
                                    {
                                        String tag = e.tagName();
                                        if(tag.equals("h2"))
                                        {
                                            filtered = filterStory(e.text());
                                            if(filtered)
                                            {
                                                subreddit.add(e.text().trim());
                                            }
                                        }
                                        else if(filtered)
                                        {
                                            if(tag.equals("a") && e.attr("href").contains("http"))
                                            {
                                                String storyTitle = e.text().trim();
                                                storyTitle = storyTitle.concat("=");
                                                String storyHref = e.attr("href").trim();
                                                subreddit.add(storyTitle.concat(storyHref));
                                            }
                                        }
                                    }
                                    storiesCache.add(subreddit);
                                }
                            }
                            afterID = data.getString("after");
                            DataHandler dataHandler = new DataHandler(titleCache, storiesCache);
                            HashMap<String, ArrayList<String>> preparedData = dataHandler.preparedData();
                            Log.d("story map", preparedData.toString());
                            listener.getResult(preparedData);

                        }
                        catch (JSONException e)
                        {
                            Log.e("JSON Parse", e.toString());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.e("Response-Error: ", error.toString());
                    }
                }
        );
        requestQueue.add(stringRequest);
    }

    public void parseStoryFromUrl(String url, final NetworkListener<String> listener)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + storyUrlSuffix,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.d("Response: ", response);
                        try
                        {
                            JSONArray initJSON = new JSONArray(response);
                            JSONObject data = initJSON.getJSONObject(0).getJSONObject("data");
                            JSONObject childData = data.getJSONArray("children").getJSONObject(0);
                            childData = childData.getJSONObject("data");
                            String storyUrl = childData.getString("url");

                            listener.getResult(storyUrl);

                        }
                        catch (JSONException e)
                        {
                            Log.e("JSON Parse", e.toString());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.e("Response-Error: ", error.toString());
                    }
                }
        );
        requestQueue.add(stringRequest);
    }

    private boolean filterStory(String input)
    {
        return !Arrays.asList(filters).contains(input);
    }
}
