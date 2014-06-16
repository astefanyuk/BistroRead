package data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

import java.io.File;

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

    @Column(name = "position")
    public long position;

    public long maxContentPosition;

    public Book() {

    }

    public Book(File file) {
        path = file.getAbsolutePath();
        size = file.length();
        modifiedDate = file.lastModified();
    }

}
