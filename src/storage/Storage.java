package storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import org.json.simple.JSONObject;

import common.*;

public class Storage implements StorageInterface {
	
	private BufferedWriter sWriter;

	public void init() {
		System.out.println("Storage Init");
		System.out.println("Storage Init complete");
		try {
			connectToFile();
		} catch (IOException e) {
			// do something
		}
		
	}

	public Storage() {
		
	}
	
	private void connectToFile() throws IOException {
		Common setting = Common.getInstance();
		String fileDir = setting.getUsrFileDirectory();
		File file = new File(fileDir + "/task.json");
		if(!file.exists()) {
			file.createNewFile();
		}
		sWriter = new BufferedWriter(new FileWriter(file));
		// do something
	}


	@Override
	public boolean save(Celebi c) {
		// TODO Auto-generated method stub
		c.setId(1);
		boolean saveSuccessful = true;
		return saveSuccessful;
	}

	@Override
	public boolean load(String s, CelebiBag c) {
		c.addCelebi(new Celebi("storage dummy test1", createDate(2015, 10, 10, 0, 0), createDate(2015, 11, 11, 0, 0)));
		c.addCelebi(new Celebi("storage dummy test2", createDate(2016, 10, 10, 0, 0), createDate(2016, 11, 11, 0, 0)));
		c.addCelebi(new Celebi("storage dummy test3", createDate(2017, 10, 10, 0, 0), createDate(2017, 11, 11, 0, 0)));
		boolean loadSuccessful = true;
		return loadSuccessful;
	}
	
	@Override
	public boolean delete(Celebi c) {
		return true;
	}
	
	// private methods
	private Date createDate(int year, int month, int day, int hour, int minute) {
		Calendar calender = Calendar.getInstance();
		calender.set(year, month, day, hour, minute);
		return new Date(calender.getTimeInMillis());
	}
}
