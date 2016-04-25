package com.example.myproject;

import java.util.ArrayList;

import android.widget.RadioButton;
import android.widget.TextView;

/*
 * Объект "вопрос"
 */

public class Question {

	public static final int N = 5;
	
	public String name; //сам вопрос в текстовом виде
	public String ans[] = new String [N]; //массив с ответами на этот вопрос
	public int correctAnswer; //номер правильного ответа
	
	public Question() {
		
		return;
	}
	
	public Question(String strQuestion) {
		//создаём список, в который будем заносить ответы на вопрос:
		ArrayList <String> qList = new ArrayList();
		//Обрабатываем вопросную строку:
		String str = strQuestion;
		int p = str.indexOf("_");
		//Заносим название вопроса в сам объект:
		this.name = str.substring(0, p);
		//Заносим по очереди ответы на вопрос в список: 
		for (int i = 0; i < this.N; ++i) {
			qList.add(str.substring(p + 1, str.indexOf("_", p + 1)));
			p = str.indexOf("_", p + 1);
		}
		//Запоминаем номер правильного ответа в списке:
		Integer cA = new Integer(str.substring(p + 1));
		int corAns = (int) (cA);
		
		//Теперь будем случайным образом вытаскивать из списка ответы и заносить их в массив:
		//Индекс случайного элемента списка:
		int rand;
		//Индекс массива объекта:
		int i = 0;
		//Вытаскиваем из списка элементы, пока он не кончится:
		while (qList.size() > 0) {
			//Генерируем случайный индекс:
			rand = (int) (qList.size()*Math.random());
			//Записываем этот случайный элемент в массив объекта:
			this.ans[i] = qList.get(rand);
			//Удаляем этот элемент из списка:
			qList.remove(rand);
			//Если индекс случайного элемента меньше индекса правильного ответа, то:
			if (rand < corAns) {
				corAns -= 1; // уменьшаем индекс правильного ответа на единицу
			} else if (rand == corAns) { // а если индекс случайного элемента равен индексу правильного ответа, то:
				this.correctAnswer = i; //говорим, что индекс правильного ответа в массиве - это i
/**!!!-->*/		corAns = -1; // говорим, что правильного ответа в списке больше нет
			}
			++i; //идём дальше
		}
		return;
	}
}
