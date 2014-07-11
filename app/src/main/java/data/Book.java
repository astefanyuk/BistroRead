package data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

import java.io.File;
import java.util.Date;

/**
 * Created by AStefaniuk on 6/12/2014.
 */
public class Book extends Model {
    @Column(name = "Path")
    public String path;

    @Column(name = "Size")
    public long size;

    @Column(name = "ModifiedSize")
    public long modifiedDate;

    @Column(name = "ContentPosition")
    public long contentPosition;

    @Column(name = "ContentIndex")
    public int contentIndex;

    @Column(name = "MaxContentPosition")
    public long maxContentPosition;

    @Column(name = "ContentSize")
    public long contentSize;

    @Column(name = "OpenDate")
    public long openDate;

    public Book() {

    }

    public Book(File file) {
        path = file.getAbsolutePath();
        size = file.length();
        modifiedDate = file.lastModified();
    }

    @Override
    public String toString() {
        return "Book: " + path + " Size=" + size + " ModifiedDate=" +
                (new Date(modifiedDate)) + " ContentPosition=" + contentPosition +
                " ContentIndex=" + contentIndex +
                " MaxContentPosition=" + maxContentPosition;
    }

    public Long save2() {
        openDate = System.currentTimeMillis();
        return save();
    }
}
