package com.example.myproject;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	//задаём кол-во групп вопросов в XML файле
		static final int count_of_group_questions_in_XML = 10;
	
	//кол-во изученных глаголов
		//static int count;
	
	static DBManager dbManager;
		
	//ключ для намерения
		static final private int NUMBER = 0;
	
	//надо (true) или не надо (false) записывать данные из XML файла в базу
		static boolean flag;
		
	//Имя вошедшего человека
		static String Name = "Вася";
		
	//Список неизученных глаголов
		static ArrayList <Integer> ArrQuestion = new ArrayList();
	
	//Создание главной активности
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (dbManager == null) {
			flag = false;
			dbManager = DBManager.getInstance(this);
			if (flag)
				WriteDataInBase(); // добавляем вопросы из XML файла
		}
		
		/*
		//Выводим результат (сколько глаголов изучили)
		TextView infoTextView = (TextView) findViewById(R.id.textViewResult);
		infoTextView.setText("Количество изученных глаголов: " + dbManager.getIndexVerbsForName(Name).size());
		*/
		TextView text_view = (TextView) findViewById(R.id.textTitle2ViewApp);
		text_view.setText("Общее количество глаголов: " + (dbManager.getLastId(DBManager.TABLE_NAME)+1));
		
		EditText edtT = (EditText) findViewById(R.id.editTextName);
		edtT.setText(Name);
	}
	
	//Записываем начальные данные в базу данных
	public void WriteDataInBase() {
		//создаём 7 массивов, в  каждом из которых вопросные строки разных групп вопросов из XML файла
		String[] q_1 = getResources().getStringArray(R.array.q_1);
		String[] q_2 = getResources().getStringArray(R.array.q_2);
		String[] q_3 = getResources().getStringArray(R.array.q_3);
		String[] q_4 = getResources().getStringArray(R.array.q_4);
		String[] q_5 = getResources().getStringArray(R.array.q_5);
		String[] q_6 = getResources().getStringArray(R.array.q_6);
		String[] q_7 = getResources().getStringArray(R.array.q_7);
		
		String[] str = new String[QuestionGroup.quantity_group];
		
		for (int i = 0; i < MainActivity.count_of_group_questions_in_XML; ++i) {
			str[0] = q_1[i];
			str[1] = q_2[i];
			str[2] = q_3[i];
			str[3] = q_4[i];
			str[4] = q_5[i];
			str[5] = q_6[i];
			str[6] = q_7[i];
			dbManager.addDataInTableQuestions(str, i);
		}
	}
	
	/*************************************************************************************************************************
	 * Вывод окна диалога
	 */
	public static void showMessage(Context context, String message, String title)
    {
  	  AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
  	  dlgAlert.setMessage(message);
  	  dlgAlert.setTitle(title);
  	  dlgAlert.setPositiveButton("OK", null);
  	  dlgAlert.setCancelable(true);
  	  dlgAlert.setPositiveButton("Ok",
  			    new DialogInterface.OnClickListener() {
  			        public void onClick(DialogInterface dialog, int which) {
  			        	
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
	
	
	//Обработка нажатия на стартовую кнопку
	public void StartButtonClicked(View v) {
		EditText edtT = (EditText) findViewById(R.id.editTextName);
		if (!edtT.getText().toString().equals("")) {
			Name = edtT.getText().toString();
			ArrayList<Integer> indexes = new ArrayList<Integer>();
			indexes = dbManager.getIndexVerbsForName(Name);
			//проверка: на все ли вопросы ответили
			if (indexes.size() <= dbManager.getLastId(DBManager.TABLE_NAME)) {
				//создаём ArrQuestion, состоящий из всех id таблицы Questions
				for (int i = 0; i <= dbManager.getLastId(DBManager.TABLE_NAME); ++i) {
					ArrQuestion.add(i);
				}
				
				//удаляем индексы групп вопросов из ArrQuestion, на которые ответил человек
				//(индексы отвеченных групп вопросов хранятся в indexes)
				for (int i = 0; i < ArrQuestion.size(); ++i) {
					int x = ArrQuestion.get(i);
					for (int j = 0; j < indexes.size(); ++j) {
						if (x == indexes.get(j)) {
							ArrQuestion.remove(ArrQuestion.indexOf(x));
						}
					}
				}
				
				startActivityForResult(new Intent(this, QuestionActivity.class), NUMBER);
			} else {
				//выводим: "Вы справились со всеми вопросами"
				showMessage(this, "Поздравляем! Вы справились со всеми вопросами!", "Результат");
				//infoTextView.setText("Поздравляем! Вы справились со всеми вопросами!");
			}
		} else {
			showMessage(this, "Введите имя!", "Ошибка");
		}
	}
	
	//Выдача результата
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//TextView infoTextView = (TextView) findViewById(R.id.textViewResult);

		if (requestCode == NUMBER) {
			if (resultCode == RESULT_OK) {
				int code = data.getIntExtra(QuestionActivity.KEY, 0);
				if (code >= 0) {
					//Добавляем вопрос в таблицу изученных вопросов
					dbManager.addDataInTableNames(Name, code);
					ArrQuestion.remove(ArrQuestion.indexOf(code));
					//Выводим результат (сколько глаголов изучили)
					ArrayList<Integer> indexes = new ArrayList<Integer>();
					indexes = dbManager.getIndexVerbsForName(Name);
					//infoTextView.setText("Количество изученных глаголов: " + indexes.size());
				}
			}
		}

	}
	
	//Обработка нажатия на кнопку настроек
	public void SettingsButtonClicked(View v) {
		//Запускаем активность настроек
		startActivity(new Intent(this, SettingsActivity.class));
	}
	
	//Обработка нажатия на кнопку результатов
	public void ResultsButtonClicked(View v) {
		EditText edtT = (EditText) findViewById(R.id.editTextName);
		if (!edtT.getText().toString().equals("")) {
			Name = edtT.getText().toString();
			//Запускаем активность результатов
			startActivity(new Intent(this, ResultsActivity.class));
		} else {
			showMessage(this, "Введите имя!", "Ошибка");
		}
	}
	
	//Обработка нажатия на кнопку рекордов
	public void RecordsButtonClicked(View v) {
		//Запускаем активность рекордов
		startActivity(new Intent(this, RecordsActivity.class));
	}
}
