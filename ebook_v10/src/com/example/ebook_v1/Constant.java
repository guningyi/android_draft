package com.example.ebook_v1;



public class Constant
{
	//directions设置
	public static String DIRECTIONSNAME="directions.txt"; //APK中自带说明的名字
	//下载用到URL
	public static final String ADD_PRE="http://book01.ap01.aws.af.cm/txt/";//存放文本文件的URL
	//public static final String ADD_PRE="http://10.0.2.2:8080/txt/";
	//翻页用到的常量值
	public static int CURRENT_LEFT_START;//记录当前读到处LeftStart的值
	public static int CURRENT_PAGE;//记录当前读到处的page的值
	public static int nextPageStart;//下一页的起始字符在字符串中的位置
	public static int nextPageNo;//下一页的页码
	public static int CONTENTCOUNT;//文本的总字符数
	public static int CURRENT_START;//当前的开始位置
	//广告条用到的常量值
	public static int AD_WIDTH=120;//广告条实际长度
	public static int NUM;//广告图的数量
	//初始背景和字体颜色
	public static int COLOR=0xffffff00;//初始的字体颜色
	//屏幕的大小
	public static int SCREEN_WIDTH;//屏幕的宽度
	public static int SCREEN_HEIGHT;//屏幕的高度
	//左右两侧图片的位置
	public static int LEFT_OR_RIGHT_X=0;//左侧图片左顶点的x坐标
	public static int RIGHT_OR_LEFT_x;//右侧图片左顶点的x坐标
	//文字的设置
	public static int PAGE_LENGTH;//每次读取文字个数
	public static int TEXT_SPACE_BETWEEN_EN=8;//文本间距-英文
	public static int TEXT_SPACE_BETWEEN_CN=16;//文本间距-中文
	public static int TEXT_SIZE=16;//文本大小
	public static int TITLE_SIZE=25;//标头文本的字体大小
	//虚拟页的设置
	public static int ROWS;//虚拟页的行数
	public static int PAGE_WIDTH;//虚拟页的宽度
	public static int PAGE_HEIGHT;//虚拟页的高度
	//存放文本的路径
	public static String FILE_PATH; //存放文本的路径
	public static String TEXTNAME;//当前阅读的文件的名字
	//上方留白
	public static int BLANK=30;//上方留白
	//中间分割线
	public static int CENTER_WIDTH=4;//中间分割条宽度
	public static int CENTER_LEFT_Y=BLANK;//中间分割线左上角y坐标
	public static int CENTER_LEFT_X;//中间分割线左上角x坐标	
	public static int CENTER_RIGHT_X;//中间分割线 右下角x坐标
	public static int CENTER_RIGHT_Y;//中间分割线 右下角y坐标
	//背景音乐的播放
	public static int I;//背景音乐的R值
	
	//根据屏幕分辨率来更改常量值
	public static void changeRatio()
	{
		//分割线自适应
		CENTER_LEFT_X=SCREEN_WIDTH/2;//给中间分割线左上角x坐标
		CENTER_RIGHT_X=CENTER_LEFT_X+CENTER_WIDTH-1;//给中间分割线 右上角x坐标
		CENTER_RIGHT_Y=SCREEN_HEIGHT;//给中间分割线 右下角y坐标
		//右侧图片左定点X坐标自适应
		//RIGHT_OR_LEFT_x=CENTER_RIGHT_X+1;//给右侧图片左定点的x坐标
		RIGHT_OR_LEFT_x = CENTER_LEFT_X + 1;
		//虚拟页高宽自适应
		PAGE_WIDTH = SCREEN_WIDTH;//虚拟页的宽度
		PAGE_HEIGHT=SCREEN_HEIGHT-BLANK;//虚拟页的高度
		//虚拟页文本行数自适应
		ROWS=PAGE_HEIGHT/TEXT_SIZE;//每个虚拟页上文本行数
		//每次读取文本中文字的个数
		PAGE_LENGTH=(PAGE_WIDTH/TEXT_SPACE_BETWEEN_EN+1)*(PAGE_HEIGHT/TEXT_SIZE+1);//每次读取文字个数赋值
	}
}
