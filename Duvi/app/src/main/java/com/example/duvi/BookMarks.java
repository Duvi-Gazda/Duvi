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

public class BookMarks extends AppCompatActivity{

    // String DATA_BASE_NAME = "mydata";
    //String TABLE_TABS = "tabs";
    //final String MAIN_TABS = "main";
    TabsWork tab ;
    Boolean add;
    TabsWork mainTab;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmarks);
        tab = new TabsWork<BookMarks>("bookmarks","bookmarks",this,R.layout.tab_list_view, R.id.tab_list);
        mainTab = new TabsWork<BookMarks>("mainTab","mainTab",this,-100,-100);
        Intent intent = getIntent();
        add = intent.getBooleanExtra("add",false);
        if(add){
            String name = intent.getStringExtra("name");
            String path = intent.getStringExtra("path");
            ArrayList<ContentValues> arrCon = tab.getData();
            arrCon.forEach(contentValues -> {
//                if(name.equals(contentValues.getAsString("name"))){
//                    add = false;
//                    return;
//                }
                if(path.equals(contentValues.getAsString("path"))){
                    add = false;
                    return;
                }
            });
            if(add){
                ContentValues cv = new ContentValues();
                cv.put("name",name);
                cv.put("path",path);
                tab.addTab(cv);
            }

        }
        tab.showData(tab.getData());
    }
    public void clearTabs(View view){
        tab.clearDB();
        tab.showData(tab.getData());
        Intent intent = new Intent();
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
        View v = (View) view.getParent().getParent();
        TextView path = v.findViewById(R.id.path);
        TextView id = v.findViewById(R.id.id);
        TextView name = v.findViewById(R.id.name);
        tab.deleteTabById(id.getText().toString());
        tab.showData(tab.getData());
        Intent intent = new Intent();
        intent.putExtra("newTab", "false");
        setResult(RESULT_OK, intent);
        finish();
    }

}
