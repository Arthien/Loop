package ethien.loop;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

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
import java.util.HashMap;


public class Index extends Activity
{
    private static final String RURL = "https://reddit.com/r/tldr/new.json?limit=10&raw_json=1&after=";
    private static final String author = "Intrinsic";
    private static final String tType = "t3";
    private static String afterID = "";
    private static ArrayList<String> titleCache = new ArrayList<>();
    private static RequestQueue requestQueue;

    private ExpandableListView contentList;
    private SwipyRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        contentList = (ExpandableListView) findViewById(R.id.listView);
        swipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        NetworkManager.getInstance(this);
        requestQueue = Volley.newRequestQueue(this);
        fetchContent();

        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction)
            {
                if (direction == SwipyRefreshLayoutDirection.BOTTOM)
                {
                    fetchContent();
                }
            }
        });
    }
    private void fetchContent()
    {
        NetworkManager.getInstance().fetchContent(new NetworkListener<HashMap>()
        {
            @Override
            public void getResult(HashMap preparedData)
            {
                if (preparedData != null)
                {
                    appendList(contentList, preparedData);
                }
            }
        });
    }
    private void fetchStory(String url)
    {
        Log.d("Story start", url);
        String result;
        NetworkManager.getInstance().parseStoryFromUrl(url, new NetworkListener<String>()
        {
            @Override
            public void getResult(String storyUrl)
            {
                Log.d("Story end", storyUrl);
                loadStory(storyUrl);
            }
        });
    }

    private void loadStory(String url)
    {
        Intent storyActivity = new Intent(this, StoryDisplay.class);
        storyActivity.putExtra("url", url);
        startActivity(storyActivity);
    }
    private void appendList(ExpandableListView listView, HashMap<String, ArrayList<String>> storyMap)
    {
        ArrayList<String> keys = new ArrayList<String>(storyMap.keySet());
        ExpandableListAdapter adapter = new ExpandableListAdapter(this, storyMap, keys);
        listView.setAdapter(adapter);
        listView.setOnChildClickListener(myListItemClicked);

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
    private OnChildClickListener myListItemClicked =  new OnChildClickListener() {

        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {

            String url = v.getTag().toString();
            if(url != null)
            {
                fetchStory(url);
            }
            return false;
        }

    };
}
