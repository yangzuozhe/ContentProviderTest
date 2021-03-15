package com.example.contentprovidertest;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.contentprovidertest.sql.MySQLiteOpenHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void openSql(){
        //获取了我们继承的 helper 的对象只执行这个语句还不能够创建数据库
        MySQLiteOpenHelper dbHelper = new MySQLiteOpenHelper(this, "BookStory.db", null, 1);
        //调用getReadableDatabase()或getWritableDatabase()才算真正创建或打开数据库
        SQLiteDatabase database = dbHelper.getWritableDatabase();
    }
}