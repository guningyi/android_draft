package com.bn.reader;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class PicLoadUtil 
{

   //从资源中加载一幅图片
   public static Bitmap LoadBitmap(Resources res,int picId)
   {
	   Bitmap result=BitmapFactory.decodeResource(res, picId);
	   return result;
   }
   
   //缩放旋转图片的方法
   public static Bitmap scaleToFit(Bitmap bm,float targetWidth,float targetHeight)//缩放图片的方法
   {
   	float width = bm.getWidth(); //图片宽度
   	float height = bm.getHeight();//图片高度	
   	
   	Matrix m1 = new Matrix(); 
   	m1.postScale(targetWidth/width, targetHeight/height);//按照	targetWidth/targetHeight 缩放图片
   	Bitmap bmResult = Bitmap.createBitmap(bm, 0, 0, (int)width, (int)height, m1, true);//声明位图        	
   	return bmResult;
   }
}
