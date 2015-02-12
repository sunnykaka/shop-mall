package com.kariqu.commonmodule;

import com.google.common.collect.Lists;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 在当前目录下创建一个简单的模块
 *
 * @author Athens(刘杰)
 * @Time 13-5-29 下午2:52
 */
public class CreateSimpleModule {

    private JFrame frame;

    private JTextField moduleName;

    private static final String ENCODE = "utf-8";

    private static Map<String, String> strMap = new HashMap<String, String>();

    static {
        strMap.put(".groovy", "def execute(context, params) {\n\tnew HashMap()\n}\n\ndef executeForm(context, params) {\n\tnew HashMap()\n}\n");

        strMap.put(".js", "<script type=\"text/javascript\"></script>");

        strMap.put(".vm", "<div></div>");

        strMap.put("Config.xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<module name=\"\">\n\t<config>\n\t\t<isCacheable>false</isCacheable>\n\t</config>\n\t<params>\n\t</params>\n</module>");

        strMap.put("Css.txt", "// 一行一个 css 文件, 相对路径即可." +
                "\n\n// 如想加载: http://assets.yijushang.com/css/common.css, 写 css/common.css 即可." +
                "\n// 双斜杠开头是注释, 会和空行一样被忽略, 就像你现在看到的这四行.\n\n");

        strMap.put("Edit.vm", "<div class=\"com_modify_wrapper com_edit\">\n\t这个模块不需要编辑\n</div>");
    }

    public CreateSimpleModule() {
        frame = new JFrame("创建简单模块");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocation(600, 380);
        frame.setSize(350, 110);

        JPanel pan = new JPanel();
        moduleName = new JTextField(20);
        pan.add(new JLabel("模块名 : "), 0);
        pan.add(moduleName, 1);

        Container con = frame.getContentPane();
        con.add(pan);

        JButton create = new JButton("创建");
        create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doCreate();
            }
        });
        JButton cancel = new JButton("取消");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        pan.add(create);
        pan.add(cancel);

        frame.setVisible(true);
    }

    private int success = 0, fail = 0;

    private void doCreate() {
        if (moduleName.getText() == null || "".equals(moduleName.getText())) {
            JOptionPane.showMessageDialog(frame, "请填写模块名");
            return;
        }
        String mdName = moduleName.getText();

        String path = CreateSimpleModule.class.getClassLoader().getResource("").getPath();
        File parent = new File(path.replace("target/classes/", "") + "src/main/java/com/kariqu/commonmodule/" + mdName);
        parent.mkdirs();

        File file;
        BufferedWriter out = null;
        for (Map.Entry<String, String> entry : strMap.entrySet()) {
            file = new File(parent + File.separator + mdName + entry.getKey());
            try {
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        fail++;
                        continue;
                    }
                }
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), ENCODE));
                out.write(entry.getValue());
                success++;
            } catch (IOException e) {
                fail++;
            } finally {
                try {
                    if (out != null)
                        out.close();
                } catch (IOException e) {
                    out = null;
                }
            }
        }
        JOptionPane.showMessageDialog(frame, "共有 " + strMap.size() + " 个文件, 创建成功 " + success + " 个, 创建失败 " + fail + " 个.");
    }

    public static void main(String[] args) throws Exception {
        new CreateSimpleModule();

        // operateCss(Action.Create);
    }

    public static enum Action {
        Create, Delete
    }

    public static void operateCss(Action action) throws Exception {
        String path = CreateSimpleModule.class.getClassLoader().getResource("").getPath();
        File parent = new File(path.replace("target/classes/", "") + "src/main/java/com/kariqu/commonmodule/");
        for (File file : parent.listFiles()) {
            if (!file.isDirectory()) continue;
            String parentName = file.getName();

            java.util.List<String> childName = Lists.newArrayList();
            for (File childFile : file.listFiles()) {
                childName.add(childFile.getName());
            }
            if (childName.contains(parentName + ".groovy")) {
                String newName = file.getAbsolutePath() + "\\" + parentName + "Css.txt";
                File newFile = new File(newName);
                switch (action) {
                    case Create:
                        if (newFile.createNewFile()) {
                            System.out.println("创建 (" + newName + ") 文件成功!");

                            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), ENCODE));
                            out.write(strMap.get("Css.txt"));
                            out.close();
                        } else {
                            System.out.println("文件 (" + newName + ") 已存在!");
                        }
                        break;
                    case Delete:
                        if (newFile.delete())
                            System.err.println("删除 (" + newName + ") 文件成功!");
                        else
                            System.out.println("文件 (" + newName + ") 删除失败!");
                        break;
                }
            }
        }
    }
}
