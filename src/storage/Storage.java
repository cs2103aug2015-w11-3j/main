package storage;

import java.util.Calendar;
import java.util.Date;

import common.Celebi;
import common.CelebiBag;

public class Storage implements StorageInterface {

	public void init() {
		System.out.println("Storage Init");
		System.out.println("Storage Init complete");
	}

	public Storage() {

	}


	@Override
	public Celebi save(Celebi c) {
		// TODO Auto-generated method stub
		c.setId(1);
		return c;
	}

	@Override
	public boolean load(String s, CelebiBag c) {
		c.addCelebi(new Celebi("storage dummy test1", createDate(2015, 10, 10, 0, 0), createDate(2015, 11, 11, 0, 0)));
		c.addCelebi(new Celebi("storage dummy test2", createDate(2016, 10, 10, 0, 0), createDate(2016, 11, 11, 0, 0)));
		c.addCelebi(new Celebi("storage dummy test3", createDate(2017, 10, 10, 0, 0), createDate(2017, 11, 11, 0, 0)));
		boolean loadSuccessful = true;
		return loadSuccessful;
	}
	
	// private methods
	private Date createDate(int year, int month, int day, int hour, int minute) {
		Calendar calender = Calendar.getInstance();
		calender.set(year, month, day, hour, minute);
		return new Date(calender.getTimeInMillis());
	}
}
