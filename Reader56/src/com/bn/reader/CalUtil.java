package com.bn.reader;


public class CalUtil 
{
	//����ҳʱ���������������ķ���
	public static int[] calCD(float ax,float ay,float bx,float by)
	{
		//ֱ��cd��б��
		float kq=(bx-ax)/(ay-by);
		//p�������
		float px=(ax+bx)/2;
		float py=(ay+by)/2;		
		//ֱ��cd��bֵ
		float bq=py-kq*px;
		//c�������
		float cx=(by-bq)/kq;
		float cy=by;
		//d�������
		float dx=bx;
		float dy=kq*bx+bq;		
		return new int[]{(int)cx,(int)cy,(int)dx,(int)dy};
	}
}
