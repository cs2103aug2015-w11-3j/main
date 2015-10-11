package storage;

import java.util.Comparator;

class TaskJsonComparator implements Comparator<TaskJson> {

	@Override
	public int compare(TaskJson tj1, TaskJson tj2) {
		int id1 = tj1.getId();
		int id2 = tj2.getId();
		
		if (id1 <= 0 || id2 <= 0) {
			throw new IllegalArgumentException("Trying to compare TaskJson without ID");
		} else if (id1 < id2) {
			return -1;
		} else if (id1 == id2) {
			return 0;
		} else {
			return 1;
		}
	}

}
