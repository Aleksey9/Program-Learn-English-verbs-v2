package com.example.myproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RecordsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_records);
		
		//Получаем ассоциативный массив
		Map<String, Integer> hm = new HashMap<String, Integer>();
		hm = MainActivity.dbManager.getRecords();
		if (hm.size() > 0) {
			//Разбиваем ассоциативный массив на два списка - список ключей и список значений
			//(ключи - это имена, значения - это результаты)
			ArrayList<String> keysList = new ArrayList<String>();
			ArrayList<Integer> valuesList = new ArrayList<Integer>();
			for (Map.Entry entry : hm.entrySet()) {
				keysList.add((String) entry.getKey());
				valuesList.add((Integer) entry.getValue());
			}
			//Создаём два массива для значений имён и результатов
			String[] keys = new String[keysList.size()];
			for (int i = 0; i < keysList.size(); ++i) {
				keys[i] = keysList.get(i);
			}
			Integer[] values = new Integer[valuesList.size()];
			for (int i = 0; i < valuesList.size(); ++i) {
				values[i] = valuesList.get(i);
			}
			//Сортируем эти массивы
			sorted2mas(values, keys);
			
			//Помещаем все значения в массив ключей (переводя числа в строки)
			for (int i = 0; i < keys.length; ++i) {
				keys[i] = (i+1) + " место - " + keys[i] + " --> изучено глаголов: " + values[i];
			}
			
			//Выводим массив имён (ключей) с результатами в ListView
			ListView listv = (ListView)this.findViewById(R.id.listView1Records);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, keys);
			listv.setAdapter(adapter);
			
			TextView tv2 = (TextView)this.findViewById(R.id.textWatchBaseRecords);
			tv2.setText("Р Е З У Л Ь Т А Т Ы");
		} else {
			MainActivity.showMessage(this, "Результаты отсутствуют", "Предупреждение");
		}
		
	}
	
	void sorted2mas(Integer[] masInt, String[] masString) {
		int indexMaxVal = 0;
		for (int j = 0; j < masInt.length; ++j) {
			for (int i = j; i < masInt.length; ++i) {
				if (masInt[i] > masInt[indexMaxVal])
					indexMaxVal = i;
				//меняем местами минимальный и j-тый элементы:
				//в массиве masInt
				int x = masInt[j];
				masInt[j] = masInt[indexMaxVal];
				masInt[indexMaxVal] = x;
				//в массиве masString
				String st = masString[j];
				masString[j] = masString[indexMaxVal];
				masString[indexMaxVal] = st;
			}
		}
	}
	
	//Нажали на кнопку удалить все рекорды
	public void pressButtonDeleteRecords(View v) {
		showDeleteRecordsMessage();
	}
	
	public void showDeleteRecordsMessage()
    {
  	  AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
  	  dlgAlert.setTitle("Вы действительно хотите удалить все результаты?");
  	  dlgAlert.setCancelable(true);
  	  dlgAlert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						
					}
				}).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
  			        public void onClick(DialogInterface dialog, int which) {
  			        	MainActivity.dbManager.restartTable(); 
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
