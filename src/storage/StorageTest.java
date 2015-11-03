//package storage;
//
//import static org.junit.Assert.*;
//
//import java.util.Calendar;
//import java.util.Date;
//
//import org.junit.Assert;
//import org.junit.Test;
//
//import common.Task;
//import common.TasksBag;
//
//public class StorageTest {
//
//    //@Test     Removed to prevent test removing my actual data
//    public void test() {
//        TasksBag cb;
//        int size;
//
//        Storage s = new Storage();
//        s.init();
//        Task c1 = new Task("storage dummy test1", createDate(2015, 10, 10, 0, 0), createDate(2015, 11, 11, 0, 0));
//        Task c2 = new Task("storage dummy test2", createDate(2016, 10, 10, 0, 0), createDate(2016, 11, 11, 0, 0));
//        Task c3 = new Task("storage dummy test3", createDate(2017, 10, 10, 0, 0), createDate(2017, 11, 11, 0, 0));
//
//        boolean result = s.save(c1);
//        Assert.assertEquals(true, result);
//
//        s.save(c2);
//        s.save(c3);
//
//        cb = new TasksBag();
//        s.load("", cb);
//        size = cb.size();
//        Assert.assertEquals(3, size);
//
//        c1.setName("new");
//        s.save(c1);
//
//        cb = new TasksBag();
//        s.load("", cb);
//
//        Task ct = cb.getTask(0);
//        String ctName = ct.getName();
//        Assert.assertEquals("new", ctName);
//
//        s.delete(c2);
//
//        cb = new TasksBag();
//        s.load("", cb);
//        size = cb.size();
//        Assert.assertEquals(2, size);
//
//        s.close();
//    }
//
//    // private methods
//    private static Date createDate(int year, int month, int day, int hour, int minute) {
//        Calendar calender = Calendar.getInstance();
//        calender.set(year, month, day, hour, minute);
//        return new Date(calender.getTimeInMillis());
//    }
//}
