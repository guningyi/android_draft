package com.example.ebook_v1;



public class Constant
{
	//directions����
	public static String DIRECTIONSNAME="directions.txt"; //APK���Դ�˵��������
	//�����õ�URL
	public static final String ADD_PRE="http://book01.ap01.aws.af.cm/txt/";//����ı��ļ���URL
	//public static final String ADD_PRE="http://10.0.2.2:8080/txt/";
	//��ҳ�õ��ĳ���ֵ
	public static int CURRENT_LEFT_START;//��¼��ǰ������LeftStart��ֵ
	public static int CURRENT_PAGE;//��¼��ǰ��������page��ֵ
	public static int nextPageStart;//��һҳ����ʼ�ַ����ַ����е�λ��
	public static int nextPageNo;//��һҳ��ҳ��
	public static int CONTENTCOUNT;//�ı������ַ���
	public static int CURRENT_START;//��ǰ�Ŀ�ʼλ��
	//������õ��ĳ���ֵ
	public static int AD_WIDTH=120;//�����ʵ�ʳ���
	public static int NUM;//���ͼ������
	//��ʼ������������ɫ
	public static int COLOR=0xffffff00;//��ʼ��������ɫ
	//��Ļ�Ĵ�С
	public static int SCREEN_WIDTH;//��Ļ�Ŀ��
	public static int SCREEN_HEIGHT;//��Ļ�ĸ߶�
	//��������ͼƬ��λ��
	public static int LEFT_OR_RIGHT_X=0;//���ͼƬ�󶥵��x����
	public static int RIGHT_OR_LEFT_x;//�Ҳ�ͼƬ�󶥵��x����
	//���ֵ�����
	public static int PAGE_LENGTH;//ÿ�ζ�ȡ���ָ���
	public static int TEXT_SPACE_BETWEEN_EN=8;//�ı����-Ӣ��
	public static int TEXT_SPACE_BETWEEN_CN=16;//�ı����-����
	public static int TEXT_SIZE=16;//�ı���С
	public static int TITLE_SIZE=25;//��ͷ�ı��������С
	//����ҳ������
	public static int ROWS;//����ҳ������
	public static int PAGE_WIDTH;//����ҳ�Ŀ��
	public static int PAGE_HEIGHT;//����ҳ�ĸ߶�
	//����ı���·��
	public static String FILE_PATH; //����ı���·��
	public static String TEXTNAME;//��ǰ�Ķ����ļ�������
	//�Ϸ�����
	public static int BLANK=30;//�Ϸ�����
	//�м�ָ���
	public static int CENTER_WIDTH=4;//�м�ָ������
	public static int CENTER_LEFT_Y=BLANK;//�м�ָ������Ͻ�y����
	public static int CENTER_LEFT_X;//�м�ָ������Ͻ�x����	
	public static int CENTER_RIGHT_X;//�м�ָ��� ���½�x����
	public static int CENTER_RIGHT_Y;//�м�ָ��� ���½�y����
	//�������ֵĲ���
	public static int I;//�������ֵ�Rֵ
	
	//������Ļ�ֱ��������ĳ���ֵ
	public static void changeRatio()
	{
		//�ָ�������Ӧ
		CENTER_LEFT_X=SCREEN_WIDTH/2;//���м�ָ������Ͻ�x����
		CENTER_RIGHT_X=CENTER_LEFT_X+CENTER_WIDTH-1;//���м�ָ��� ���Ͻ�x����
		CENTER_RIGHT_Y=SCREEN_HEIGHT;//���м�ָ��� ���½�y����
		//�Ҳ�ͼƬ�󶨵�X��������Ӧ
		//RIGHT_OR_LEFT_x=CENTER_RIGHT_X+1;//���Ҳ�ͼƬ�󶨵��x����
		RIGHT_OR_LEFT_x = CENTER_LEFT_X + 1;
		//����ҳ�߿�����Ӧ
		PAGE_WIDTH = SCREEN_WIDTH;//����ҳ�Ŀ��
		PAGE_HEIGHT=SCREEN_HEIGHT-BLANK;//����ҳ�ĸ߶�
		//����ҳ�ı���������Ӧ
		ROWS=PAGE_HEIGHT/TEXT_SIZE;//ÿ������ҳ���ı�����
		//ÿ�ζ�ȡ�ı������ֵĸ���
		PAGE_LENGTH=(PAGE_WIDTH/TEXT_SPACE_BETWEEN_EN+1)*(PAGE_HEIGHT/TEXT_SIZE+1);//ÿ�ζ�ȡ���ָ�����ֵ
	}
}
