package com.example.ebook_v1;

import java.io.Serializable;



public class ReadRecord implements Serializable
{
	private static final long serialVersionUID = 1629550672724754997L;
	int pageNo;//��0��ʼ��
	int start; //��ʼλ��  
   
	public ReadRecord(int start,int pageNo)
	{
		this.start =start;
		this.pageNo=pageNo;
	}
}