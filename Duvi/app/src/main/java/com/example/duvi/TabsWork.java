package com.example.duvi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

class TabsWork<Class extends AppCompatActivity>{
    String DbName;
    String tableName;
    public TabHelper dataBase;
    SQLiteDatabase SQLiteDatabase;
    Class aClass;
    Context context;
    int layoutID;
    int listID;

    public TabsWork(String DbName, String tableName, Class data,int layoutID, int listID){
        this.DbName = DbName;
        this.tableName = tableName;
        this.aClass = data;
        context = aClass;
        this.dataBase = new TabHelper(aClass);
        SQLiteDatabase = this.dataBase.getWritableDatabase();
        this.layoutID = layoutID;
        this.listID = listID;
    }

    class TabHelper extends SQLiteOpenHelper {

        public TabHelper(Context context){
            super(context, DbName, null, 1);
        };

        @Override
        public void onCreate(SQLiteDatabase db) {
            //    Log.d(LOG_TAG, "DB was created!");
            db.execSQL(
                    "create table "+ tableName+"("
                            +"id integer primary key autoincrement,"
                            +"name text,"
                            +"path text"
                            +");"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
    public class Tab{
        public String name;
        public String path;
        public String id;
        public Tab(String name, String path, String id){
            this.name = name;
            this.path = path;
            this.id = id;
        }
    }
    public class TabAdapter extends ArrayAdapter<Tab> {
        public TabAdapter(Context context, ArrayList<Tab> users){
            super(context,0,users);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(layoutID == -100)return convertView;
            Tab tab = getItem(position);
            if(convertView == null){
                convertView =  LayoutInflater.from(getContext()).inflate(layoutID, parent, false);
            }
            TextView Name = (TextView) convertView.findViewById(R.id.name);
            TextView Path = (TextView) convertView.findViewById(R.id.path);
            TextView Id = (TextView) convertView.findViewById(R.id.id);
            Name.setText(tab.name);
            Path.setText(tab.path);
            Id.setText(tab.id);
            return convertView;
        }

    }
    public void clearDB(){
        int clearCount = SQLiteDatabase.delete(tableName, null,null);
    }
    public void addTab(ContentValues cv){
        long rowId = SQLiteDatabase.insert(tableName, null,cv);
    }
    public void deleteTabById(String id){
        if(id.equalsIgnoreCase("")){
            return;
        }
        int deleteCount = SQLiteDatabase.delete(tableName, "id = "+ id, null);
    }
    public  void updateDB(String id,ContentValues cv){
        if(id.equalsIgnoreCase("")){
            return;
        }
        int updCount = SQLiteDatabase.update(tableName, cv, "id = ?", new String[]{id});
    }
//    public ArrayList<ContentValues> getSortedData(String[] paramName, String[] paramVal){
//        ArrayList<ContentValues> contentArr = new ArrayList<ContentValues>();
//        Cursor c = SQLiteDatabase.query(tableName, null, null,null,null,null,null);
//        if(c.moveToFirst()) {
//            int idColIndex = c.getColumnIndex("id");
//            int nameColIndex = c.getColumnIndex("name");
//            int pathColIndex = c.getColumnIndex("path");
//            do {
//                ContentValues cv = new ContentValues();
//                String str = c.getString(nameColIndex);
//                cv.put("name",str);
//                str = c.getString(pathColIndex);
//                cv.put("path",str);
//                str = c.getString(idColIndex);
//                cv.put("id",str);
//                contentArr.add(cv);
//            } while (c.moveToNext());
//        }
//        return contentArr;
//    }
    public ArrayList<ContentValues> getData(){
        ArrayList<ContentValues> contentArr = new ArrayList<ContentValues>();
        Cursor c = SQLiteDatabase.query(tableName, null, null,null,null,null,null);
        if(c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int pathColIndex = c.getColumnIndex("path");
            do {
                ContentValues cv = new ContentValues();
                String str = c.getString(nameColIndex);
                cv.put("name",str);
                str = c.getString(pathColIndex);
                cv.put("path",str);
                str = c.getString(idColIndex);
                cv.put("id",str);
                contentArr.add(cv);
            } while (c.moveToNext());
        }
        return contentArr;
    }
    public void showData(ArrayList<ContentValues> arr){
        if(layoutID == -100)return;
        Collections.reverse(arr);
        ArrayList<Tab> tabArrayList = new ArrayList<Tab>();
        for (ContentValues tab: arr) {
            Tab tab1 = new Tab(tab.getAsString("name"),tab.getAsString("path"),tab.getAsString("id"));
            tabArrayList.add(tab1);
        }
        TabAdapter adapter = new TabAdapter(aClass,tabArrayList);
        ListView listView = (ListView) aClass.findViewById(listID);
        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView id_view = parent.findViewById(R.id.id);
//                String idDB = id_view.getText().toString();
//                TabHelper db = new TabHelper(context);
//
//            }
//        });
    }
}
