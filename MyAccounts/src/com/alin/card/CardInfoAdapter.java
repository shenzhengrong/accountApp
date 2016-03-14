package com.alin.card;

import java.util.ArrayList;
import java.util.List;

import com.alin.card.common.CardInfo;
import com.alin.card.store.CardInfoOpenHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CardInfoAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	private CardInfoOpenHelper openHelper;
	private List<CardInfo> cardInfos = new ArrayList<CardInfo>();

	public CardInfoAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		
		openHelper = new CardInfoOpenHelper(context);
		
		cardInfos = openHelper.onQueryAll();
	}
	
	public void notifyData() {
		cardInfos = openHelper.onQueryAll();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return cardInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return cardInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (null == view) {
			view = mInflater.inflate(R.layout.card_info_item, null);
		}
//		if (0 == (position % 2)) {
//			view.setBackgroundResource(R.color.pink);
//		} else {
//			view.setBackgroundResource(R.color.aqua);
//		}

		TextView tvName = (TextView) view.findViewById(R.id.name);
		TextView tvAccount = (TextView) view.findViewById(R.id.account);

		CardInfo cardInfo = cardInfos.get(position);

		tvName.setText(cardInfo.getName());
		tvAccount.setText(cardInfo.getAccount());

		return view;
	}

}
