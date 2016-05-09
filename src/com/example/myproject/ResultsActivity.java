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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ResultsActivity extends Activity {

	static Integer ng = 0;
	
	static ArrayList<QuestionGroup> data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);
		
		data = MainActivity.dbManager.getVerbsForName(MainActivity.Name);
		MainActivity.dbManager = DBManager.getInstance(this);
		TextView tv = (TextView)this.findViewById(R.id.textTitleWatch1Results);
		tv.setText("Ученик(ца) " + MainActivity.Name);
		TextView tv2 = (TextView)this.findViewById(R.id.textTitleWatch2Results);
		tv2.setText("Изучено глаголов: " + data.size());
		if (data.size() != 0) {
			ng = 0;
			goRecord(ng, this);
		} else {
			MainActivity.showMessage(this, "У ученика(цы) " + MainActivity.Name + " нет результатов", "Предупреждение");
		}
		
	}
	
	static private void goRecord(int number_of_group, Context context) {
		if (!(number_of_group < 0)) {
			ng = number_of_group;
			QuestionGroup qg = data.get(ng);
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
			ListView listv = (ListView)((Activity) context).findViewById(R.id.listView1Results);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_expandable_list_item_1, strMas);
			listv.setAdapter(adapter);
			//
			TextView tv2 = (TextView)((Activity) context).findViewById(R.id.textWatchBaseResults);
			tv2.setText("З А П И С Ь     " + (number_of_group+1) + "     И З     " + (data.size()));
			//
			ProgressBar pb1 = (ProgressBar) ((Activity) context).findViewById(R.id.progressBarWatch1Results);
			ProgressBar pb2 = (ProgressBar) ((Activity) context).findViewById(R.id.progressBarWatch2Results);
			int lastid = data.size()-1;
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
	
	public void pressButtonWatchLeftResults(View v) {
		if ((ng-1) >= 0) {
			ng--;
			goRecord(ng, this);
		}
	}
	
	public void pressButtonWatchRightResults(View v) {
		if ((ng+1) < data.size()) {
			ng++;
			goRecord(ng, this);
		}
	}
	
	public void pressButtonDeleteResults(View v) {
		showDeleteAllMessage();
	}
	
	public void showDeleteAllMessage()
    {
  	  AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
  	  dlgAlert.setTitle("Вы хотите удалить все результаты игрока " + MainActivity.Name + " ?");
  	  dlgAlert.setCancelable(true);
  	  dlgAlert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						
					}
				}).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
  			        public void onClick(DialogInterface dialog, int which) {
  			        	MainActivity.dbManager.deleteResultsForName(MainActivity.Name); 
  			        	finish();
  			        }
  			    });
  	  dlgAlert.create().show();
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
