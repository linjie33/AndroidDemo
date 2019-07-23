package com.linjie.databasedemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new MyDatabaseHelper(this,"BookStore.db",null,2);
        Button creatDatabase = (Button)findViewById(R.id.create_database);
        creatDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.getWritableDatabase();
            }
        });

        Button addData = (Button)findViewById(R.id.add_data);
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                //开始组装第一条数据
                values.put("name","The Da Vinci Code");
                values.put("author","Dan Brown");
                values.put("pages",454);
                values.put("price",16.96);
                db.insert("Book",null,values);//输入第一条数据
                values.clear();
                //开始组装第二天数据
                values.put("name","The Lost Symbol");
                values.put("author","Dan Brown");
                values.put("pages",510);
                values.put("price",19.98);
                db.insert("Book",null,values);
            }
        });

        Button updateData = (Button)findViewById(R.id.updata_data);
        updateData.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("price",10.00);
                db.update("Book",values,"name=?",new String[]{"The Da Vinci Code"});
            }
        });

        Button deleteData = (Button)findViewById(R.id.delete_data);
        deleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           SQLiteDatabase db = dbHelper.getWritableDatabase();
           db.delete("Book","pages>?",new String[]{"500"});
            }
        });

        Button queryButton = (Button)findViewById(R.id.query_data);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db =  dbHelper.getWritableDatabase();
                //查询BOOK表中的数据
                Cursor cursor  = db.query("Book",null,null,null,null,null,null);
                if (cursor.moveToFirst()){
                    do{
                        //遍历Cursor对象，取出数据并打印
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        String author = cursor.getString(cursor.getColumnIndex("author"));
                        int pages = cursor.getInt(cursor.getColumnIndex("pages"));
                        double price =cursor.getDouble(cursor.getColumnIndex("price"));
                        Log.e("MainActivity","book name is"+ name);
                        Log.e("MainActivity","book author is"+ author);
                        Log.e("MainActivity","book page is"+ pages);
                        Log.e("MainActivity","book price is"+ price);



                    }while (cursor.moveToNext());
                }
                cursor.close();
            }
        });
    }
}
