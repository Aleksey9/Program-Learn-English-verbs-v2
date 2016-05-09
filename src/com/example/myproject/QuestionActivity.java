package com.example.myproject;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionActivity extends Activity{
	
	/*
	 * Основные поля:
	 */
	
    CountDownTimer cdt;
    int progress = 0;
    int k = 0;
    // идентификатор диалогового окна AlertDialog с кнопками
    	private final int IDD_RADIO_QUESTIONS = 0;
    boolean bool;
	
	//создаём объект "глобальный вопрос"
		static Question q1 = new Question();
		
	//массив вопросных строк для одной группы вопросов
		ArrayList <String> q_g = new ArrayList();
		
	//номер случайной группы вопросов
		static int numberOfRandomGroupQuestion;
		
	//индекс номера случайной группы вопросов
		static int indexNumberOfRandomGroupQuestion;
		
	//кол-во спрошенных вопросов в группе
		static int CountAskQuestion = 0;
		
	//счётчик кол-ва правильных ответов в группе вопросов
		static int CorrectAnswers = 0;
		
	//получен ли ответ на вопрос
		static boolean AnswerTheQuestion;

		public final static String KEY = "com.example.myproject.KEY";
		
	//private DBManager dbManager;
		
	/*
	 * Функции и процедуры:
	 */
		
	/*************************************************************************************************************************
	 * Создание активности
	 */
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_question);
			
			MainActivity.dbManager = DBManager.getInstance(this);
			
			q1 = new Question();
			q_g.clear();
			CountAskQuestion = 0;
			CorrectAnswers = 0;
			AnswerTheQuestion = false;
			
			//генерируем случайный индекс номера группы вопросов
			indexNumberOfRandomGroupQuestion = (int) ((MainActivity.ArrQuestion.size())*Math.random());
			//!? это работает правильно только тогда, когда приведение к int отбрасывает дробную часть, а не округляет число
			
			//Создаём группу вопросов
			CreateGroupQuestions();
			
			showMessageStart("Загрузите приложение", "Задание");
			/*
			//задаём случайный вопрос
			AskQuestions();
			*/
	}	
	

	//вывод информационного сообщения
    public void showMessageStart(String message, String title)
    {
  	  AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
  	  dlgAlert.setMessage(message);
  	  dlgAlert.setTitle(title);
  	  //dlgAlert.setPositiveButton("OK", null);
  	  dlgAlert.setCancelable(false);
  	  dlgAlert.setPositiveButton("OK",
  			    new DialogInterface.OnClickListener() {
  			        public void onClick(DialogInterface dialog, int which) {
  			        	startTimer();
  			        }
  			    });
  	  dlgAlert.create().show();
    }
	
	/*************************************************************************************************************************
	 * Здесь создаём группу вопросов	
	 */
	public void CreateGroupQuestions() {
		
		//Получаем QuestionGroup из базы с загаданным индексом
		QuestionGroup qg = new QuestionGroup();
		//Получаем случайную группу вопросов
		numberOfRandomGroupQuestion = MainActivity.ArrQuestion.get(indexNumberOfRandomGroupQuestion);
		qg = MainActivity.dbManager.getDataById(numberOfRandomGroupQuestion);
		//Записываем QuestionGroup в ArrayList
		for (int i = 0; i < QuestionGroup.quantity_group; ++i) {
			q_g.add(qg.q_str[i]);
		}
		
	}
	
	
	/*************************************************************************************************************************
	 * Здесь задаём вопросы	
	 */
	public void AskQuestions() {
		int rand = (int) (q_g.size()*Math.random());
		
		if (CountAskQuestion == 0) {
			q1 = new Question(q_g.get(0));
			//output();
			q_g.remove(0);
		} else {
			q1 = new Question(q_g.get(rand));
			//output();
			q_g.remove(rand);
		}
		
	}
	
	/**Работа с таймером и диалогами*/
	boolean finishActivity = false;
	//запуск таймера
	void startTimer() {
    	final ProgressBar pb = (ProgressBar) this.findViewById(R.id.progressBar1Question);
    	final Context context = this;
    	if (progress < 100) {
	    	cdt = new CountDownTimer(3500, 250) {
	        	  		
	            public void onTick(long millisUntilFinished) {
	            	if ((k == 7) & bool) {
	            		//Обнуляем счётчик кол-ва ответов:
	    				//CountAskQuestion = 0;
	            			            		
	    				if (CorrectAnswers != 7) {
	    					Intent i = new Intent();
	    					i.putExtra(KEY, -1);
	    					setResult(RESULT_OK, i);
		    				//Обнуляем счётчик правильных ответов
	    					showMessageInformate("Вы ответили правильно на " + CorrectAnswers + " вопросов из 7", "Приложение не загружено");
	    					CorrectAnswers = -1;
	    					TextView tv = (TextView)((Activity) context).findViewById(R.id.textView1);
	    					tv.setText("Загрузка остановлена. Непредвиденная ошибка во время загрузки");
	    				}
	            	} else if (CorrectAnswers != -1) {
	            		pb.setProgress(progress++);
	            	}
	            	bool = false;
	            }
	
	            public void onFinish() {
	            	if ((k < 7)& (!finishActivity)) {
	            		AskQuestions();
		                Dialog dd = onCreateDialog(IDD_RADIO_QUESTIONS);
	                	dd.show();
	                	bool = true;
	            	} else {
	    				if (CorrectAnswers == 7) {
	    					// Делаем намерение
	    					Intent i = new Intent();
	    					i.putExtra(KEY, numberOfRandomGroupQuestion);
	    					setResult(RESULT_OK, i);
		    				//Обнуляем счётчик правильных ответов
	    					showMessageInformate("Вы ответили на все вопросы правильно!", "Приложение загружено успешно");
	    				}
	            	}
	            }
	        }.start();
    	}
    }
	
	void finAct(){
		finish();
	}
	
	//вывод информационного сообщения
    public void showMessageInformate(String message, final String title)
    {
  	  AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
  	  dlgAlert.setMessage(message);
  	  dlgAlert.setTitle(title);
  	  //dlgAlert.setPositiveButton("OK", null);
  	  dlgAlert.setCancelable(false);
  	  dlgAlert.setPositiveButton("OK",
  			    new DialogInterface.OnClickListener() {
  			        public void onClick(DialogInterface dialog, int which) {
  			        	k+=1;
                        if ((progress < 100) & !finishActivity) {
                        	if ((title.toString().equals("Правильно!")) || (k == 7)) {
                        		startTimer();
                        	} else if (k < 7) {
                        		AskQuestions();
        		                Dialog dd = onCreateDialog(IDD_RADIO_QUESTIONS);
        	                	dd.show();
                        	}
                        		
                        }
                        if (finishActivity) {
                        	CorrectAnswers = 0;
                        	//cdt.cancel();
                        	//cdt.onFinish();
                        	finAct();
                        }
  			        }
  			    });
  	  dlgAlert.create().show();
    }
    
    //Диалог вопросов
    boolean answers = false;
    String mess = null;
    int index;
    @Override
    protected Dialog onCreateDialog(int id) {
    	final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	String[] str = new String[q1.N];
    	for (int i = 0; i < q1.N; ++i) {
    		str[i] = q1.ans[i];
    	}
        final String[] finalMas = str;
        final int corAns = q1.correctAnswer;
        //builder = new AlertDialog.Builder(this);
        builder.setTitle(q1.name)
                .setCancelable(false)
                //
                .setPositiveButton("Ответить", 
                		new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int id) {
                            	if ((mess != null) & answers) {
                            		dialog.cancel();
                                	String message = q1.name + "\n\n";
                                	if (mess.toString().equals("Правильно!")) {
                                		CorrectAnswers += 1;
                                		for (int i = 0; i < finalMas.length; ++i){
                                			if (i != index) {
                                				message += "✐  " + finalMas[i] + "\n";
                                			} else {
                                				message += "✔  " + finalMas[i] + "\n";
                                			}
                                    	}
                                	} else {
                                		for (int i = 0; i < finalMas.length; ++i){
                                			if (i == index) {
                                				message += "✘  " + finalMas[i] + "\n";
                                			} else if (i != corAns) {
                                				message += "✐  " + finalMas[i] + "\n";
                                			} else {
                                				message += "✅  " + finalMas[i] + "\n";
                                			}
                                    	}
                                	}
                                	showMessageInformate(message, mess);
                                	answers = false;
                            	}
                            }
                        })

                // добавляем переключатели
                .setSingleChoiceItems(finalMas, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int item) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Выбрано: "
                                                + finalMas[item],
                                        Toast.LENGTH_LONG).show();
                                if (item == corAns) {
                                	mess = "Правильно!";
                                } else {
                                	mess = "Неправильно! Правильный ответ " + q1.ans[q1.correctAnswer];
                                }
                                answers = true;
                                index = item;
                            }
                        });
        return builder.create();
    }
    
    //создание меню
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
	
	protected void onDestroy() {
		super.onDestroy();
	}
}
