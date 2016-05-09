package com.example.myproject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

	
	/*ПОЛЯ*/
	
	private Context context;
	//Название базы данных:
	private String DB_NAME = "questions_groups.db";
	//Назавание таблицы:
	public static String TABLE_NAME = "questions_groups";
	public static String TABLE_2_NAME = "names";

	//Объект базы данных:
	private SQLiteDatabase db;
	//Объект менеджера базы данных
	private static DBManager dbManager;

	
	
	/*МЕТОДЫ*/
	
	/**ФУНКЦИИ СОЗДАНИЯ*/
	//Функция, которая отдаёт готовый менеджер:
	public static DBManager getInstance(Context context) {
		if (dbManager == null) {
			dbManager = new DBManager(context);
		}
		return dbManager;
	}

	//Конструктор менеджера базы данных:
	private DBManager(Context context) {
		this.context = context;
		db = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
		//Создаём таблицы
		createTablesIfNeedBe();
	}

	//Функция создания таблиц
	private void createTablesIfNeedBe() {
		//если таблица не существует:
		if (!isTableExists(db, TABLE_NAME)) {
			
			//НАДО (true) записывать данные из XML файла в базу
			//И ТОЛЬКО В ЭТОМ СЛУЧАЕ НАДО!!!
			MainActivity.flag = true;
			
			
			//создаём таблицу TABLE_NAME
			String stroka = "CREATE TABLE " + TABLE_NAME + " (_ID INTEGER,";
			for (int i = 0; i < QuestionGroup.quantity_group; ++i) {
				stroka += " Q_STR" + i + " TEXT,";
			}
			stroka += " Q_KEY INTEGER); ";
			db.execSQL(stroka);
			/*
			//во избежание ошибок при определении индекса записываем "контрольную" строку в таблицу:
			stroka = "INSERT INTO " + TABLE_NAME + " VALUES (-1, ";
			for (int i = 0; i < QuestionGroup.quantity_group; ++i) {
				stroka += "'control_value" + i + "', ";
			}
			stroka += "500);";
			db.execSQL(stroka);
			*/
			
			//создаём таблицу TABLE_2_NAME
			db.execSQL("CREATE TABLE " + TABLE_2_NAME + " (_ID INTEGER, NAME TEXT, ID_QUESTION INTEGER); ");
		} else {
			//эта часть написана для разнообразия
			String stroka = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (_ID INTEGER,";
			for (int i = 0; i < QuestionGroup.quantity_group; ++i) {
				stroka += " Q_STR" + i + " TEXT,";
			}
			stroka += " Q_KEY INTEGER); ";
			db.execSQL(stroka);
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_2_NAME + " (_ID INTEGER, NAME TEXT, ID_QUESTION INTEGER); ");
		}
	}
	
	/**ПРОЦЕДУРЫ ДОБАВЛЕНИЯ*/
	//Добавление данных в таблицу:
	void addDataInTableQuestions(String[] q_str, int q_key) {
		String stroka = "INSERT INTO " + TABLE_NAME + " VALUES (" + (dbManager.getLastId(TABLE_NAME)+1) + ", '";
		for (int i = 0; i < QuestionGroup.quantity_group; ++i) {
			if (i == QuestionGroup.quantity_group - 1) {
				stroka += q_str[i] + "', ";
			} else
				stroka += q_str[i] + "', '";
		}
		stroka += q_key + ");";
		db.execSQL(stroka);
	}
	
	//Добавляем данные в таблицу имён
	void addDataInTableNames(String name, int id_question) {
		int Lastid = dbManager.getLastId(TABLE_2_NAME);
		db.execSQL("INSERT INTO " + TABLE_2_NAME + " VALUES (" + (Lastid+1) + ", '" + name + "', " + id_question + ");");
	}
	
	/**ПРОЦЕДУРЫ УДАЛЕНИЯ*/
	//Удаление данных с конкретным id
	void deleteData(int table_id) {
		
		//Удаляем запись с _ID = table_id из TABLE_NAME
		db.delete(TABLE_NAME, "_ID = " + table_id, null);
		
		/**Удаляем все записи с ID_QUESTION = table_id из TABLE_2_NAME
		меняя индексы _ID в TABLE_2_NAME*/
		
		
		//Создаём пустой ArrayList
		ArrayList<Integer> delete_id = new ArrayList<Integer>();
		//Создаём курсор для всей таблицы
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_2_NAME + ";", null);
		//Устанавливаем курсор на первую, контрольную запись
		boolean hasMoreData = cursor.moveToFirst();
		//Идём до конца таблицы
		while (hasMoreData) {
			//Сохраняем id текущей записи в таблице
			int id_of_record_in_table_names = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_ID")));
			//Сохраняем question текущей записи в таблице
			int question_of_record_in_table_names = Integer.parseInt(cursor.getString(cursor.getColumnIndex("ID_QUESTION")));
			//Если номер группы вопросов этой записи совпадает с id 1-ой таблицы, которое необходимо удалить, то
			if (question_of_record_in_table_names == table_id) {
				//записываем id этой записи (во 2-ой таблице) в список удаления:
				delete_id.add(id_of_record_in_table_names);
			}
			//переходим к следующей записи
			hasMoreData = cursor.moveToNext();
		}
		cursor.close();
		
		for (int i = 0; i < delete_id.size(); ++i) {
			//определяем i-тый индекс, запись с которым надо удалить
			int xi = delete_id.remove(0);
			//удаляем запись с этим индексом
			db.delete(TABLE_2_NAME, "_ID = " + xi, null);
			//уменьшаем на единицу все последующие записи
			for (int j = xi + 1; j <= dbManager.getLastId(TABLE_2_NAME); ++j) {
				db.execSQL("UPDATE " + TABLE_2_NAME + " SET _ID=" + (j - 1) + " WHERE _ID=" + j);
			}
		}
		/**__________________________________________________________*/
		
		//Уменьшаем индексы у записей ниже на единицу
		int x = table_id + 1;
		for (int i = x; i <= dbManager.getLastId(TABLE_NAME); ++i) {
			db.execSQL("UPDATE " + TABLE_NAME + " SET _ID=" + (i - 1) + " WHERE _ID=" + i);
			db.execSQL("UPDATE " + TABLE_2_NAME + " SET ID_QUESTION=" + (i - 1) + " WHERE ID_QUESTION=" + i);
		}
	}
	
	//Очистка таблицы TABLE_2_NAME
	void restartTable() {

		//Очистка таблицы TABLE_2_NAME
		int lastId = getLastId(TABLE_2_NAME);
		for (int i = 0; i <= lastId; ++i) {
			db.delete(TABLE_2_NAME, "_ID = " + i, null);
		}
	}
	
	//Удаление всех результатов человека с именем name
	void deleteResultsForName(String name) {
		db.delete(TABLE_2_NAME, "NAME = '" + name + "'", null);
	}

	/**ФУНКЦИИ ДЛЯ ПОЛУЧЕНИЯ ДАННЫХ*/
	//Функция, которая возвращает все данные в ArrayList
	ArrayList<QuestionGroup> getAllData(String tableName) {
		//Создаём пустой ArrayList
		ArrayList<QuestionGroup> data = new ArrayList<QuestionGroup>();
		//Создаём курсор для всей таблицы
		Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + ";", null);
		//Устанавливаем курсор на первую, контрольную запись
		boolean hasMoreData = cursor.moveToFirst();
		//Идём до конца таблицы
		while (hasMoreData) {
			//Сохраняем id текущей записи в таблице
			int table_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_ID")));
			//Сохраняем другие параметры текущей записи в таблице:
			String[] str = new String[QuestionGroup.quantity_group];
			for (int i = 0; i < QuestionGroup.quantity_group; ++i) {
				str[i] = cursor.getString(cursor.getColumnIndex("Q_STR" + i));
			}
			int q_key = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Q_KEY")));
			//Добавляем в ArrayList значения параметров текущей записи
			data.add(new QuestionGroup(table_id, str, q_key));
			//Переходим к следующей записи:
			hasMoreData = cursor.moveToNext();
		}
		cursor.close();
		//Возвращаем готовый ArrayList:
		return data;
	}
	
	//Функция, котрая возвращает запись в таблице с определённым индексом
	QuestionGroup getDataById(int table_id) {
		//Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE _ID=" + table_id + " ;", null);
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + ";", null);
		boolean hasMoreData = cursor.moveToPosition(table_id);
		//Если нашли запись:
		if (hasMoreData) {
			//Сохраняем параметры текущей записи в таблице:
			String[] str = new String[QuestionGroup.quantity_group];
			for (int i = 0; i < QuestionGroup.quantity_group; ++i) {
				str[i] = cursor.getString(cursor.getColumnIndex("Q_STR" + i));
			}
			int q_key = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Q_KEY")));
			//Добавляем в question_group значения параметров записи
			QuestionGroup question_group = new QuestionGroup(table_id, str, q_key);
			//Закрываем курсор (чтобы память не переполнялась)
			cursor.close();
			//Возвращаем нашу группу вопросов:
			return question_group;
		} else
			cursor.close();
			return null;		
	}

	//Функция возвращает индексы отвеченных групп вопросов человеком с именем name
	ArrayList<Integer> getIndexVerbsForName(String name) {
		//Создаём пустой ArrayList
		ArrayList<Integer> data = new ArrayList<Integer>();
		//Создаём курсор для всей таблицы
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_2_NAME + ";", null);
		//Устанавливаем курсор на первую, контрольную запись
		boolean hasMoreData = cursor.moveToFirst();
		//Идём до конца таблицы
		while (hasMoreData) {
			//берём имя из таблицы
			String strName = cursor.getString(cursor.getColumnIndex("NAME"));
			//если имя такое же, то
			if (name.equals(strName)) {
				//получаем id группы вопросов
				int id_question = Integer.parseInt(cursor.getString(cursor.getColumnIndex("ID_QUESTION")));
				data.add(id_question);
			}
			//переходим к следующей записи
			hasMoreData = cursor.moveToNext();
		}
		cursor.close();
		
		return data;
	}

	//Функция возвращает отвеченные группы вопросов человеком с именем name
	ArrayList<QuestionGroup> getVerbsForName(String name) {
		//Создаём пустой ArrayList
		ArrayList<QuestionGroup> data = new ArrayList<QuestionGroup>();
		//Создаём курсор для всей таблицы
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_2_NAME + ";", null);
		//Устанавливаем курсор на первую, контрольную запись
		boolean hasMoreData = cursor.moveToFirst();
		//Идём до конца таблицы
		while (hasMoreData) {
			//Сохраняем имя человека в текущей записи в таблице:
			String strName = cursor.getString(cursor.getColumnIndex("NAME"));
			//Если это имя совпадает с заданным в качестве параметров именем, то:
			if (name.equals(strName)) {
				//получаем id группы вопросов
				int id_question = Integer.parseInt(cursor.getString(cursor.getColumnIndex("ID_QUESTION")));
				//Получаем эту группу вопросов из 1-ой таблицы по id
				QuestionGroup qg = new QuestionGroup();
				qg = getDataById(id_question);
				//добавляем эту группу в наш список
				data.add(qg);
			}
			//Переходим к следующей записи:
			hasMoreData = cursor.moveToNext();
		}
		cursor.close();
		//Возвращаем готовый ArrayList:
		return data;
	}
	
	//Функция, возвращающая результаты всех участников
	Map<String, Integer> getRecords() {
		Map<String, Integer> hm = new HashMap<String, Integer>();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_2_NAME + ";", null);
		//Устанавливаем курсор на первую, контрольную запись
		boolean hasMoreData = cursor.moveToFirst();
		//Идём до конца таблицы
		while (hasMoreData) {
			//Получаем имя
			String name = cursor.getString(cursor.getColumnIndex("NAME"));
			//Если есть запись с этим именем, то
			if (hm.containsKey(name)) {
				//новое значение == старое значение + 1
				int val = hm.get(name) + 1;
				//добавляем это значение в ассоциативный массив
				hm.put(name, val);
			} else {
				//добавляем в ассоциативный массив запись с именем name и значением 1
				hm.put(name, 1);
			}
			//Переходим к следующей записи:
			hasMoreData = cursor.moveToNext();
		}
		cursor.close();
		//возвращаем ассоциативный массив
		return hm;
	}
	
	//Функция выводит id последней записи в таблице
	int getLastId(String tableName) {
		//Создаём курсор для всей таблицы
		Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + ";", null);
		//Устанавливаем курсор на последнюю запись
		boolean hasMoreData = cursor.moveToLast();
		int table_id;
		if (hasMoreData) {
			//Получаем id последней записи
			table_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_ID")));
			cursor.close();
		} else {
			table_id = -1;
		}
		//Выводим этот id:
		return table_id;
	}

	/**ФУНКЦИИ ОБСЛУЖИВАНИЯ*/
	//Функция проверяет, существует ли таблица
	private boolean isTableExists(SQLiteDatabase db, String tableName) {
		//если имени таблицы и объекта базы данных нет, то:
	    if (tableName == null || db == null || !db.isOpen()) {
	    	//таблицы не существует
	        return false;
	    }
	    //Проверка на существование таблицы
	    Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
	    //Если курсору некуда перемещаться, то:
	    if (!cursor.moveToFirst()) {
	    	//таблицы не существует
	        return false;
	    }
	    //Пытаемся переместить курсор:
	    int count = cursor.getInt(0);
	    //Убираем курсор
	    cursor.close();
	    //Выводим результат
	    return count > 0;
	}
	
	private boolean dbExist() {
		File dbFile = context.getDatabasePath(DB_NAME);
		return dbFile.exists();
	}

}
