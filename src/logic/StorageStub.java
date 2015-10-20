package logic;

import common.Task;
import common.TasksBag;
import storage.StorageInterface;

public class StorageStub implements StorageInterface {

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean load(String s, TasksBag c) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean save(Task c) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean delete(Task c) {
        // TODO Auto-generated method stub
        return true;
    }

	@Override
	public boolean moveFileTo(String destination) {
		// TODO Auto-generated method stub
		return true;
	}

}
