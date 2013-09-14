package com.bn.reader;

public class TurnPageThread extends Thread 
{
	ReaderView readerView;
	private boolean pageflag=false;//线程停止的标识位
	private boolean thirtySecond=false;//30秒自动翻页的标识位
	private boolean fortySecond=false;//40秒自动翻页的标识位
	private boolean fiftySecond=false;//50秒自动翻页的标识
	private int sleepSpan1=30000;//30秒后重画
	private int sleepSpan2=40000;
	private int sleepSpan3=50000;
	
	TurnPageThread(ReaderView readerView)
	{
		this.readerView=readerView;
	}
	@Override
	public void run()
	{
		while(pageflag)
		{
			try{
				if(thirtySecond)
				{
					Thread.sleep(sleepSpan1);//睡眠指定毫秒数
				}else
					if(fortySecond)
					{
						Thread.sleep(sleepSpan2);//睡眠指定毫秒数
					}else
						if(fiftySecond)
						{
							Thread.sleep(sleepSpan3);//睡眠指定毫秒数
						}
            }
            catch(Exception e){
            	e.printStackTrace();//打印堆栈信息
            }
            
			readerView.currBook.put(readerView.currRR.pageNo, readerView.currRR);
		   
			//初始化到下一页数据
			readerView.currRR=new ReadRecord(Constant.nextPageStart,Constant.nextPageNo);
			Constant.CURRENT_LEFT_START=readerView.currRR.start;//记录当前读到处leftstart的值
			Constant.CURRENT_PAGE=readerView.currRR.pageNo;//记录当前读到处的page的值
		
			if(readerView.currRR.start>Constant.CONTENTCOUNT){
				pageflag=false;//如果翻到最后一页，则停止翻页
			}else
			{
				//绘制左右两幅图片
				readerView.bmLeft=readerView.drawPage(readerView.currRR);
				//readerView.bmRight=readerView.drawPage(readerView.currRR);
				readerView.repaint();
			}
		}
	}
	
	public void setPageflag(boolean pageflag) {
		this.pageflag = pageflag;
	}
	public boolean isPageflag() {
		return pageflag;
	}
	
	public void setFortySecond(boolean fortySecond) {
		this.fortySecond= fortySecond;
	}
	public boolean isFortySecond() {
		return fortySecond;
	}
	public void setThirtySecond(boolean thirtySecond) {
		this.thirtySecond = thirtySecond;
	}
	public boolean isThirtySecond() {
		return thirtySecond;
	}
	public void setFiftySecond(boolean fiftySecond) {
		this.fiftySecond = fiftySecond;
	}
	public boolean isFiftySecond() {
		return fiftySecond;
	}
}
