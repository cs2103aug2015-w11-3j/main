package logic;

import common.TasksBag;

public class Feedback {
    private final TasksBag cBag;

    public Feedback(TasksBag bag) {

        cBag = bag;
    }

    /**
     * Provides the current sorted state of bag for UI
     * 
     * @return sorted state of bag
     */
    public TasksBag getcBag() {
        return cBag.getFiltered();
    }

}
