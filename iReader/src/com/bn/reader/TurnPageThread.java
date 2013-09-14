package com.bn.reader;

public class TurnPageThread extends Thread 
{
	ReaderView readerView;
	private boolean pageflag=false;//�߳�ֹͣ�ı�ʶλ
	private boolean thirtySecond=false;//30���Զ���ҳ�ı�ʶλ
	private boolean fortySecond=false;//40���Զ���ҳ�ı�ʶλ
	private boolean fiftySecond=false;//50���Զ���ҳ�ı�ʶ
	private int sleepSpan1=30000;//30����ػ�
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
					Thread.sleep(sleepSpan1);//˯��ָ��������
				}else
					if(fortySecond)
					{
						Thread.sleep(sleepSpan2);//˯��ָ��������
					}else
						if(fiftySecond)
						{
							Thread.sleep(sleepSpan3);//˯��ָ��������
						}
            }
            catch(Exception e){
            	e.printStackTrace();//��ӡ��ջ��Ϣ
            }
            
			readerView.currBook.put(readerView.currRR.pageNo, readerView.currRR);
		   
			//��ʼ������һҳ����
			readerView.currRR=new ReadRecord(Constant.nextPageStart,Constant.nextPageNo);
			Constant.CURRENT_LEFT_START=readerView.currRR.start;//��¼��ǰ������leftstart��ֵ
			Constant.CURRENT_PAGE=readerView.currRR.pageNo;//��¼��ǰ��������page��ֵ
		
			if(readerView.currRR.start>Constant.CONTENTCOUNT){
				pageflag=false;//����������һҳ����ֹͣ��ҳ
			}else
			{
				//������������ͼƬ
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
