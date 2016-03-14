package com.alin.card;

import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alin.card.common.Constant;
import com.alin.card.encrypt.SHA256_BASE64;

public class LoginActivity extends Activity implements OnClickListener {

	private EditText edPwd;
	private Button btnCancel;
	private Button btnLogin;

	private SharedPreferences preferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		preferences = getSharedPreferences(Constant.SHARED_NAME,
				Context.MODE_PRIVATE);

		initView();

	}

	private void initView() {
		edPwd = (EditText) findViewById(R.id.pwd);
		btnCancel = (Button) findViewById(R.id.cancel);
		btnLogin = (Button) findViewById(R.id.login);
		
//		edPwd.setText("Alin@0826");

		btnCancel.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel:
			finish();
			break;

		case R.id.login:
			if (doLogin(edPwd.getText().toString().trim())) {
				Toast.makeText(getBaseContext(), "Login Successed!", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(getBaseContext(), InfoListActivity.class));
				finish();
			} else {
				Toast.makeText(getBaseContext(), "Login Failed!", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}

	private boolean doLogin(String pwd) {

		if (null == pwd || "".equals(pwd)) {
			return false;
		}

		String sharedPsw;
		try {
			pwd = SHA256_BASE64.execute(pwd.getBytes());

			sharedPsw = preferences.getString(Constant.SHARED_PWD,
					SHA256_BASE64.execute(Constant.DEFAULT_PWS.getBytes()));

			if (pwd.equals(sharedPsw)) {
				return true;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return false;
	}

}
