package data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

/**
 * Created by AStefaniuk on 6/12/2014.
 */
public class Book extends Model {
    @Column(name = "Path")
    public String path;
}
