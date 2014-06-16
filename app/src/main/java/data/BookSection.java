package data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

/**
 * Created by AStefaniuk on 6/13/2014.
 */
public class BookSection extends Model {

    @Column(name = "BookId")
    public long bookId;

    @Column(name = "Title")
    public String title;

    @Column(name = "Start")
    public long start;

    @Column(name = "End")
    public long end;
}
