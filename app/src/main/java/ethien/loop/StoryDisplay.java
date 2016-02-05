package ethien.loop;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class StoryDisplay extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Bundle extras = getIntent().getExtras();
        String storyUrl = extras.getString("url");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_display);


        WebView storyWebView = (WebView) findViewById(R.id.story_view);
        storyWebView.setWebViewClient(new storyBrowser());
        storyWebView.getSettings().setLoadsImagesAutomatically(true);
        storyWebView.getSettings().setJavaScriptEnabled(true);
        storyWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
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
