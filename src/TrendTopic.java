import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.bson.BSONObject;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;


public class TrendTopic implements DBObject{
	private String title;
	private Timestamp startTime , endTime;
	
	public TrendTopic(String title){
		this.title = title;
		startTime = new Timestamp(new Date().getTime());
		endTime = null;
		
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

	public void saveToCollection(DBCollection coll){
		put("Title",title);
		put("StartTime",startTime);
		put("EndTime",endTime);
		coll.insert(this);
	}

	@Override
	public boolean containsField(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	@Deprecated
	public boolean containsKey(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object get(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object put(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(BSONObject arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void putAll(Map arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object removeField(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map toMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPartialObject() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void markAsPartialObject() {
		// TODO Auto-generated method stub
		
	}
}