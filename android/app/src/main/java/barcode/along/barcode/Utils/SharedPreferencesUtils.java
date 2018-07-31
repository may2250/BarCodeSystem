package barcode.along.barcode.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
    SharedPreferences sharedPreferences;

    public SharedPreferencesUtils(Context context, String fileName) {
        sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public static class ContentValue {
        String key;
        Object value;

        //通过构造方法来传入key和value
        public ContentValue(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    //一次可以传入多个ContentValue对象的值
    public void putValues(ContentValue... contentValues) {
        //获取SharePreference对象的编辑对象，才能进行数据的存储
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //数据分类和存储
        for (ContentValue contentValue : contentValues) {
            //如果是字符型类型
            if (contentValue.value instanceof String) {
                editor.putString(contentValue.key, contentValue.value.toString()).commit();
            }
            //如果是int类型
            if (contentValue.value instanceof Integer) {
                editor.putInt(contentValue.key, Integer.parseInt(contentValue.value.toString())).commit();
            }
            //如果是Long类型
            if (contentValue.value instanceof Long) {
                editor.putLong(contentValue.key, Long.parseLong(contentValue.value.toString())).commit();
            }
            //如果是布尔类型
            if (contentValue.value instanceof Boolean) {
                editor.putBoolean(contentValue.key, Boolean.parseBoolean(contentValue.value.toString())).commit();
            }

        }
    }

    //获取数据的方法
    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public boolean getBoolean(String key, Boolean b) {
        return sharedPreferences.getBoolean(key, b);
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key, -1);
    }

    //清除当前文件的所有的数据
    public void clear() {
        sharedPreferences.edit().clear().commit();
    }
}
