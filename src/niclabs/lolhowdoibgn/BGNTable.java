package niclabs.lolhowdoibgn;

import android.content.Context;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class BGNTable extends SQLiteOpenHelper {
	
	String sqlCreate = "CREATE TABLE Keys (secretKey TEXT, id TEXT)";

	public BGNTable(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sqlCreate);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS Keys");
		db.execSQL(sqlCreate);
		
	}

}
