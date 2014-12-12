import java.sql.Timestamp;
import java.util.Date;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;


public class TrendTopic {
	private String title;
	private Timestamp startTime , endTime;
	
	public TrendTopic(String title){
		super();
		this.title = title;
		startTime = new Timestamp(new Date().getTime());
		endTime = null;
	}
	
	public TrendTopic(BasicDBObject o){
		title = (String) o.get("Title");
		System.out.println(title);
		startTime =  new Timestamp(((Date) o.get("StartTime")).getTime());
		System.out.println(startTime);
		endTime = new Timestamp(((Date) o.get("EndTime")).getTime());
		System.out.println(endTime);
	}
	
	public void setEndTime(){
		endTime = new Timestamp(new Date().getTime());
	}
	public void setEndTime(Object o){
		endTime = null;
	}
	
	public String getTitle(){
		return title;
	}
	
	public boolean expired(){
		if(endTime == null){
			return false;
		}
		long expiration = endTime.getTime() + 1000*60*60*2;
		Timestamp exp = new Timestamp(expiration);
		Timestamp now = new Timestamp(new Date().getTime());
		if(now.after(exp)){
			//expired
			return true;
		}
		return false;
	}
	
	public Timestamp getEndTime(){
		return endTime;
	}
	
	public boolean isSameTopic(String title){
		return this.title.equals(title);
	}

	
	public void saveToCollection(DBCollection col){
		BasicDBObject obj = new BasicDBObject();
	 	obj.put("Title",title);
	 	obj.put("StartTime",startTime);
	 	obj.put("EndTime",endTime);
	 	col.insert(obj);
	}

}