package ethien.loop;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;


public class Index extends AppCompatActivity
{
    private static final String RURL = "https://reddit.com/r/tldr/new.json?limit=10&raw_json=1&after=";
    private static final String author = "Intrinsic";
    private static final String tType = "t3";
    private static String afterID = "";
    private static ArrayList<String> titleCache = new ArrayList<>();

    private ListView contentList;
    private SwipyRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        contentList = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        fetchContent("");

        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if(direction == SwipyRefreshLayoutDirection.BOTTOM)
                {
                    fetchContent(afterID);
                }
            }
        });
    }
    private void fetchContent(String after)
    {
        String requestURL = RURL + after;
        Log.d("URL", requestURL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.d("Response: ", response);
                        JSONObject jsonObject = null;
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
                                    Elements cats = doc.select("h2");
                                    String subreddits = doc.select("h2 > a").text();
                                    Elements stories = doc.select("li > a[href^=https]");

                                }
                            }
                            afterID = data.getString("after");
                            appendList(contentList, titleCache);

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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void appendList(ListView listView, ArrayList<String> values)
    {
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_index, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
