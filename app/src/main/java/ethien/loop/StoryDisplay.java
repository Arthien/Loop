package ethien.loop;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class StoryDisplay extends AppCompatActivity
{
    private WebView storyWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String storyUrl = extras.getString("url");

        setContentView(R.layout.activity_story_display);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        storyWebView = (WebView) findViewById(R.id.story_view);
        storyWebView.setWebViewClient(new storyBrowser()
        {
            public void onProgressChanged(WebView view, int progress)
            {
                StoryDisplay.this.setProgress(progress * 100);
            }

            public void onPageFinished(WebView view, String url)
            {
                //String name = storyWebView.getTitle();
                setTitle(view.getTitle());
            }
        });
        storyWebView.getSettings().setLoadsImagesAutomatically(true);
        storyWebView.getSettings().setJavaScriptEnabled(true);
        storyWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        storyWebView.getSettings().setLoadWithOverviewMode(true);
        storyWebView.getSettings().setUseWideViewPort(true);
        Log.d("Webview", storyUrl);
        storyWebView.loadUrl(storyUrl);
    }

    private class storyBrowser extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
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
                storyWebView.loadUrl(storyUrl);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_story_display, menu);
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
