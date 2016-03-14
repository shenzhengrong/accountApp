package com.alin.card;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alin.card.common.CardInfo;
import com.alin.card.common.Constant;
import com.alin.card.store.CardInfoOpenHelper;

public class InfoListActivity extends Activity implements OnItemClickListener, 
			OnItemLongClickListener
{

	private ListView listView;

	private CardInfoAdapter infoAdapter;

	private Dialog listDialog;

	private String[] items;

	private CardInfoOpenHelper openHelper;

	private String currClickItemName;
	
	private final int DOUBLE_CLICK_FINISH = 0 ;
	
	private final int GAP_DELAY = 2000;
	
	private AtomicBoolean isDoubleClicked = new AtomicBoolean(false);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_list);

		openHelper = new CardInfoOpenHelper(this);
		items = new String[] { getString(R.string.add),
				getString(R.string.scan), getString(R.string.modify),
				getString(R.string.delete) };
		
		initView();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void initView()
	{
		listView = (ListView) findViewById(R.id.listview);
		infoAdapter = new CardInfoAdapter(this);
		listView.setAdapter(infoAdapter);
		infoAdapter.notifyData();

		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.set_menu, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		case R.id.action_add:
			add();
			break;
			
		case R.id.action_setting:
			goToSetting();
			break;

		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		CardInfo cardInfo = (CardInfo) infoAdapter.getItem(arg2);
		currClickItemName = cardInfo.getName();
		scan();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3)
	{

		CardInfo cardInfo = (CardInfo) infoAdapter.getItem(arg2);
		currClickItemName = cardInfo.getName();
		showDialogList();
		return false;
	}

	private void showDialogList()
	{
		if (null == listDialog)
		{
			listDialog = new Dialog(this);
			listDialog.setContentView(R.layout.dialog_list);
			listDialog.setTitle(R.string.operate);
			ListView listView = (ListView) listDialog
					.findViewById(R.id.long_click_list);
			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, items);
			listView.setAdapter(arrayAdapter);
			listDialog.setCanceledOnTouchOutside(true);
			listView.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3)
				{
					switch (arg2)
					{
					case 0:
						add();
						break;
					case 1:
						scan();
						break;
					case 2:
						modify();
						break;
					case 3:
						delete();
						break;
					default:
						break;
					}
					listDialog.dismiss();
				}
			});
		}
		listDialog.show();
	}

	private void goToSetting()
	{
		Intent intent = new Intent(getBaseContext(), SettingActivity.class);
		startActivity(intent);
	}
	
	private void add()
	{
		Intent intent = new Intent(getBaseContext(), DetailInfoActivity.class);
		intent.putExtra(Constant.TYPE, Constant.ADD);
		startActivity(intent);
	}

	private void modify()
	{
		Intent intent = new Intent(getBaseContext(), DetailInfoActivity.class);
		intent.putExtra(Constant.NAME, currClickItemName);
		intent.putExtra(Constant.TYPE, Constant.MODIFY);
		startActivity(intent);
	}

	private void scan()
	{
		Intent intent = new Intent(this, DetailInfoActivity.class);
		intent.putExtra(Constant.NAME, currClickItemName);
		intent.putExtra(Constant.TYPE, Constant.SCAN);
		startActivity(intent);
	}

	private void delete()
	{
		if(openHelper.onDelete(currClickItemName) < 1)
		{
			Toast.makeText(this, R.string.delete_failed, Toast.LENGTH_SHORT).show();
		}
		infoAdapter.notifyData();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		infoAdapter.notifyData();
	}

	private Handler mBackHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case DOUBLE_CLICK_FINISH:
				isDoubleClicked.set(false);
				break;

			default:
				break;
			}
		}
	};
	
	@Override
	public void onBackPressed() 
	{
		if(!isDoubleClicked.get()) {
			isDoubleClicked.set(true);
			Toast.makeText(this, R.string.tips_click_exit, Toast.LENGTH_SHORT).show();
			mBackHandler.sendEmptyMessageDelayed(DOUBLE_CLICK_FINISH, GAP_DELAY);
		}else{
			mBackHandler.removeMessages(DOUBLE_CLICK_FINISH);
			super.onBackPressed();
		}
	}
	
}
