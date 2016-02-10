package ethien.loop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class Index extends AppCompatActivity
{

    private ExpandableListView contentList;
    private SwipyRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_index);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("Loop");
        }

        contentList = (ExpandableListView) findViewById(R.id.listView);
        swipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);


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
                else if (direction == SwipyRefreshLayoutDirection.TOP)
                {
                    NetworkManager.getInstance().clearCache();
                    fetchContent();
                }
            }
        });
    }
    private void fetchContent()
    {
        NetworkManager.getInstance().fetchContent(new NetworkListener<LinkedHashMap>()
        {
            @Override
            public void getResult(LinkedHashMap preparedData)
            {
                if (preparedData != null)
                {
                    appendList(contentList, preparedData);
                }
            }
        });
    }

    private void loadStory(String url)
    {
        Intent storyActivity = new Intent(this, StoryDisplay.class);
        storyActivity.putExtra("url", url);
        startActivity(storyActivity);
    }
    private void appendList(ExpandableListView listView, LinkedHashMap<String, ArrayList<String>> storyMap)
    {
        ArrayList<String> keys = new ArrayList<>(storyMap.keySet());
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

        return super.onOptionsItemSelected(item);
    }
    private OnChildClickListener myListItemClicked =  new OnChildClickListener() {

        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {

            String url = v.getTag().toString();
            if(url != null)
            {
                loadStory(url);
            }
            return false;
        }

    };
}
