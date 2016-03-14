package com.alin.card;

import com.alin.card.R.menu;
import com.alin.card.common.CardInfo;
import com.alin.card.common.Constant;
import com.alin.card.store.CardInfoOpenHelper;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class DetailInfoActivity extends Activity implements OnClickListener
{
	private EditText edName;
	private EditText edAccount;
	private EditText edPwd;
	private EditText edRemark;

	private ImageButton ibName;
	private ImageButton ibAccount;
	private ImageButton ibPwd;
	private ImageButton ibRemark;

	private CardInfoOpenHelper databaseHelper;

	private CardInfo mCardInfo;
	
	private String whereName;
	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_info);

		databaseHelper = new CardInfoOpenHelper(this);
		whereName = getIntent().getStringExtra(Constant.NAME);
		type = getIntent().getIntExtra(Constant.TYPE, Constant.ADD);
		
		initView();
		initData();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void initView()
	{
		edName = (EditText) findViewById(R.id.name);
		edAccount = (EditText) findViewById(R.id.account);
		edPwd = (EditText) findViewById(R.id.pwd);
		edRemark = (EditText) findViewById(R.id.remark);

		ibName = (ImageButton) findViewById(R.id.clear_name);
		ibAccount = (ImageButton) findViewById(R.id.clear_account);
		ibPwd = (ImageButton) findViewById(R.id.clear_pwd);
		ibRemark = (ImageButton) findViewById(R.id.clear_remark);

		if(Constant.SCAN != type)
		{
			setTextWatcher(edName, ibName);
			setTextWatcher(edAccount, ibAccount);
			setTextWatcher(edPwd, ibPwd);
			setTextWatcher(edRemark, ibRemark);
		}

		ibName.setOnClickListener(this);
		ibAccount.setOnClickListener(this);
		ibPwd.setOnClickListener(this);
		ibRemark.setOnClickListener(this);

	}

	private void initData()
	{
		mCardInfo = getCardInfo(whereName);
		
		setDataToLayout(mCardInfo);
	}

	private void refreshLayout(int type, Menu menu)
	{
		switch (type)
		{
		case Constant.ADD:
			getMenuInflater().inflate(R.menu.add_menu, menu);
			menu.findItem(R.id.action_finish).setTitle(R.string.add);
			break;
		case Constant.MODIFY:
			getMenuInflater().inflate(R.menu.add_menu, menu);
			menu.findItem(R.id.action_finish).setTitle(R.string.modify);
			break;
		default:
			break;
		}
		setEditEnable(type);
	}

	private void setDataToLayout(CardInfo cardInfo)
	{
		if (null == cardInfo)
		{
			return;
		}
		edName.setText(cardInfo.getName());
		edAccount.setText(cardInfo.getAccount());
		edPwd.setText(cardInfo.getPassword());
		edRemark.setText(cardInfo.getRemark());
	}
	
	private CardInfo getDataFromLayout()
	{
		CardInfo cardInfo = new CardInfo();
		cardInfo.setName(edName.getText().toString().trim());
		cardInfo.setAccount(edAccount.getText().toString().trim());
		cardInfo.setPassword(edPwd.getText().toString().trim());
		cardInfo.setRemark(edRemark.getText().toString().trim());
		return cardInfo;
	}

	private CardInfo getCardInfo(String name)
	{
		if (null == name || null == databaseHelper)
		{
			return null;
		}
		return databaseHelper.onQueryByName(name);
	}

	private boolean addInfo()
	{
		if (null != databaseHelper)
		{
			if (null != mCardInfo && null != mCardInfo.getName() 
					&& !"".equals(mCardInfo.getName().trim()))
			{
				if(null != databaseHelper.onQueryByName(mCardInfo.getName()))
				{
					Toast.makeText(this, R.string.already_exist, Toast.LENGTH_SHORT).show();
					return false;
				}
				databaseHelper.onInsert(mCardInfo);
				return true;
			}
		}
		Toast.makeText(this, R.string.add_failed, Toast.LENGTH_SHORT).show();
		return false;
	}
	
	private boolean update()
	{
		if (null != databaseHelper)
		{
			if (null != mCardInfo && null != mCardInfo.getName()
					&& !"".equals(mCardInfo.getName().trim()))
			{
				if(!whereName.equals(mCardInfo.getName()) 
						&& null != databaseHelper.onQueryByName(mCardInfo.getName()))
				{
					Toast.makeText(this, R.string.already_exist, Toast.LENGTH_SHORT).show();
					return false;
				}
				databaseHelper.onUpdate(whereName, mCardInfo);
				return true;
			}
		}
		Toast.makeText(this, R.string.update_failed, Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		refreshLayout(type, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		
		switch (item.getItemId()) {
		case android.R.id.home:
			
			finish();
			
			break;
		case R.id.action_finish:

			mCardInfo = getDataFromLayout();
			if (Constant.MODIFY == type)
			{
				if(update())
				{
					finish();
				}
			}
			else if(Constant.ADD == type)
			{
				if(addInfo())
				{
					finish();
				}
			}
			else
			{
				finish();
			}
		
			break;

		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.clear_name:
			edName.setText("");
			break;
		case R.id.clear_account:
			edAccount.setText("");
			break;
		case R.id.clear_pwd:
			edPwd.setText("");
			break;
		case R.id.clear_remark:
			edRemark.setText("");
			break;
		default:
			break;
		}
	}
	
	private void setEditEnable(int type)
	{
		if(Constant.SCAN == type)
		{
			ibName.setVisibility(View.GONE);
			ibAccount.setVisibility(View.GONE);
			ibPwd.setVisibility(View.GONE);
			ibRemark.setVisibility(View.GONE);
			
			edName.setEnabled(false);
			edName.setFocusable(false);
			edAccount.setEnabled(false);
			edAccount.setFocusable(false);
			edPwd.setEnabled(false);
			edPwd.setFocusable(false);
			edRemark.setEnabled(false);
			edRemark.setFocusable(false);
		}
	}

	private void setTextWatcher(EditText editText, final ImageButton imageButton)
	{
		editText.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count)
			{
				if (s == null || s.length() == 0)
				{
					imageButton.setVisibility(View.GONE);
				}
				else
				{
					imageButton.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{

			}

			@Override
			public void afterTextChanged(Editable s)
			{

			}
		});
	}

}
