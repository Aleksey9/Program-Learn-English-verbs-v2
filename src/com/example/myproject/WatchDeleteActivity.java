package com.example.myproject;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WatchDeleteActivity extends Activity {

	//private static DBManager dbManager;
	
	static Integer ng;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch_delete);
		
		//dbManager = MainActivity.dbManager;
		MainActivity.dbManager = DBManager.getInstance(this);
		ng = MainActivity.dbManager.getLastId(DBManager.TABLE_NAME);
		ng = 0;
		goRecord(ng, this);
	}
	
	public static void showDeleteMessage(final Context context)
    {
  	  AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
  	  dlgAlert.setTitle("Вы действительно хотите удалить " + (ng+1) + " вопрос?");
  	  dlgAlert.setPositiveButton("OK", null);
  	  dlgAlert.setCancelable(true);
  	  dlgAlert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						
					}
				}).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
  			        public void onClick(DialogInterface dialog, int which) {
  			        	//Удаляем текущую запись
  			        	MainActivity.dbManager.deleteData(ng);
  						//Обновляем текущую группу
  						goRecord(ng, context);
  						//Выводим сообщение, что вопрос успешно удалён
  						MainActivity.showMessage(context, "Запись " + (ng+1) + " успешно удалена!", "Удаление записи");
  			        	
  			        }
  			    });
  	  dlgAlert.create().show();
    }
	
	static private void goRecord(int number_of_group, Context context) {
		if (!((number_of_group < 0) || (number_of_group > MainActivity.dbManager.getLastId(DBManager.TABLE_NAME)))) {
			ng = number_of_group;
			QuestionGroup qg = new QuestionGroup();
			qg = MainActivity.dbManager.getDataById(number_of_group);
			Question[] question = new Question[qg.quantity_group];
			for (int i = 0; i < qg.quantity_group; ++i) {
				question[i] = new Question(qg.q_str[i]);
			}
			int len = QuestionGroup.quantity_group*(Question.N + 2);
			//заполняем массив всеми вопросами и ответами на них
			String[] strMas = new String[len];
			for (int i = 0; i < len; ++i) {
				if ((i % (Question.N + 2)) == 0) {
					strMas[i] = "В О П Р О С       № " + ((i / (Question.N + 2)+1));
				} else if ((i % (Question.N + 2)) == 1) {
					strMas[i] = question[i / (Question.N + 2)].name;
				} else {
					strMas[i] = question[i / (Question.N + 2)].ans[(i % (Question.N + 2)) - 2];
				}
			}
			//устанавливаем массив на listView через адаптер
			ListView listv = (ListView)((Activity) context).findViewById(R.id.listView1);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, strMas);
			listv.setAdapter(adapter);
			//
			TextView tv = (TextView)((Activity) context).findViewById(R.id.textTitleWatch1);
			tv.setText("Просмотр " + (number_of_group+1));
			//
			TextView tv2 = (TextView)((Activity) context).findViewById(R.id.textWatchBase);
			tv2.setText("З А П И С Ь     " + (number_of_group+1) + "     И З     " + (MainActivity.dbManager.getLastId(DBManager.TABLE_NAME)+1));
			//
			ProgressBar pb1 = (ProgressBar) ((Activity) context).findViewById(R.id.progressBarWatch1);
			ProgressBar pb2 = (ProgressBar) ((Activity) context).findViewById(R.id.progressBarWatch2);
			int lastid = MainActivity.dbManager.getLastId(DBManager.TABLE_NAME);
			if (lastid > 0) {
				pb1.setProgress(number_of_group*100/lastid);
				pb1.setProgress(number_of_group*100/lastid);
				pb2.setProgress(number_of_group*100/lastid);
				pb2.setProgress(number_of_group*100/lastid);
			} else {
				pb1.setProgress(100);
				pb1.setProgress(100);
				pb2.setProgress(100);
				pb2.setProgress(100);
			}
		}
	}
	
	public void goNumberGroup(View v) {
		EditText et = (EditText)this.findViewById(R.id.editTextNumberGroup);
		if (!et.getText().toString().isEmpty()) {
			ng = new Integer(et.getText().toString()) - 1;
			int lastid = MainActivity.dbManager.getLastId(DBManager.TABLE_NAME);
			if ((ng != null) & (ng <= lastid)) {
				goRecord(ng, this);
			} else {
				//выводим сообщение, что введён не существующий индекс
				MainActivity.showMessage(this, "Операция невыполнима! Введён несуществующий индекс", "Ошибка");
			}
		} else {
			//выводим сообщение, что индекс не введён
			MainActivity.showMessage(this, "Операция невыполнима! Необходимо ввести индекс", "Ошибка");
		}
		
	}
	
	public void pressButtonWatchRight(View v) {
		//Переходим к следующей записи
		goRecord(ng + 1, this);
	}
	
	public void pressButtonWatchLeft(View v) {
		//Переходим к предыдущей записи
		goRecord(ng - 1, this);
	}
	
	public void pressButtonDelete(View v) {
		TextView tv2 = (TextView)this.findViewById(R.id.textWatchBase);
		showDeleteMessage(this);
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
