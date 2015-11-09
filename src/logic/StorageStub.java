//@@author A0125546E
package logic;

import common.Task;
import common.TasksBag;
import storage.StorageInterface;

/**
 * Simulate a storage and returns true for every methods
 */
public class StorageStub implements StorageInterface {

    @Override
    public void init() {
    }

    @Override
    public boolean load(TasksBag c) {
        return true;
    }

    @Override
    public boolean save(Task c) {
        return true;
    }

    @Override
    public boolean delete(Task c) {
        return true;
    }

    @Override
    public void moveFileTo(String destination) {

    }

    @Override
    public void close() {
    }

}
