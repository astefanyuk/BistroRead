package data;

import android.text.TextUtils;

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

    @Column(name = "SectionId")
    public long sectionId;

    @Column(name = "Position")
    public int position;

    public String[] text;

    public void parseContent() {

        if (!TextUtils.isEmpty(content)) {
            text = content.split(" ");
        } else {
            text = new String[]{};
        }
    }

    @Override
    public String toString() {
        return "BookContent: " + " Start=" + start + " End=" + end + " Position=" + position + " Text=" + content;
    }
}
