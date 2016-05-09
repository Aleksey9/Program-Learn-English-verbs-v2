package com.example.myproject;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AddActivity extends Activity {

	//private DBManager dbManager;
	
	int progress = 0;
	int count = 0;
	String[] str = new String[QuestionGroup.quantity_group];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		
		MainActivity.dbManager = DBManager.getInstance(this);
				
		AddNextQuestion();
	}
	
	void AddNextQuestion() {
		TextView tv = (TextView) this.findViewById(R.id.textView1);
		tv.setText("Добавить вопрос " + (count + 1));
		
		//Определяем по id EditText
		EditText[] edt = {(EditText) this.findViewById(R.id.editText1), 
				(EditText) this.findViewById(R.id.editText2),
				(EditText) this.findViewById(R.id.editText3),
				(EditText) this.findViewById(R.id.editText4),
				(EditText) this.findViewById(R.id.editText5),
				(EditText) this.findViewById(R.id.editText6)};
		//Установим значения "по умолчанию":
		edt[0].setText("Вопрос " + (count+1));
		edt[1].setText("Прав. отв. на вопр. " + (count+1));
		edt[2].setText("Ответ 2 на вопр. " + (count+1));
		edt[3].setText("Ответ 3 на вопр. " + (count+1));
		edt[4].setText("Ответ 4 на вопр. " + (count+1));
		edt[5].setText("Ответ 5 на вопр. " + (count+1));
	}
	
	public void clickAdd(View v) {
		ProgressBar pb1 = (ProgressBar) this.findViewById(R.id.progressBar1);
		ProgressBar pb2 = (ProgressBar) this.findViewById(R.id.progressBar2);
				
		//Определяем по id EditText
		EditText[] edt = {(EditText) this.findViewById(R.id.editText1), 
				(EditText) this.findViewById(R.id.editText2),
				(EditText) this.findViewById(R.id.editText3),
				(EditText) this.findViewById(R.id.editText4),
				(EditText) this.findViewById(R.id.editText5),
				(EditText) this.findViewById(R.id.editText6)};
		
		
		int NewId = MainActivity.dbManager.getLastId(DBManager.TABLE_NAME) + 1;
		String st1 = "";
		for (int i = 0; i < (Question.N + 1); ++i) {
			st1 += edt[i].getText().toString() + "_";
		}
		st1 += "0";
		str[count] = st1;
		
		
		count += 1;
		progress += 15;
		pb1.setProgress(progress);
		pb2.setProgress(progress);
		if (count < 7) {
			AddNextQuestion();
		} else {
			count = 0;
			progress = 0;
			pb1.setProgress(progress);
			pb2.setProgress(progress);
			MainActivity.dbManager.addDataInTableQuestions(str, NewId);
			AddNextQuestion();
			MainActivity.showMessage(this, "Запись успешно добавлена!", "Добавление");
		}
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
