package com.example.myproject;

public class QuestionGroup {
    
	public static final int quantity_group = 7;
	
	String[] q_str = new String[quantity_group];
    int table_id, q_key;
    QuestionGroup(int table_id, String[] q_str, int q_key)
    {
    	this.table_id = table_id;
    	this.q_str = q_str;
    	this.q_key = q_key;
    }
    QuestionGroup()
    {
    	this.table_id = 0;
    	for (int i = 0; i < this.quantity_group; ++i)
    		this.q_str[i] = "";
    	this.q_key = 0;
    }
}
