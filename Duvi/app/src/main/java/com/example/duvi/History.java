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

public class History extends AppCompatActivity{

    // String DATA_BASE_NAME = "mydata";
    //String TABLE_TABS = "tabs";
    //final String MAIN_TABS = "main";
    TabsWork tab ;
    Boolean add;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_tabs);
        tab = new TabsWork<History>("history","history",this,R.layout.history_tab_list_view, R.id.tab_list);
        tab.showData(tab.getData());
    }
    public void clearTabs(View view){
        tab.clearDB();
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
