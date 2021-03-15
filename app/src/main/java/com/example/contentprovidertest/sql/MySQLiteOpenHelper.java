package com.example.contentprovidertest.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    Context mContext;
    /**
     * 创建了一个名为Book的表
     * autoincrement 表示自增，每次插入一条新的数据，主键id都会+1
     */
    public static final String CREATE_BOOK = "create table Book(" +
            "id integer primary key autoincrement," +
            "author text," +
            "price real," +
            "name text)";

    public static final String CREATE_CATEGORY = "create table Category(" +
            "id integer primary key autoincrement," +
            "author text," +
            "price real)";

    /**
     * @param context 上下文
     * @param name    数据库的名称
     * @param factory 自定义的 Cursor
     * @param version 数据库版本号
     */
    public MySQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    /**
     * 第一次创建数据库的时候才会调用这个方法
     * 调用 getWritableDatabase() 或者 getReadableDatabase() 获取实例的时候，如果数据库不存在就调用这个方法
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //execSQL用于执行SQL语句
        db.execSQL(CREATE_BOOK);
        db.execSQL(CREATE_CATEGORY);
        Toast.makeText(mContext, "数据库创建成功，只有在第一次，数据库为空的时候才会使用onCreate 方法", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}