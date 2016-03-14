package com.alin.card;

import java.security.NoSuchAlgorithmException;

import com.alin.card.common.Constant;
import com.alin.card.encrypt.SHA256_BASE64;
import com.alin.card.store.CardInfoOpenHelper;
import com.alin.card.store.FileTool;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnClickListener
{

	private EditText edOriginalPwd;
	private EditText edNewPwd;
	private EditText edConfirmPwd;
	private Button btnConfirmModify;
	
	private EditText edPath;
	private Button btnImport;
	private Button btnExport;
	
	private SharedPreferences preferences;
	
	private CardInfoOpenHelper databaseHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		preferences = getSharedPreferences(Constant.SHARED_NAME,
				Context.MODE_PRIVATE);
		
		databaseHelper = new CardInfoOpenHelper(this);
		
		initView();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	private void initView()
	{
		edOriginalPwd = (EditText) findViewById(R.id.original);
		edNewPwd = (EditText) findViewById(R.id.newpwd);
		edConfirmPwd = (EditText) findViewById(R.id.confirm);
		btnConfirmModify = (Button) findViewById(R.id.confirm_modify_pwd);
		
		edPath = (EditText) findViewById(R.id.path);
		btnImport = (Button) findViewById(R.id.import_info);
		btnExport = (Button) findViewById(R.id.export_info);
		
		edPath.setText(Constant.D_FILE_NAME);
		
		btnConfirmModify.setOnClickListener(this);
		btnImport.setOnClickListener(this);
		btnExport.setOnClickListener(this);
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if(android.R.id.home == item.getItemId())
		{
			finish();
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.confirm_modify_pwd:
			String originalPwd = edOriginalPwd.getText().toString().trim();
			String newPwd = edNewPwd.getText().toString().trim();
			String confirmPwd = edConfirmPwd.getText().toString().trim();
			if(modifyPwd(originalPwd, newPwd, confirmPwd))
			{
				edOriginalPwd.setText("");
				edNewPwd.setText("");
				edConfirmPwd.setText("");
			}
			break;
		case R.id.import_info:
			String pathImport = edPath.getText().toString().trim();
			importInfo(pathImport);
			
			break;
		case R.id.export_info:
			String pathExport = edPath.getText().toString().trim();
			exportInfo(pathExport);
			
			break;
		default:
			break;
		}
	}
	
	private boolean modifyPwd(String originalPwd, String newPwd, String confirmPwd)
	{
		try
		{
			String sharedPsw = preferences.getString(Constant.SHARED_PWD,
					SHA256_BASE64.execute(Constant.DEFAULT_PWS.getBytes()));
			if(!sharedPsw.equals(SHA256_BASE64.execute(originalPwd.getBytes())))
			{
				Toast.makeText(this, R.string.original_pwd_err, Toast.LENGTH_SHORT).show();
				return false;
			}
			if(!newPwd.equals(confirmPwd))
			{
				Toast.makeText(this, R.string.confirm_pwd_err, Toast.LENGTH_SHORT).show();
				return false;
			}
			Editor editor = preferences.edit();
			editor.putString(Constant.SHARED_PWD, SHA256_BASE64.execute(newPwd.getBytes()));
			if(editor.commit())
			{
				return true;
			}
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		Toast.makeText(this, R.string.modify_pwd_err, Toast.LENGTH_SHORT).show();
		return false;
	}
	
	private boolean importInfo(String path)
	{
		int code = FileTool.importFile(databaseHelper, path);
		switch (code) {
		case FileTool.CODE_SUCCSESS:
			Toast.makeText(this, R.string.import_success, Toast.LENGTH_SHORT).show();
			return true;
		case FileTool.CODE_PATH_NOT_EXIST:
			Toast.makeText(this, R.string.path_not_exist, Toast.LENGTH_SHORT).show();
			
			return false;
		case FileTool.CODE_IMPORTFILE_FAILED:
			Toast.makeText(this, R.string.import_failed, Toast.LENGTH_SHORT).show();
			
			return false;

		default:
			return false;
		}
	}
	
	private boolean exportInfo(String path)
	{
		int code = FileTool.exportFile(databaseHelper, path);
		switch (code) {
		case FileTool.CODE_SUCCSESS:
			Toast.makeText(this, R.string.export_success, Toast.LENGTH_SHORT).show();
			return true;
		case FileTool.CODE_CREATE_FILE_FAILED:
			Toast.makeText(this, R.string.create_file_failed, Toast.LENGTH_SHORT).show();
			
			return false;
		case FileTool.CODE_FILE_ALREADY_EXIST:
			Toast.makeText(this, R.string.path_exist, Toast.LENGTH_SHORT).show();
			
			return false;
		case FileTool.CODE_EXPORTFILE_FAILED:
			Toast.makeText(this, R.string.export_failed, Toast.LENGTH_SHORT).show();
			
			return false;

		default:
			return false;
		}
	}
}
