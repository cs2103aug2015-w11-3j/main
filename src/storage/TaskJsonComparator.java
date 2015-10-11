package storage;

import java.util.Comparator;

public class TaskJsonComparator implements Comparator<TaskJson> {

	@Override
	public int compare(TaskJson tj1, TaskJson tj2) {
		int id1 = Integer.parseInt(tj1.get("ID"));
		int id2 = Integer.parseInt(tj2.get("ID"));
		
		if (id1 < id2) {
			return -1;
		} else if (id1 == id2) {
			return 0;
		} else {
			return 1;
		}
	}

}
