package storage;

import java.io.BufferedWriter;
import java.io.IOException;
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
	
	private void connectToDatabase() throws IOException {
		Common setting = Common.getInstance();
		String fileDir = setting.getUsrFileDirectory();
		Database.connect(fileDir + "tasks.json");
		Database.load();
	}

	@Override
	public boolean save(Celebi c) {
		CelebiJson cj = new CelebiJson(c);
		int id = Database.insert(cj);
		c.setId(id);
		boolean saveSuccessful = true;
		return saveSuccessful;
	}

	@Override
	public boolean load(String s, CelebiBag c) {
		List<CelebiJson> data = Database.getData();
		for (int i = 0; i < data.size(); i ++) {
			c.addCelebi(data.get(i).toCelebi());
		}
		
		boolean loadSuccessful = true;
		return loadSuccessful;
	}
	
	@Override
	public boolean delete(Celebi c) {
		Database.delete(c.getId());
		return true;
	}
}
