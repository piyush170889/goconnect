package com.devjiva.goconnect;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MoreUsersActivityNew extends GoconnectActivity implements OnClickListener {

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);
	getSupportActionBar().hide();
	setContentView(R.layout.activity_moreusers);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}	
	
}