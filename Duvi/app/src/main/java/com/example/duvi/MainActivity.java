package com.example.duvi;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity  implements View.OnKeyListener {

    public static final String URL_ENCODING = "UTF-8";
    private static final String URL_ABOUT_BLANK = "about:blank";
    public static final String URL_SCHEME_ABOUT = "about:";
    public static final String URL_SCHEME_MAIL_TO = "mailto:";
    private static final String URL_SCHEME_FILE = "file://";
    private static final String URL_SCHEME_HTTPS = "https://";
    private static final String URL_SCHEME_HTTP = "http://";
    public static final String URL_SCHEME_INTENT = "intent://";



    WebView webView;
    EditText search_query;
    ProgressBar progressBar;
    ImageView more, tabs ,search;
    Bundle ss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ss = savedInstanceState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.show_web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setBackgroundColor(Color.WHITE);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
                if(newProgress < 100 && progressBar.getVisibility() == ProgressBar.GONE){
                    progressBar.setVisibility(View.VISIBLE);
                }
                if(newProgress == 100){
                    progressBar.setVisibility(View.GONE);
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
        webView.setWebViewClient(new MyWebClient());
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setMax(100);
        progressBar.setVisibility(View.GONE);
        search_query = findViewById(R.id.search_query);
        search_query.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    changeUrl();
                }
                return false;
            }
        });
        search_query.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    search_query.setSelection(search_query.getText().toString().length());
                }
            }
        });
        search = findViewById(R.id.search_im_b);
        tabs = findViewById(R.id.tabs_im_b);
        more = findViewById(R.id.more_im_b);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUrl();
            }
        });
        if(savedInstanceState == null){
            mainTabLoad();
        }
    }
    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }
    private  void changeUrl(){
        // load
        LoadURL(search_query.getText().toString());
        // set data to history
        ContentValues cv = new ContentValues();
        cv.put("name",search_query.getText().toString());
        cv.put("path",getBigURL(search_query.getText().toString()));
        TabsWork history  = new TabsWork<MainActivity>("history","history",this,-100, -100);
        history.addTab(cv);
        //  tabs work
        TabsWork mainTab = new TabsWork<MainActivity>("mainTab","mainTab", this,-100,-100);
        TabsWork tabsWork = new TabsWork<MainActivity>("mydata","tabs", this,-100,-100);
        try{
            ContentValues contentValues =(ContentValues) mainTab.getData().get(0);
            String mainTabId = contentValues.get("id").toString();
            tabsWork.updateDB(mainTabId,cv);
            cv.put("id",mainTabId);
            mainTab.addTab(cv);
        }catch (Exception e){
            tabsWork.addTab(cv);
        }

    }
    private void mainTabLoad(){
        // if it was last page create another one
        TabsWork tab  = new TabsWork<MainActivity>("mydata","tabs", this,-100,-100);
        TabsWork mainTab = new TabsWork<MainActivity>("mainTab","mainTab", this,-100,-100);
        ArrayList c =  tab.getData();
        if(c.size() <= 0){
            ContentValues cv = new ContentValues();
            cv.put("name","home");
            cv.put("path","google.com");
            tab.addTab(cv);
            // main tab settings
            mainTab.clearDB();
            mainTab.addTab((ContentValues) tab.getData().get(0));
        }
        try{
            ContentValues contentValues =(ContentValues) mainTab.getData().get(0);
            String url = contentValues.get("path").toString();
            search_query.setText(url);
            LoadURL(url);
        }catch (Exception e){
            ContentValues cv = new ContentValues();
            cv.put("name","home");
            cv.put("path","google.com");
            mainTab.addTab(cv);
            tab.addTab(cv);
            search_query.setText(cv.get("path").toString());
            LoadURL(cv.get("path").toString());
        }
    }

    private String getBigURL(String url){
        if(existUrl(url)) {
            if((!url.contains("https://"))&&(!url.contains("http://"))){
                url = "https://" + url;
            }
        }else{
            url = "https://google.com/search?q="+url;
        }
        return url;
    }

    private  boolean existUrl(String url){
        if (url == null) {
            return false;
        }
        while((url.charAt(url.length() - 1) == ' ')){
            url = url.substring(0, url.length() - 1);
        }
        if((url.contains(".")&&(!url.contains(" ")))){
            return true;
        }
        if((url.contains(" ")&&(url.contains("/")))){
            return true;
        }
        return false;
    }

    public void showMore(View view) {
        PopupMenu popup = new PopupMenu(this,view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu_more, popup.getMenu());
        popup.show();
    }


    public void Home(MenuItem item) {
        webView.loadUrl("https://www.google.com");
    }

    public void Share(MenuItem item) {
    }

    public void Reload(MenuItem item) {
        webView.reload();
    }
    public void LoadURL(String url){
        try{
            if(!NetworkState.canConnection(MainActivity.this)){
                Toast.makeText(MainActivity.this, "Can not connect", Toast.LENGTH_SHORT);
            }else{
                if(existUrl(url)) {
                    if((!url.contains("https://"))&&(!url.contains("http://"))){
                        url = "https://" + url;
                    }
                }else{
                    url = "https://google.com/search?q="+url;
                }
                webView.loadUrl(url);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void goToHistory(MenuItem item){
        Intent myIntent = new Intent(MainActivity.this, History.class);
        myIntent.putExtra("add",false);

        startActivityForResult(myIntent,1);
    }

    public void getBookMarks(MenuItem item){
        Intent myIntent = new Intent(MainActivity.this, BookMarks.class);
        myIntent.putExtra("add",false);

        startActivityForResult(myIntent,1);
    }
    public  void addBookmark(MenuItem item){
        Intent myIntent = new Intent(MainActivity.this, BookMarks.class);
        myIntent.putExtra("add",true);

        myIntent.putExtra("name",search_query.getText().toString());
        myIntent.putExtra("path",search_query.getText().toString());

        startActivityForResult(myIntent,1);
    }

    public void Settings(MenuItem item) {
        Intent myIntent = new Intent(MainActivity.this, Settings.class);
        startActivity(myIntent);
    }

    public void Tabs(View view) {
        Intent myIntent = new Intent (MainActivity.this, Tabs.class);
        startActivityForResult(myIntent,1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        String name = data.getStringExtra("newTab");
        if(name.equals("true")){
            String url;
            TabsWork mainTab = new TabsWork<MainActivity>("mainTab","mainTab", this,-100,-100);
            ContentValues contentValues =(ContentValues) mainTab.getData().get(0);
            url = contentValues.get("path").toString();
            search_query.setText(url);
            changeUrl();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }
}