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
	public static void createOrOpenDatabase()//创建或打开数据库
	{
		try
    	{
	    	sld=SQLiteDatabase.openDatabase
	    	(
	    			"/data/data/com.bn.reader/recordself", //数据库所在路径
	    			null, 							//游标工厂
	    			SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.CREATE_IF_NECESSARY //读写、若不存在则创建
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
	    	sld.execSQL(sql1);//创建表
	    	sld.execSQL(sql2);
	    	sld.execSQL(sql3);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
	}
	
	//关闭数据库的方法
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

	//返回指定书所有的书签
	public static List<BookMark> getBookmarkList(String path)
	{
		List<BookMark> al=new ArrayList<BookMark>();
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//打开数据库			
			String sql="select page,bmname from BookRecord,BookMark"
						+" where path='"+path+"' and rid=ridfk order by page";
			cur=sld.rawQuery(sql, new String[]{});
			while(cur.moveToNext())
		    {		
				int page=cur.getInt(0);
				String bmname=cur.getString(1);
		    	al.add(new BookMark(bmname,page));//将页数和书签名字存入书签类中
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			cur.close();
			closeDatabase(); //关闭数据库
		}
		return al;
	}
	//返回指定书的最后一个书签的页数
	public static int getLastBookmarkPage(String path)
	{
		int page=0;
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//打开数据库			
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
			closeDatabase(); //关闭数据库
		}
		return page;
	}
	
	
	
	
	
	//返回每一本书所读到的书的页数
	public static int getLastTimePage(String path)
	{
		int page=0;
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//打开数据库			
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
			closeDatabase(); //关闭数据库
		}
		return page;
	}
	
	//返回每一本书所读到的书的字体大小
	public static int getLastTimeFontSize(String path)
	{
		int fontsize=0;
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//打开数据库			
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
			closeDatabase(); //关闭数据库
		}
		return fontsize;
	}
	
	
	
	
	//向lastTime表中插入数据
	public static void lastTimeInsert(String path,int page,int fontsize)
	{
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//打开数据库
			//先从数据库中查询指定的名称是否存在
			String sql="select path from LastTimePage where path='"+path+"'";//查找是否存在指定路径的数据
			cur=sld.rawQuery(sql, new String[]{});
			if(cur.moveToNext())
			{//若已经存在则更新
				sql="update LastTimePage set page=?,fontsize=? where path='"+path+"'";
				sld.execSQL(sql,new Object[]{page,fontsize});
			}
			else
			{//若不存在则插入
	            sql="insert into LastTimePage(path,page,fontsize)values(?,?,?)";    		
	    		sld.execSQL(sql,new Object[]{path,page,fontsize});   
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			cur.close();
        	closeDatabase(); //关闭数据库
		}
	}
	
	//向BookRecord表中插入数据
	public static void recordInsert(String path,byte[] rdata)
	{
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//打开数据库
			//先从数据库中查询指定的名称是否存在
			String sql="select path from BookRecord where path='"+path+"'";//查找是否存在指定路径的数据
			cur=sld.rawQuery(sql, new String[]{});
			if(cur.moveToNext())
			{//若已经存在则更新
				sql="update BookRecord set data=? where path='"+path+"'";
				sld.execSQL(sql,new Object[]{rdata});
			}
			else
			{//若不存在则插入
				//插入计算计划ID,计算计划名称,计算计划总量,计算计划人数，计算计划blod
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
        	closeDatabase(); //关闭数据库
		}
	}
	
	//获取指定名称的记录数据（得到的是HashMap的byte数据）
	public static byte[] selectRecordData(String path)
	{
		byte[] data=null;
		Cursor cur=null;

		try
		{
			createOrOpenDatabase();//打开数据库			
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
        	closeDatabase(); //关闭数据库
		}
		
		return data;
	}
	
	//判断是第一次打开这本书还是第N次打开本书
	public static boolean judgeIsWhichTime(String path)
	{
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//打开数据库			
			String sql="select path from BookRecord where path='"+path+"'";
			cur=sld.rawQuery(sql, new String[]{});
			if(cur.moveToNext())//如果存在路径，则是第N次打开这本书
		    {
				return false;
		    }
			else
			{
				return true;//否则是第1次打开这本书	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();  
		}
		finally
		{
			cur.close();
        	closeDatabase(); //关闭数据库
		}
		return false;	
	}
	
	//删除BookMark表中对应“书名”的记录
	public static void deleteOneBookMark(String name)
	{
		try
		{
			createOrOpenDatabase();//打开数据库			
			String sql="delete from BookMark where bmname='"+name+"'";
			sld.execSQL(sql);
		}
		catch(Exception e)
		{
			e.printStackTrace();  
		}
		finally
		{
        	closeDatabase(); //关闭数据库
		}
	}
	//删除当前这本书的全部书签
	//返回指定书所有的书签
	public static void deleteAllBookMark(String path)
	{	
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//打开数据库			
			String sql="select rid from BookRecord,BookMark"
						+" where path='"+path+"' and rid=ridfk";
			cur=sld.rawQuery(sql, new String[]{});
		    cur.moveToNext();
			int rid=cur.getInt(0);//得到书签中ridfk的值
			
			String sql2="delete from BookMark where ridfk='"+rid+"'";
			sld.execSQL(sql2);//清空当前这本书的全部记录
		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			cur.close();
        	closeDatabase(); //关闭数据库
		}
	}
	//判断当前这本书是否存在书签
	public static boolean judgeHaveBookMark(String path)
	{	
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//打开数据库			
			String sql="select page from BookRecord,BookMark"
						+" where path='"+path+"' and rid=ridfk";
			cur=sld.rawQuery(sql, new String[]{});
			if(cur.moveToNext())//如果书中存在书签，返回true
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
			closeDatabase(); //关闭数据库
		}
		return false;		
	}
	//向BookMark表中插入数据
	public static void bookMarkInsert(String name,int page)
	{
		Cursor cur=null;
		try
		{
			createOrOpenDatabase();//打开数据库
			//查询当前path对应的rid
			String sql="select rid from BookRecord where path='"+Constant.FILE_PATH+"'";
			cur=sld.rawQuery(sql, new String[]{});
			cur.moveToNext();
			int rid=cur.getInt(0);
			
			//先从数据库中查询指定的名称是否存在
			sql="select bmname from BookMark where bmname='"+name+"'";
			cur=sld.rawQuery(sql, new String[]{});
			if(cur.moveToNext())
			{//若已经存在则更新
				sql="update BookMark set page=?,ridfk=? where bmname='"+name+"'";
				sld.execSQL(sql,new Object[]{page,rid});
			}
			else
			{//若不存在则插入
	            sql="insert into BookMark(bmname,page,ridfk)values(?,?,?)";    		
	    		sld.execSQL(sql,new Object[]{name,page,rid});  		
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			cur.close();
        	closeDatabase(); //关闭数据库
		}
	}
	//从hashMap转化为byte
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
	
	//从byte转化为hashMap
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
