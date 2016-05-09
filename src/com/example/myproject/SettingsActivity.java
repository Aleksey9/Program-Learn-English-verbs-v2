package com.example.myproject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class SettingsActivity extends Activity {
	
	//private DBManager dbManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		//dbManager = DBManager.getInstance(this);
		
	}
	
	public void clickSettingsAdd(View v) {
		//Запускаем активность для добавления вопросов
		startActivity(new Intent(this, AddActivity.class));
		
	}
	
	public void clickWatch(View v) {
		//Запускаем активность для просмотра и удаления вопросов
		startActivity(new Intent(this, WatchDeleteActivity.class));
		
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.myproject, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    // Операции для выбранного пункта меню
	    switch (item.getItemId()) 
		{
	    case R.id.action_settings_1:
	    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	        return true;
	    case R.id.action_settings_2:
	    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
}
