package com.example.duvi;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Tabs extends AppCompatActivity{

    // String DATA_BASE_NAME = "mydata";
     //String TABLE_TABS = "tabs";
    //final String MAIN_TABS = "main";
    TabsWork tab ;
    TabsWork mainTab;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs);
        tab = new TabsWork<Tabs>("mydata","tabs",this,R.layout.tab_list_view, R.id.tab_list);
        mainTab = new TabsWork<Tabs>("mainTab","mainTab",this,-100,-100);
        tab.showData(tab.getData());
    }
    public void clearTabs(View view){
        tab.clearDB();
        tab.showData(tab.getData());
        checkMainTab();
        finish();
    }
    public void addTab(View view){
        ContentValues cv = new ContentValues();
        cv.put("name","home");
        cv.put("path","google.com");
        tab.addTab(cv);
        tab.showData(tab.getData());
        mainTab.clearDB();
        mainTab.addTab(cv);
        Intent intent = new Intent();
        intent.putExtra("newTab", "true");
        setResult(RESULT_OK, intent);
        finish();
    }
    public void goToTab(View view){
        TextView path = view.findViewById(R.id.path);
        TextView id = view.findViewById(R.id.id);
        TextView name = view.findViewById(R.id.name);

        ContentValues cv = new ContentValues();
        cv.put("name",name.getText().toString());
        cv.put("id",id.getText().toString());
        cv.put("path",path.getText().toString());
        mainTab.clearDB();
        mainTab.addTab(cv);
        Intent intent = new Intent();
        intent.putExtra("newTab", "true");
        setResult(RESULT_OK, intent);
        finish();
    }
    public void deleteTab(View view){
        // get parent view to take all components
        View v = (View) view.getParent().getParent();
        // get all components
        TextView path = v.findViewById(R.id.path);
        TextView id = v.findViewById(R.id.id);
        TextView name = v.findViewById(R.id.name);
        // delete tab
        tab.deleteTabById(id.getText().toString());
        tab = new TabsWork<Tabs>("mydata","tabs",this,R.layout.tab_list_view, R.id.tab_list);
        checkMainTab();
        // get main tab id
        ContentValues mainData =(ContentValues) mainTab.getData().get(0);
        String mainId = mainData.get("id").toString();
        // check if this tab is not a main tab
        if(mainId.equals(id.getText().toString())){
            mainTab.clearDB();
            // get first element from BD
            ContentValues firstElement = (ContentValues) tab.getData().get(0);
            mainTab.addTab(firstElement);
            // send to main activity that main tab had changed
            Intent intent = new Intent();
            intent.putExtra("newTab", "true");
            setResult(RESULT_OK, intent);
            finish();
        }
        Intent intent = new Intent();
        intent.putExtra("newTab", "false");
        setResult(RESULT_OK, intent);
        finish();

//        TabsWork mainTab = new TabsWork<Tabs>("mainTab","mainTab", this,-100,-100);
//        ContentValues contentValues =(ContentValues) mainTab.getData().get(0);
//        String mainTabId = contentValues.get("id").toString();
//        String tabId = id.getText().toString();
////        if (mainTabId.equals(tabId)){
//            ContentValues tabs =(ContentValues) tabr.getData().get(0);
//            ContentValues cv = new ContentValues();
//            cv.put("name",tabs.get("name").toString());
//            cv.put("id",tabs.get("id").toString());
//            cv.put("path",tabs.get("path").toString());
//            tab.deleteTabById(id.getText().toString());
//            mainTab.clearDB();
//            mainTab.addTab(cv);
//            Intent intent = new Intent();
//            intent.putExtra("newTab", "true");
//            setResult(RESULT_OK, intent);
//            finish();
////        }
//        tab.deleteTabById(id.getText().toString());
//        tab.showData(tab.getData());
//        Intent intent = new Intent();
//        intent.putExtra("newTab", "true");
//        setResult(RESULT_OK, intent);
//        finish();
    }
    private void checkMainTab(){
        // if it was last page create another one
        ArrayList c =  tab.getData();
        if(c.size() <= 0){
            ContentValues cv = new ContentValues();
            cv.put("name","home");
            cv.put("path","google.com");
            tab.addTab(cv);
            // main tab settings
            mainTab.clearDB();
            mainTab.addTab((ContentValues) tab.getData().get(0));
            // send to main activity that main tab had changed
            Intent intent = new Intent();
            intent.putExtra("newTab", "true");
            setResult(RESULT_OK, intent);
            finish();
        }
    }


}
