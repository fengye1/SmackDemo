package com.lvpf.samckdemo.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLite extends SQLiteOpenHelper {

	public SQLite(Context context, String name,
				  CursorFactory factory, int version) {
		// 数据库保持位置<包>/databases
		super(context, name, factory, version);
	}

	@Override
	// 数据库第一次被创建时调用
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE lm_notice " + "("
				+ "  id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ "  server_client int NOT NULL default 0 ,"
				+ " from_user_type int NOT NULL default 0 ,"
				+ " from_user_id int NOT NULL default 0,"
				+ "other_id int NOT NULL default 0,"
				+ "notice_type int NOT NULL default 0,"
				+ " notice_content varchar(256) NOT NULL default \'\' ,"
				+ " add_time datetime not null default '2015-08-01',"
				+ " update_time datetime not null default '2015-08-01'" + ");");
		
		db.execSQL("" +
				"CREATE TABLE [im_msg_his] (" +
				"	[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
				"   [content] TEXT, " +
				"	[msg_from] NVARCHAR, " +
				"	[msg_to] NVARCHAR, " +
				"	[msg_time] TEXT, " +
				"	[msg_type] INTEGER " +
				");");
		
		//content,msg_from, msg_type,msg_time,thread_id
		
		db.execSQL("CREATE TABLE [im_notice]  " +
				"	([_id] INTEGER NOT NULL  PRIMARY KEY AUTOINCREMENT, " +
				"	[type] INTEGER, " +
				"	[title] NVARCHAR, " +
				"	[content] TEXT, " +
				"	[notice_from] NVARCHAR, " +
				"	[notice_to] NVARCHAR, " +
				"	[notice_time] TEXT, " +
				"	[status] INTEGER);"
			);
	}
	@Override
	// 数据库文件的版本号发生变更时调用
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("ALTER TABLE person ADD phone VARCHAR(12) NULL");
	}
}
