package ethien.loop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;



public class StoryDisplay extends AppCompatActivity
{
    private WebView storyWebView;
    private static final String userAgentMobile = "Android";
    private static final String userAgentDesktop = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";
    private static String articleBaseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String baseUrl = extras.getString("url");

        setContentView(R.layout.activity_story_display);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_story);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("Loading...");
        }


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
                getSupportActionBar().setTitle(view.getTitle());
            }
        });
        WebSettings settings = storyWebView.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        Log.d("Webview", baseUrl);
        fetchStory(baseUrl);
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
        NetworkManager.getInstance().parseStoryFromUrl(url, new NetworkListener<String>()
        {
            @Override
            public void getResult(String storyUrl)
            {
                Log.d("Story end", storyUrl);
                articleBaseUrl = storyUrl;
                storyWebView.loadUrl(storyUrl);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && storyWebView.canGoBack()) {
            storyWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        item.setChecked(!item.isChecked());

        switch (id)
        {
            case R.id.action_check:
                if(item.isChecked())
                {
                    storyWebView.getSettings().setUserAgentString(userAgentDesktop);
                    storyWebView.getSettings().setLoadWithOverviewMode(true);
                    Log.d("USer agent", "Request desktop site");
                }
                else
                {
                    storyWebView.getSettings().setUserAgentString(userAgentMobile);
                    storyWebView.getSettings().setLoadWithOverviewMode(false);
                }
                if(getSupportActionBar() != null)
                {
                    getSupportActionBar().setTitle("Loading...");
                }
                Log.d("URL WEB VIEW", articleBaseUrl);
                Log.d("User Agent is", storyWebView.getSettings().getUserAgentString());
                storyWebView.loadUrl(articleBaseUrl);
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
