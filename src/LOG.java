import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhuangh7 on 17-6-21.
 */
public class LOG {
    static RandomAccessFile f = null;
    static boolean first = true;

    static void add(String s) {
        try {
            if (f == null)
                f = new RandomAccessFile("LOG", "rw");

            long length = f.length();
            f.seek(length);
            if (first) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                f.write((df.format(new Date())+"  ").getBytes());
                first = false;
            }
            f.write((s+"\n").getBytes());
        } catch (Exception e) {

        }
    }
}
