package cn.deepkolos.schultegrid;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.ArrayMap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by deepkolos on 2018/3/25.
 */

public class DB extends SQLiteOpenHelper {
    private static DB db;
    private Context ctx;

    private DB(Context context) {
        super(context, "history.db", null, 1);
        ctx = context;
    }

    public static DB getInstance (Context context) {
        if (db == null)
            db = new DB(context);

        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE score (" +
                "time timestamp primary key not null default (datetime('now','localtime'))," +
                "size text," +
                "difficulty text," +
                "score real);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists score;");
        onCreate(sqLiteDatabase);
    }

    public void record (float score) {
        SharedPreferences sp = ctx.getSharedPreferences(Const.SETTING, Context.MODE_PRIVATE);
        String size = sp.getString(Const.SIZE, "");
        String difficluty = sp.getString(Const.DIFFICULTY, "");

        ContentValues values = new ContentValues();
        values.put(Const.SIZE, size);
        values.put(Const.DIFFICULTY, difficluty);
        values.put(Const.SCORE, score);
        getWritableDatabase().insert("score", null, values);
    }

    public ArrayList<HistoryItem> getHistory () {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<HistoryItem> list = new ArrayList<>();

        Cursor cursor = db.rawQuery("select datetime(time), size, difficulty, score from score order by time desc limit 100", null);

        try {
            cursor.moveToFirst();
            list.ensureCapacity(cursor.getCount());

            do {
                list.add(new HistoryItem(
                    cursor.getString(0), // time
                    cursor.getString(1), // size
                    cursor.getString(2), // difficulty
                    cursor.getFloat(3)   // score
                ));
            } while (cursor.moveToNext());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return list;
    }

    public ArrayMap<String, String> getBest () {
        ArrayMap<String, String> map = new ArrayMap<>();

        Cursor cursor = db.getReadableDatabase().rawQuery("select size, min(score) best from score group by size", null);

        try {
            cursor.moveToFirst();
            do {
                map.put(cursor.getString(0), cursor.getString(1) + 's');
            } while (cursor.moveToNext());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return map;
    }

    public static class HistoryItem {
        private String size;
        private String difficulty;
        private String time;
        private float score;

        public String getSize() {
            return size;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public String getTime() {
            return time;
        }

        public float getScore() {
            return score;
        }

        public HistoryItem(String time, String size, String difficulty, float score) {
            this.size = size;
            this.difficulty = difficulty;
            this.time = time;
            this.score = score;
        }
    }

}
