package storage;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import common.*;

public class Storage implements StorageInterface {
	public Storage() {
		
	}
	
	public void init() {
		System.out.println("Storage Init");
		System.out.println("Storage Init complete");
		try {
			connectToDatabase();
		} catch (IOException e) {
			// do something
		}
		
	}
	
	public void close() {
		System.out.println("Storage closed");
		Database.disconnect();
	}
	
	private void connectToDatabase() throws IOException {
		Common setting = Common.getInstance();
		String fileDir = setting.getUsrFileDirectory();
		Database.connect("tasks.json");
		Database.load();
	}

	@Override
	public boolean save(Task c) {
		CelebiJson cj = new CelebiJson(c);
		int id = c.getId();
		if (id <= 0) {
			// new
			id = Database.insert(cj);
			c.setId(id);
		} else {
			Database.update(id, cj);
		}
		
		boolean saveSuccessful = true;
		return saveSuccessful;
	}

	@Override
	public boolean load(String s, TasksBag c) {
		List<CelebiJson> data = Database.getData();
		for (int i = 0; i < data.size(); i ++) {
			c.addTask(data.get(i).toCelebi());
		}
		
		boolean loadSuccessful = true;
		return loadSuccessful;
	}
	
	@Override
	public boolean delete(Task c) {
		Database.delete(c.getId());
		return true;
	}
	
	
	// private methods
	private static Date createDate(int year, int month, int day, int hour, int minute) {
		Calendar calender = Calendar.getInstance();
		calender.set(year, month, day, hour, minute);
		return new Date(calender.getTimeInMillis());
	}

	
	public static void main (String[] args) {
		Storage s = new Storage();
		s.init();
		Task c1 = new Task("storage dummy test1", createDate(2015, 10, 10, 0, 0), createDate(2015, 11, 11, 0, 0));
		Task c2 = new Task("storage dummy test2", createDate(2016, 10, 10, 0, 0), createDate(2016, 11, 11, 0, 0));
		Task c3 = new Task("storage dummy test3", createDate(2017, 10, 10, 0, 0), createDate(2017, 11, 11, 0, 0));
		
		s.save(c1);
		s.save(c2);
		s.save(c3);
		
		c1.setName("d");
		s.save(c1);
		s.delete(c2);
		
		TasksBag cb = new TasksBag();
		
		s.load("", cb);
		System.out.println(cb.size());
		
		s.close();
	}
}
