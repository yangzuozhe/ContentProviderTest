package com.example.contentprovidertest.ContentProvider;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.contentprovidertest.sql.MySQLiteOpenHelper;

public class MyProvider extends ContentProvider {

    public static final int BOOK_DIR = 0;
    public static final int BOOK_ITEM = 1;
    public static final int CATEGORY_DIR = 2;
    public static final int CATEGORY_ITEM = 3;
    /**
     * SQL的 Helper 类
     */
    private MySQLiteOpenHelper mSqLiteOpenHelper;
    /**
     * UriMatcher类使用:在ContentProvider 中注册URI
     */
    private static final UriMatcher mUriMatcher;
    /**
     * 设置本 Provider 的唯一标识
     */
    public static final String AUTOHORITY = "com.example.contentprovidertest.contentprovider.myprovider";

    /**
     * 通过 UriMatcher 的 addURI 方法将URI添加到 UriMatcher 中
     * 然后下面的六个方法 中根据 BOOK_DIR 等这些标志，返回相应的注册码。
     */
    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //若URI资源路径 = content://com.example.contentprovidertest.contentprovider.myprovider/book,则返回注册码 BOOK_DIR
        mUriMatcher.addURI(AUTOHORITY, "book", BOOK_DIR);
        //若URI资源路径 = content://com.example.contentprovidertest.contentprovider.myprovider/book/#,则返回注册码 BOOK_ITEM
        //# 表示某一行，比如为22，就是表示第22行的数据
        mUriMatcher.addURI(AUTOHORITY, "book/#", BOOK_ITEM);
        mUriMatcher.addURI(AUTOHORITY, "category", CATEGORY_DIR);
        mUriMatcher.addURI(AUTOHORITY, "category/#", CATEGORY_ITEM);
    }

    /**
     * 初始化创建 内容提供器
     *
     * @return
     */
    @Override
    public boolean onCreate() {
        //可以在onCreate 的方法中做内容提供器的初始化，比如说，清空表的数据啊等等
        mSqLiteOpenHelper = new MySQLiteOpenHelper(getContext(), "BookStory.db", null, 1);
        return true;
    }

    /**
     * 内容提供器的查询方法
     *
     * @param uri
     * @param projection    要查询的列名  new String[]{"id", "name"}
     * @param selection     查询条件 "id=?"
     * @param selectionArgs 查询条件的参数  new String[]{"1"}
     * @param sortOrder     排列顺序
     * @return
     */
    @SuppressLint("Recycle")
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mSqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (mUriMatcher.match(uri)) {
            case BOOK_DIR:
                cursor = db.query("Book", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ITEM:
                //将 authority 后面的 path 和 id 用 "/"分开，第0个是路径，第1个是id
                //比如：content://com.example.contentprovidertest.contentprovider.myprovider/book/22
                //bookId = 22；
                String bookId = uri.getPathSegments().get(1);
                //实际上我们这里的 bookId 的操作，可以在外面做，因为直接通过传入 selection 和 selectionArgs 参数就可以达到同样的效果
                cursor = db.query("Book", projection, "id = ?", new String[]{bookId}, null, null, sortOrder);
                break;
            case CATEGORY_DIR:
                cursor = db.query("Category", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CATEGORY_ITEM:
                String categoryId = uri.getPathSegments().get(1);
                cursor = db.query("Category", projection, "id = ?", new String[]{categoryId}, null, null, sortOrder);
                break;
            default:
                break;
        }
        return cursor;
    }

    /**
     * 插入数据（和其他方法不一样，插入数据时，得到的注册码，dir 和 item 是可以共享的，因为添加数据是不需要特定在某一行的）
     *
     * @param uri
     * @param values 插入的值
     * @return
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mSqLiteOpenHelper.getWritableDatabase();
        Uri uriReturn = null;
        //如果是 insert 插入数据，那么由于，插入数据是不需要区分 当前的 uri 是 行还是 id 的，因此行和id 的代码做同样的操作
        switch (mUriMatcher.match(uri)) {
            case BOOK_DIR:
            case BOOK_ITEM:
                long newBookId = db.insert("Book", null, values);
                uriReturn = Uri.parse("content://" + AUTOHORITY + "/book/" + newBookId);
                break;
            case CATEGORY_DIR:
            case CATEGORY_ITEM:
                long categoryId = db.insert("Category", null, values);
                uriReturn = Uri.parse("content://" + AUTOHORITY + "/category/" + categoryId);
                break;
            default:
                break;
        }
        return uriReturn;
    }

    /**
     * 删除数据
     *
     * @param uri
     * @param selection     查询条件 "id=?"
     * @param selectionArgs 查询条件的参数  new String[]{"1"}
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mSqLiteOpenHelper.getWritableDatabase();
        int deleteId = 0;
        switch (mUriMatcher.match(uri)) {
            case BOOK_DIR:
                deleteId = db.delete("Book", selection, selectionArgs);
                break;
            case BOOK_ITEM:
                String bookId = uri.getPathSegments().get(1);
                deleteId = db.delete("Book", "id = ?", new String[]{bookId});
                break;
            case CATEGORY_DIR:
                deleteId = db.delete("Category", selection, selectionArgs);
                break;
            case CATEGORY_ITEM:
                String categoryId = uri.getPathSegments().get(1);
                deleteId = db.delete("Category", "id = ?", new String[]{categoryId});
                break;
            default:
                break;
        }
        return deleteId;
    }

    /**
     * 更新数据
     *
     * @param uri
     * @param values        要更新的值
     * @param selection     查询条件 "id=?"
     * @param selectionArgs 查询条件的参数  new String[]{"1"}
     * @return
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mSqLiteOpenHelper.getWritableDatabase();
        int updateId = 0;
        switch (mUriMatcher.match(uri)) {
            case BOOK_DIR:
                updateId = db.update("Book", values, selection, selectionArgs);
                break;
            case BOOK_ITEM:
                String bookId = uri.getPathSegments().get(1);
                updateId = db.update("Book", values, "id = ?", new String[]{bookId});
                break;
            case CATEGORY_DIR:
                updateId = db.update("Category", values, selection, selectionArgs);
                break;
            case CATEGORY_ITEM:
                String categoryId = uri.getPathSegments().get(1);
                updateId = db.update("Category", values, "id = ?", new String[]{categoryId});
                break;
            default:
                break;
        }
        return updateId;
    }

    /**
     * 通过getType方法设置 uri 所需要返回的 mime 类型的值
     *
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        //实际上dir和 item “/” 后面所相对的就是自定义的字符串。
        String dirType = "vnd.android.cursor.dir/" + AUTOHORITY;
        String itemType = "vnd.android.cursor.item/" + AUTOHORITY;
        switch (mUriMatcher.match(uri)) {
            case BOOK_DIR:
                return dirType + ".book";
            case BOOK_ITEM:
                return itemType + ".book";
            case CATEGORY_DIR:
                return dirType + ".category";
            case CATEGORY_ITEM:
                return itemType + ".category";
            default:
                break;
        }
        return null;
    }
}
