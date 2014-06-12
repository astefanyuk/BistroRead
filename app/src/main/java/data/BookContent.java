package data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

/**
 * Created by AStefaniuk on 6/12/2014.
 */
public class BookContent extends Model {
    @Column(name = "Content")
    public String content;

    @Column(name = "Start")
    public long start;

    @Column(name = "End")
    public long end;
}
