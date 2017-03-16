package com.lvpf.samckdemo.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lvpf.samckdemo.base.LMApplication;
import com.lvpf.samckdemo.model.UserSQL;

import java.util.ArrayList;
import java.util.List;

public class SQLService {

	private SQLite sqlite;
	private static SQLService mSqlService;//

	public SQLService(Context context) {
		int version = 1;
		this.sqlite = new SQLite(context, "smackSQLite.db", null, version);
	}

	public static SQLService getInstance() {
		if (mSqlService == null) {
			mSqlService = new SQLService(LMApplication.getInstance());
		}
		return mSqlService;

	}

	// 增
	public void save(UserSQL user) {
		SQLiteDatabase db = this.sqlite.getWritableDatabase();
		db.execSQL(
				"insert into lm_notice(server_client, from_user_type) values(?, ?)",
				new Object[] { user.getServer_client(),
						user.getFrom_user_type() });
	}

	// 增
	public void add(UserSQL user) {
		SQLiteDatabase db = this.sqlite.getWritableDatabase();
		 db.execSQL(
		 "insert into lm_notice(server_client, from_user_type,from_user_id,other_id,notice_type,notice_content) values(?, ?,?,?,?,?)",
		 new Object[] { user.getServer_client(),
		 user.getFrom_user_type(), user.getFrom_user_id(),
		 user.getOther_id(), user.getNotice_type(),
		 user.getNotice_content() });


	}

	// 删
	public void delete(Integer id) {
		SQLiteDatabase db = this.sqlite.getWritableDatabase();
		db.execSQL("delete from lm_notice where id=?", new Object[] { id });
	}



	/**
	 * 删除同一类型 根据type 和other_id删除
	 * 
	 * @param id
	 */
	public void deleteType(Integer id, Integer noticeOtherId) {
		SQLiteDatabase db = this.sqlite.getWritableDatabase();
		db.execSQL("delete from lm_notice where notice_type=? And other_id=?",
				new Object[] { id, noticeOtherId });
	}

	/**
	 * 删除同一类型且from_user_id 相同的 ddd传null
	 * 
	 * @param id
	 * @param user_id
	 * @param ddd
	 */
	public void deleteType(Integer id, Integer user_id, String ddd) {
		SQLiteDatabase db = this.sqlite.getWritableDatabase();
		db.execSQL(
				"delete from lm_notice where notice_type=? and from_user_id=?",
				new Object[] { id, user_id });
	}

	// 改
	public void update(UserSQL user) {
		SQLiteDatabase db = this.sqlite.getWritableDatabase();
		db.execSQL(
				"update lm_notice set server_client=?,from_user_type=? where id=?",
				new Object[] { user.getServer_client(),
						user.getFrom_user_type(), user.getId() });
	}

	// 查
	public UserSQL find(Integer id) {
		SQLiteDatabase db = this.sqlite.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from lm_notice where id=?",
				new String[] { id.toString() });
		if (cursor.moveToFirst()) {
			int userId = cursor.getInt(cursor.getColumnIndex("id"));
			int server_client = cursor.getInt(cursor
					.getColumnIndex("server_client"));
			int from_user_type = cursor.getInt(cursor
					.getColumnIndex("from_user_type"));
			return new UserSQL(userId, server_client, from_user_type);
		}
		cursor.close();
		return null;
	}

	/**
	 * 查notice_type 和noticeOtherId
	 * 
	 * @param noticetype
	 * @param noticeOtherId
	 * @return
	 */
	public boolean findLook(Integer noticetype, Integer noticeOtherId) {
		SQLiteDatabase db = this.sqlite.getReadableDatabase();
		Cursor cursor = db
				.rawQuery(
						"select * from lm_notice where notice_type=? And other_id=?",
						new String[] { noticetype.toString(),
								noticeOtherId.toString() });
		if (cursor.moveToFirst()) {
			return true;
		}
		cursor.close();
		return false;
	}

	/**
	 * 查,只查询notice_type;
	 * 
	 * @param noticetype
	 * @return
	 */
	public boolean findLook(Integer noticetype) {
		SQLiteDatabase db = this.sqlite.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from lm_notice where notice_type=?",
				new String[] { noticetype.toString() });
		if (cursor.moveToFirst()) {
			return true;
		}
		cursor.close();
		return false;
	}

	/**
	 * 查,查询notice_type; user_id, 类型，跟用户id， ddd才传null
	 * 
	 * @param noticetype
	 * @param notice_user_id
	 * @param ddd
	 * @return
	 */
	public boolean findLook(Integer noticetype, Integer notice_user_id,
							String ddd) {
		SQLiteDatabase db = this.sqlite.getReadableDatabase();
		Cursor cursor = db
				.rawQuery(
						"select * from lm_notice where notice_type=? and from_user_id=?",
						new String[] { noticetype.toString(),
								notice_user_id.toString() });
		if (cursor.moveToFirst()) {
			return true;
		}
		cursor.close();
		return false;
	}

	/**
	 * 查,查询notice_type; user_id, 类型，跟用户id ,传多种类型的
	 * 
	 * @param noticetype
	 * @param notice_user_id
	 * @return
	 */
	public boolean findLook(String noticetype, Integer notice_user_id) {
		SQLiteDatabase db = this.sqlite.getReadableDatabase();
		Cursor cursor = db
				.rawQuery(
						"select * from lm_notice where notice_type in(2,3,4,5,6,7,8,9) and from_user_id="
								+ notice_user_id, null);
		if (cursor.moveToFirst()) {
			return true;
		}
		cursor.close();
		return false;
	}

	/**
	 * 查,查询notice_type;传多种类型的
	 * 
	 * @param noticetype
	 * @return
	 */
	public boolean findLook(String noticetype) {
		System.out.println("----------------" + noticetype);
		SQLiteDatabase db = this.sqlite.getReadableDatabase();
		Cursor cursor = db
				.rawQuery(
						"select * from lm_notice where notice_type in (2,3,4,5,6,7,8,9)",
						null);
		// Cursor cursor = db.rawQuery(
		// "select * from lm_notice where notice_type in (?)",
		// new String[] {noticetype});
		if (cursor.moveToFirst()) {
			return true;
		}
		cursor.close();
		return false;
	}

	// 分页获取记录，跳过前offset条记录，查找maxResult条记录
	public List<UserSQL> getScrollData(int offset, int maxResult) {
		SQLiteDatabase db = this.sqlite.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from lm_notice order by id asc limit ?,?",
				new String[] { String.valueOf(offset),
						String.valueOf(maxResult) });
		List<UserSQL> users = new ArrayList<UserSQL>();
		while (cursor.moveToNext()) {
			int userId = cursor.getInt(cursor.getColumnIndex("id"));
			int server_client = cursor.getInt(cursor
					.getColumnIndex("server_client"));
			int from_user_type = cursor.getInt(cursor
					.getColumnIndex("from_user_type"));
			users.add(new UserSQL(userId, server_client, from_user_type));
		}
		cursor.close();
		return users;
	}

	// 获取记录条数
	public long getCount() {
		SQLiteDatabase db = this.sqlite.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from lm_notice", null);
		cursor.moveToFirst();
		long res = cursor.getLong(0);
		cursor.close();
		return res;
	}
}
