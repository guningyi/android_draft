package com.bn.reader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLDBUtil 
{
	static SQLiteDatabase sld;
	public static void createOrOpenDatabase()//����������ݿ�
	{
		try
    	{
	    	sld=SQLiteDatabase.openDatabase
	    	(
	    			"/data/data/com.bn.reader/recordself", //���ݿ�����·��
	    			null, 							//�α깤��
	    			SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.CREATE_IF_NECESSARY //��д�����������򴴽�
	    	);	
	    	
	    	String sql1="create table if not exists BookRecord"+
					"("+
					"rid INTEGER PRIMARY KEY AUTOINCREMENT,"+
					"path varchar(50),"+
					"data blob"+
					");";
	    	String sql2="create table if not exists BookMark"+
					"("+
					"mid INTEGER PRIMARY KEY AUTOINCREMENT,"+
					"ridfk INTEGER,"+
					"bmname varchar(50),"+
					"page INTEGER"+
					");";
	    	String sql3="create table if not exists LastTimePage"+
	    			"("+
	    			"lid INTEGER PRIMARY KEY AUTOINCREMENT,"+
	    			"path varchar(50),"+
	    			"page INTEGER,"+
	    			"fontsize INTEGER"+
	    			");";
	    	sld.execSQL(sql1);//������
	    	sld.execSQL(sql2);
	    	sld.execSQL(sql3);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
	}
	
	//�ر����ݿ�ķ���
    public static void closeDatabase()
    {
    	try
    	{
	    	sld.close();     		
    	}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    } 

	//����ָ�������е���ǩ
	public static List<BookMark> getBookmarkList(String path)
	{
		List<BookMark> al=new ArrayList<BookMark>();
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//�����ݿ�			
			String sql="select page,bmname from BookRecord,BookMark"
						+" where path='"+path+"' and rid=ridfk order by page";
			cur=sld.rawQuery(sql, new String[]{});
			while(cur.moveToNext())
		    {		
				int page=cur.getInt(0);
				String bmname=cur.getString(1);
		    	al.add(new BookMark(bmname,page));//��ҳ������ǩ���ִ�����ǩ����
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			cur.close();
			closeDatabase(); //�ر����ݿ�
		}
		return al;
	}
	//����ָ��������һ����ǩ��ҳ��
	public static int getLastBookmarkPage(String path)
	{
		int page=0;
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//�����ݿ�			
			String sql="select page from BookRecord,BookMark"
						+" where path='"+path+"' and rid=ridfk order by page";
			cur=sld.rawQuery(sql, new String[]{});
			
			cur.moveToLast();
			page=cur.getInt(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			cur.close();
			closeDatabase(); //�ر����ݿ�
		}
		return page;
	}
	
	
	
	
	
	//����ÿһ���������������ҳ��
	public static int getLastTimePage(String path)
	{
		int page=0;
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//�����ݿ�			
			String sql="select page from LastTimePage"
						+" where path='"+path+"'";
			cur=sld.rawQuery(sql, new String[]{});
			cur.moveToNext();	
			page=cur.getInt(0);

			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			cur.close();
			closeDatabase(); //�ر����ݿ�
		}
		return page;
	}
	
	//����ÿһ��������������������С
	public static int getLastTimeFontSize(String path)
	{
		int fontsize=0;
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//�����ݿ�			
			String sql="select fontsize from LastTimePage"
						+" where path='"+path+"'";
			cur=sld.rawQuery(sql, new String[]{});
			cur.moveToNext();	
			fontsize=cur.getInt(0);

			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			cur.close();
			closeDatabase(); //�ر����ݿ�
		}
		return fontsize;
	}
	
	
	
	
	//��lastTime���в�������
	public static void lastTimeInsert(String path,int page,int fontsize)
	{
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//�����ݿ�
			//�ȴ����ݿ��в�ѯָ���������Ƿ����
			String sql="select path from LastTimePage where path='"+path+"'";//�����Ƿ����ָ��·��������
			cur=sld.rawQuery(sql, new String[]{});
			if(cur.moveToNext())
			{//���Ѿ����������
				sql="update LastTimePage set page=?,fontsize=? where path='"+path+"'";
				sld.execSQL(sql,new Object[]{page,fontsize});
			}
			else
			{//�������������
	            sql="insert into LastTimePage(path,page,fontsize)values(?,?,?)";    		
	    		sld.execSQL(sql,new Object[]{path,page,fontsize});   
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			cur.close();
        	closeDatabase(); //�ر����ݿ�
		}
	}
	
	//��BookRecord���в�������
	public static void recordInsert(String path,byte[] rdata)
	{
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//�����ݿ�
			//�ȴ����ݿ��в�ѯָ���������Ƿ����
			String sql="select path from BookRecord where path='"+path+"'";//�����Ƿ����ָ��·��������
			cur=sld.rawQuery(sql, new String[]{});
			if(cur.moveToNext())
			{//���Ѿ����������
				sql="update BookRecord set data=? where path='"+path+"'";
				sld.execSQL(sql,new Object[]{rdata});
			}
			else
			{//�������������
				//�������ƻ�ID,����ƻ�����,����ƻ�����,����ƻ�����������ƻ�blod
	            sql="insert into BookRecord(path,data)values(?,?)";    		
	    		sld.execSQL(sql,new Object[]{path,rdata});   
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			cur.close();
        	closeDatabase(); //�ر����ݿ�
		}
	}
	
	//��ȡָ�����Ƶļ�¼���ݣ��õ�����HashMap��byte���ݣ�
	public static byte[] selectRecordData(String path)
	{
		byte[] data=null;
		Cursor cur=null;

		try
		{
			createOrOpenDatabase();//�����ݿ�			
			String sql="select path,data from BookRecord where path='"+path+"'";
			cur=sld.rawQuery(sql, new String[]{});
			if(cur.moveToNext())
		    {
				data=cur.getBlob(1);	
		    }		
		}
		catch(Exception e)
		{
			e.printStackTrace();  
		}
		finally
		{
			cur.close();
        	closeDatabase(); //�ر����ݿ�
		}
		
		return data;
	}
	
	//�ж��ǵ�һ�δ��Ȿ�黹�ǵ�N�δ򿪱���
	public static boolean judgeIsWhichTime(String path)
	{
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//�����ݿ�			
			String sql="select path from BookRecord where path='"+path+"'";
			cur=sld.rawQuery(sql, new String[]{});
			if(cur.moveToNext())//�������·�������ǵ�N�δ��Ȿ��
		    {
				return false;
		    }
			else
			{
				return true;//�����ǵ�1�δ��Ȿ��	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();  
		}
		finally
		{
			cur.close();
        	closeDatabase(); //�ر����ݿ�
		}
		return false;	
	}
	
	//ɾ��BookMark���ж�Ӧ���������ļ�¼
	public static void deleteOneBookMark(String name)
	{
		try
		{
			createOrOpenDatabase();//�����ݿ�			
			String sql="delete from BookMark where bmname='"+name+"'";
			sld.execSQL(sql);
		}
		catch(Exception e)
		{
			e.printStackTrace();  
		}
		finally
		{
        	closeDatabase(); //�ر����ݿ�
		}
	}
	//ɾ����ǰ�Ȿ���ȫ����ǩ
	//����ָ�������е���ǩ
	public static void deleteAllBookMark(String path)
	{	
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//�����ݿ�			
			String sql="select rid from BookRecord,BookMark"
						+" where path='"+path+"' and rid=ridfk";
			cur=sld.rawQuery(sql, new String[]{});
		    cur.moveToNext();
			int rid=cur.getInt(0);//�õ���ǩ��ridfk��ֵ
			
			String sql2="delete from BookMark where ridfk='"+rid+"'";
			sld.execSQL(sql2);//��յ�ǰ�Ȿ���ȫ����¼
		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			cur.close();
        	closeDatabase(); //�ر����ݿ�
		}
	}
	//�жϵ�ǰ�Ȿ���Ƿ������ǩ
	public static boolean judgeHaveBookMark(String path)
	{	
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//�����ݿ�			
			String sql="select page from BookRecord,BookMark"
						+" where path='"+path+"' and rid=ridfk";
			cur=sld.rawQuery(sql, new String[]{});
			if(cur.moveToNext())//������д�����ǩ������true
		    {
				return true;
		    }
			else
			{
				return false;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			cur.close();
			closeDatabase(); //�ر����ݿ�
		}
		return false;		
	}
	//��BookMark���в�������
	public static void bookMarkInsert(String name,int page)
	{
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//�����ݿ�
			//��ѯ��ǰpath��Ӧ��rid
			String sql="select rid from BookRecord where path='"+Constant.FILE_PATH+"'";
			cur=sld.rawQuery(sql, new String[]{});
			cur.moveToNext();
			int rid=cur.getInt(0);
			
			//�ȴ����ݿ��в�ѯָ���������Ƿ����
			sql="select bmname from BookMark where bmname='"+name+"'";
			cur=sld.rawQuery(sql, new String[]{});
			if(cur.moveToNext())
			{//���Ѿ����������
				sql="update BookMark set page=?,ridfk=? where bmname='"+name+"'";
				sld.execSQL(sql,new Object[]{page,rid});
			}
			else
			{//�������������
	            sql="insert into BookMark(bmname,page,ridfk)values(?,?,?)";    		
	    		sld.execSQL(sql,new Object[]{name,page,rid});  		
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			cur.close();
        	closeDatabase(); //�ر����ݿ�
		}
	}
	//��hashMapת��Ϊbyte
	public static byte[] fromListRowNodeListToBytes(HashMap<Integer,ReadRecord> map)
	{
		byte[] result=null;		
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		try 
		{
			ObjectOutputStream oout=new ObjectOutputStream(baos);
			oout.writeObject(map);
			result=baos.toByteArray();
			oout.close();
			baos.close();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		return result;
	}
	
	//��byteת��ΪhashMap
	@SuppressWarnings("unchecked")
	public static HashMap<Integer,ReadRecord> fromBytesToListRowNodeList(byte[] data)
	{
		HashMap<Integer,ReadRecord> result=null;
		try
		{			
			ByteArrayInputStream bais=new ByteArrayInputStream(data);			
			ObjectInputStream oin=new ObjectInputStream(bais);			
			result=(HashMap<Integer,ReadRecord>)oin.readObject();
			oin.close();
			bais.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
}
