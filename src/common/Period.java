package common;

import java.util.Date;

/**
 * Represents a period of time for easy calendar based processing (convenience
 * wrapper class for events/blocked timeslots) Period must have start and end
 * dates.
 *
 * Do not use Period to represent Celebi start and end date, because Celebis
 * might not have both dates.
 */
public class Period {

    private Date _start;
    private Date _end;

    private static final String ERR_MISSING_DATE = "common.Period constructor requires non-null arguments";
    private static final String ERR_START_EQUALS_END = "common.Period constructor requires start and end dates to be separate";
    private static final String ERR_START_AFTER_END = "common.Period requires start date to be before end date";

    /**
     * Constructor
     *
     * Constraints (handled by throwing exceptions): argument dates must be
     * distinct and not null.
     *
     * Pre-conditions: NIL (behaviour completely defined at runtime).
     *
     * Post-conditions: New Period constructed with earlier date argument as
     * start date and later date argument as end date. Start and end dates are
     * clones of constructor args for mutate protection.
     */
    public Period(Date a, Date b) throws IllegalArgumentException {

        if (a == null || b == null) {
            throw new IllegalArgumentException(ERR_MISSING_DATE);
        }

        a = (Date) a.clone();
        b = (Date) b.clone();

        if (b.before(a)) {
            _start = b;
            _end = a;

        } else if (a.before(b)) {
            _start = a;
            _end = b;

        } else {
            throw new IllegalArgumentException(ERR_START_EQUALS_END);
        }
    }

    // all getters clone the internal dates to protect from mutation.
    public Date getStart() {
        return (Date) _start.clone();
    }

    public Date getEnd() {
        return (Date) _end.clone();
    }

    // all setters use clones of date arguments for mutate protection
    public void setStart(Date newStart) {
        if (!newStart.before(_end)) {
            throw new IllegalArgumentException(ERR_START_AFTER_END);
        }
        _start = (Date) newStart.clone();
    }

    public void setEnd(Date newEnd) {
        if (!_start.before(newEnd)) {
            throw new IllegalArgumentException(ERR_START_AFTER_END);
        }
        _end = (Date) newEnd.clone();
    }

    public boolean clashesWith(Period p) {
        // TODO
        return false;
    }

}