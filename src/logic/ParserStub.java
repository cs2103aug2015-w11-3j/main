package logic;

import java.nio.file.Path;
import java.util.Date;

import common.Task.DataType;
import parser.Command;
import parser.Command.Type;
import parser.ParserInterface;

public class ParserStub implements ParserInterface {

    // @Override
    public void init() {
        // TODO Auto-generated method stub
    }

    // @Override
    public Command parseCommand(String s) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Command makeAdd(String name, Date start, Date end) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Command makeUpdate(int taskUID, DataType fieldType, Object newValue) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Command makeDelete(int taskUID) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Command makeQuit() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Command makeInvalid() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Command makeShow(Type showtype) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Command makeRedo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Command makeUndo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Command makeMark(int taskUID) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Command makeUnmark(int taskUID) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Command makeSearch(String searchKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Command makeFilterDate(Date rangeStart, Date rangeEnd) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Command makeMove(Path newPath) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Command makeHelp(Type helpTarget) {
        // TODO Auto-generated method stub
        return null;
    }

}
