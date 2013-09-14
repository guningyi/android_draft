package com.example.ebook_v1;

import java.io.Serializable;



public class ReadRecord implements Serializable
{
	private static final long serialVersionUID = 1629550672724754997L;
	int pageNo;//从0开始数
	int start; //开始位置  
   
	public ReadRecord(int start,int pageNo)
	{
		this.start =start;
		this.pageNo=pageNo;
	}
}