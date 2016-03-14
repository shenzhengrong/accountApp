package com.alin.card.store;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.alin.card.common.CardInfo;
import com.alin.card.encrypt.BASE64;

public class CardInfoOpenHelper extends SQLiteOpenHelper
{

	private static final String DATABASE_NAME = "CardInfos.db";
	private static final int VERSION = 1;

	private static final String TABLE_NAME = "card";

	class CardInfoColumns
	{
		public static final String NAME = "name";
		public static final String ACCOUNT = "account";
		public static final String PASSWROD = "passwrod";
		public static final String REMARK = "remark";
	}

	public CardInfoOpenHelper(Context context)
	{
		super(context, DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String sql = "create table if not exists " + TABLE_NAME + "("
				+ CardInfoColumns.NAME + " varchar(50), "
				+ CardInfoColumns.ACCOUNT + " varchar(20), "
				+ CardInfoColumns.PASSWROD + " varchar(20), "
				+ CardInfoColumns.REMARK + " varchar(150))";
		db.execSQL(sql);

	}

	public long onInsert(CardInfo cardInfo)
	{
		SQLiteDatabase db = getWritableDatabase();

		String name = BASE64.encodeInfo(cardInfo.getName());
		String account = BASE64.encodeInfo(cardInfo.getAccount());
		String pwd = BASE64.encodeInfo(cardInfo.getPassword());
		String remark = BASE64.encodeInfo(cardInfo.getRemark());

		ContentValues values = new ContentValues();
		values.put(CardInfoColumns.NAME, name);
		values.put(CardInfoColumns.ACCOUNT, account);
		values.put(CardInfoColumns.PASSWROD, pwd);
		values.put(CardInfoColumns.REMARK, remark);

		return db.insert(TABLE_NAME, null, values);
	}

	public int onDelete(String name)
	{
		SQLiteDatabase db = getWritableDatabase();
		
		name = BASE64.encodeInfo(name);

		String whereClause = CardInfoColumns.NAME + "=" + "?";
		String[] whereArgs = new String[] { name };

		return db.delete(TABLE_NAME, whereClause, whereArgs);
	}

	public int onUpdate(String whereName, CardInfo cardInfo)
	{
		SQLiteDatabase db = getWritableDatabase();

		whereName = BASE64.encodeInfo(whereName);
		
		String name = BASE64.encodeInfo(cardInfo.getName());
		String account = BASE64.encodeInfo(cardInfo.getAccount());
		String pwd = BASE64.encodeInfo(cardInfo.getPassword());
		String remark = BASE64.encodeInfo(cardInfo.getRemark());

		ContentValues values = new ContentValues();
		values.put(CardInfoColumns.NAME, name);
		values.put(CardInfoColumns.ACCOUNT, account);
		values.put(CardInfoColumns.PASSWROD, pwd);
		values.put(CardInfoColumns.REMARK, remark);

		String whereClause = CardInfoColumns.NAME + "=" + "?";
		String[] whereArgs = new String[] { whereName };

		return db.update(TABLE_NAME, values, whereClause, whereArgs);
	}

	public List<CardInfo> onQueryAll()
	{
		SQLiteDatabase db = getReadableDatabase();

		String sql = "select * from " + TABLE_NAME;
		Cursor cursor = db.rawQuery(sql, new String[] {});

		CardInfo cardInfo = null;
		List<CardInfo> cardInfos = new ArrayList<CardInfo>();
		if (null != cursor)
		{
			while (cursor.moveToNext())
			{
				
				String name = cursor.getString(cursor
						.getColumnIndex(CardInfoColumns.NAME));
				name = BASE64.decodeInfo(name);
				
				String account = cursor.getString(cursor
						.getColumnIndex(CardInfoColumns.ACCOUNT));
				account = BASE64.decodeInfo(account);
				
				String pwd = cursor.getString(cursor
						.getColumnIndex(CardInfoColumns.PASSWROD));
				pwd = BASE64.decodeInfo(pwd);
				
				String remark = cursor.getString(cursor
						.getColumnIndex(CardInfoColumns.REMARK));
				remark = BASE64.decodeInfo(remark);

				cardInfo = new CardInfo();
				cardInfo.setName(name);
				cardInfo.setAccount(account);
				cardInfo.setPassword(pwd);
				cardInfo.setRemark(remark);
				cardInfos.add(cardInfo);
			}
		}
		return cardInfos;
	}

	public CardInfo onQueryByName(String name)
	{
		SQLiteDatabase db = getReadableDatabase();
		
		name = BASE64.encodeInfo(name);
		
		String sql = "select * from " + TABLE_NAME + " where "
				+ CardInfoColumns.NAME + "=?";
		Cursor cursor = db.rawQuery(sql, new String[] { name });

		CardInfo cardInfo = null;
		if (null != cursor)
		{
			if (cursor.moveToNext())
			{
				name = cursor.getString(cursor
						.getColumnIndex(CardInfoColumns.NAME));
				name = BASE64.decodeInfo(name);
				
				String account = cursor.getString(cursor
						.getColumnIndex(CardInfoColumns.ACCOUNT));
				account = BASE64.decodeInfo(account);
				
				String pwd = cursor.getString(cursor
						.getColumnIndex(CardInfoColumns.PASSWROD));
				pwd = BASE64.decodeInfo(pwd);
				
				String remark = cursor.getString(cursor
						.getColumnIndex(CardInfoColumns.REMARK));
				remark = BASE64.decodeInfo(remark);
				
				cardInfo = new CardInfo();
				cardInfo.setName(name);
				cardInfo.setAccount(account);
				cardInfo.setPassword(pwd);
				cardInfo.setRemark(remark);
			}
		}
		return cardInfo;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

		if (oldVersion != newVersion)
		{
			String sql = "dorp table if exist " + TABLE_NAME;
			db.execSQL(sql);

			onCreate(db);
		}
	}

}
