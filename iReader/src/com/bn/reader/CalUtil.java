package com.bn.reader;


public class CalUtil 
{
	//求折页时边上两个点的坐标的方法
	public static int[] calCD(float ax,float ay,float bx,float by)
	{
		//直线cd的斜率
		float kq=(bx-ax)/(ay-by);
		//p点的坐标
		float px=(ax+bx)/2;
		float py=(ay+by)/2;		
		//直线cd的b值
		float bq=py-kq*px;
		//c点的坐标
		float cx=(by-bq)/kq;
		float cy=by;
		//d点的坐标
		float dx=bx;
		float dy=kq*bx+bq;		
		return new int[]{(int)cx,(int)cy,(int)dx,(int)dy};
	}
}
