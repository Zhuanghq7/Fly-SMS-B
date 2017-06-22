import ZNetServer.Interface.MessageReceiveCallBack;
import ZNetServer.ZServer;
import com.tencent.xinge.Message;
import com.tencent.xinge.Style;
import com.tencent.xinge.XingeApp;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 瞎几把乱写
 * Created by zhuangh7 on 17-6-21.
 */
public class Main {

    private static ArrayList<String> Log = new ArrayList<String>();
    private static JTextArea temp1, temp2;

    private static XingeApp xinge = new XingeApp(2100261173, "53f616db7283e77ec6bb738e509a918b");

    public static void main(String[] args) {
        ZServer server = new ZServer();
        server.setMessageReceiveCallBack(
                Main::callback
        );
        LOG.add("Sys start");

        JFrame frame = new JFrame("Fly-SMS");
        frame.setResizable(false);
        frame.setLayout(null);

        temp1 = new JTextArea();
        temp1.setEditable(false);
        refreshD(temp1);
        temp1.setSize(250, 380);
        temp1.setLocation(0, 0);
        frame.add(temp1);

        temp2 = new JTextArea();
        temp2.setEditable(false);
        JScrollPane scrool = new JScrollPane(temp2);
        scrool.setSize(250, 380);
        scrool.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrool.setLocation(250, 0);
        StringBuilder SB = new StringBuilder();
        for (int i = 0; i < Log.size(); i++) {
            SB.append(Log.get(0) + "\n");
        }
        temp2.setText(SB.toString());

        frame.add(scrool);

        JButton Bstart = new JButton("start");
        Bstart.addActionListener(
                (ActionEvent actionEvent) -> {
                    server.Zstart();
                    addLog("netSdk start");

                }
        );
        Bstart.setSize(80, 190);
        Bstart.setLocation(500, 0);
        frame.add(Bstart);

        JButton Bstop = new JButton("stop");
        Bstop.addActionListener(
                (ActionEvent actionEvent) -> {
                    server.Zstop();
                    addLog("netSdk stop");
                }
        );
        Bstop.setSize(80, 190);
        Bstop.setLocation(500, 190);
        frame.add(Bstop);

        frame.setSize(580, 380);
        frame.setLocation(500, 500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static void refreshD(JTextArea t) {
        try {
            File f = new File("ZMMP");
            long LENGHT = f.length();
            RandomAccessFile memoryMappedFile = new RandomAccessFile("ZMMP", "rw");
            MappedByteBuffer out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, LENGHT);
            CharBuffer cb = StandardCharsets.UTF_8.newDecoder().decode(out);
            t.setText(cb.toString());
        }catch (Exception e){

        }
    }

    private static String callback(String s) {
        Scanner scanner = new Scanner(s);
        String result;
        switch (judge(s)) {
            case 0:
                scanner.next();
                result = mmpSdk.get(scanner.next());
                if (result == null) {
                    return "false";
                } else {
                    if (scanner.next().equals(result)) {
                        return "true";
                    } else {
                        return "false";
                    }
                }
            case 1:
                scanner.next();
                String key = scanner.next();
                result = mmpSdk.get(key);
                if (result != null) {
                    return "false";
                } else {
                    mmpSdk.put(key, scanner.next());
                    refreshD(temp1);
                    return "true";
                }
            case 2:
                scanner.next();
                String account = scanner.next();
                scanner.next();
                JSONObject r = PushSingleAccount(account,"有新的来电","电话号码为:"+scanner.next());
                if(r.get("ret_code").equals("0")){
                    return "true";
                }else{
                    return "false";
                }
            case 3:
                scanner.next();
                String account1 = scanner.next();
                scanner.next();
                JSONObject R = PushSingleAccount(account1,"有新的短信",scanner.next());
                if(R.get("ret_code").equals("0")){
                    return "true";
                }else{
                    return "false";
                }
        }
        return null;
    }

    private static int judge(String a) {
        //0:登录 1:注册 2:推送电话 3:推送短信
        Scanner s = new Scanner(a);
        switch (s.next()){
            case "signUp":
                return 1;
            case "signIn":
                return 0;
            case "listener":
                s.next();
                if(s.next().equals("sms")){
                    return 3;
                }else{
                    return 2;
                }
        }
        return -1;
    }

    private static void addLog(String s) {
        LOG.add(s);
        Log.add(s);
        StringBuilder SB = new StringBuilder();
        for (int i = 0; i < Log.size(); i++) {
            SB.append(Log.get(i) + "\n");
        }
        temp2.setText(SB.toString());
    }

    private static JSONObject PushSingleAccount(String account,String title,String content) {
        Message message = new Message();
        message.setTitle(title);
        message.setContent(content);
        message.setType(Message.TYPE_NOTIFICATION);
        message.setStyle(new Style(0,1,1,1,0,1,0,1));
        JSONObject ret = xinge.pushSingleAccount(0, account, message);
        return ret;
    }
}
