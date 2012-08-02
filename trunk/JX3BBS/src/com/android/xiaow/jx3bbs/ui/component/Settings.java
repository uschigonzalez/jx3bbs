package com.android.xiaow.jx3bbs.ui.component;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.xiaow.jx3bbs.Config;
import com.android.xiaow.jx3bbs.R;
import com.android.xiaow.jx3bbs.ui.LoginActivity;

public class Settings extends LinearLayout {
	RadioGroup radioGroup;
	Button login;

	public Settings(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Settings(Context context) {
		super(context);
		init();
	}

	public Settings(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setOrientation(LinearLayout.VERTICAL);
		init();
	}

	public void init() {
		addView(LayoutInflater.from(getContext()).inflate(R.layout.settings,
				this, false));
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		radioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
		login = (Button) findViewById(R.id.login);
		login.setOnClickListener(onClickListener);

		if (Config.ONLY_IMAGE)
			radioGroup.check(R.id.radio0);
		if (Config.ONLY_GRAVATAR)
			radioGroup.check(R.id.radio1);
		if (Config.ALL)
			radioGroup.check(R.id.radio2);
		if (Config.ALL_IN_WIFI)
			radioGroup.check(R.id.radio3);
		if (Config.OFF_ALL)
			radioGroup.check(R.id.radio4);
	}

	RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (group.getCheckedRadioButtonId()) {
			case R.id.radio0:
				Config.setType(true, Config.TYPE_ONLY_IMAGE);
				break;
			case R.id.radio1:
				Config.setType(true, Config.TYPE_ONLY_GRAVATAR);
				break;
			case R.id.radio2:
				Config.setType(true, Config.TYPE_ALL);
				break;
			case R.id.radio3:
				Config.setType(true, Config.TYPE_ALL_IN_WIFI);
				break;
			case R.id.radio4:
				Config.setType(true, Config.TYPE_OFF_ALL);
				break;

			}
		}
	};
	View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			getContext().startActivity(
					new Intent(getContext(), LoginActivity.class));
		}
	};
}
