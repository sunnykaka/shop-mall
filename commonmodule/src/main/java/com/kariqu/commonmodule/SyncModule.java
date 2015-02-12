package com.kariqu.commonmodule;

import com.google.common.collect.Lists;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.util.ModuleParamAndXmlConverter;
import com.kariqu.designcenter.domain.util.RenderUtil;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

/**
 * 同步模块到某个机器的编辑模式
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-9-12
 *        Time: 上午10:02
 */

public class SyncModule {

    private JButton button;
    private JTextField ipAddress;
    private JFrame f;
    private JTextField portNumber;
    private JTextField databaseName;
    private JTextField userName;
    private JPasswordField passWord;
    private JComboBox select;
    private int error = 0;

    private java.util.List<String> ignoreList = Lists.newArrayList();
    private java.util.List<String> insertList = Lists.newArrayList();
    private java.util.List<String> updateList = Lists.newArrayList();

    private static final File PARENT_FILE;
    static {
        String path = SyncModule.class.getClassLoader().getResource("").getPath().replace("target/classes/", "");
        PARENT_FILE = new File(path + "src/main/java/com/kariqu/commonmodule");
    }

    public SyncModule() {
        f = new JFrame("模板更新界面");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocation(500, 500);

        f.setSize(350, 230);
        Container con = f.getContentPane();
        JPanel pan = new JPanel();
        con.add(pan);

        JLabel sl = new JLabel("       模块        :");
        JLabel il = new JLabel("      IP地址      :");
        JLabel pl = new JLabel("       端口        :");
        JLabel dl = new JLabel("       库名        :");
        JLabel ul = new JLabel("      用户名      :");
        JLabel wl = new JLabel("       密码        :");

        select = new JComboBox(fileName().toArray());
        select.insertItemAt("All", 0);
        select.setSelectedIndex(0);
        pan.add(sl);
        pan.add(select);

        ipAddress = new JTextField(18);
        ipAddress.setText("localhost");
        pan.add(il);
        pan.add(ipAddress);

        portNumber = new JTextField(18);
        portNumber.setText("3306");
        pan.add(pl);
        pan.add(portNumber);

        databaseName = new JTextField(18);
        databaseName.setText("boss");
        pan.add(dl);
        pan.add(databaseName);

        userName = new JTextField(18);
        userName.setText("root");
        pan.add(ul);
        pan.add(userName);

        passWord = new JPasswordField(18);
        passWord.setText("root");
        pan.add(wl);
        pan.add(passWord);

        button = new JButton("确认");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                readFileByLines();
            }
        });
        JButton cancel = new JButton("取消");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                f.dispose();
            }
        });
        pan.add(button);
        pan.add(cancel);

        f.setVisible(true);
    }

    public static void main(String arg[]) {
        new SyncModule();
    }

    private java.util.List<String> fileName() {
        java.util.List<String> fileList = Lists.newArrayList();
        for (File fl : PARENT_FILE.listFiles()) {
            if (!".svn".equalsIgnoreCase(fl.getName()) && fl.isDirectory())
                fileList.add(fl.getName());
        }
        return fileList;
    }
    /**
     * 读取模板里面的内容
     */
    private void readFileByLines() {
        DataBase dataBase = new DataBase();
        if (StringUtils.isBlank(ipAddress.getText())) {
            JOptionPane.showMessageDialog(f, "ip地址能不为空");
            return;
        }
        if (StringUtils.isBlank(portNumber.getText())) {
            JOptionPane.showMessageDialog(f, "端口号不能为空");
            return;
        }
        if (StringUtils.isBlank(databaseName.getText())) {
            JOptionPane.showMessageDialog(f, "数据库名不能为空");
            return;
        }
        if (StringUtils.isBlank(userName.getText())) {
            JOptionPane.showMessageDialog(f, "用户名名不能为空");
            return;
        }
        String password = new String(passWord.getPassword());
        if (StringUtils.isBlank(password)) {
            JOptionPane.showMessageDialog(f, "密码不能为空");
            return;
        }
        dataBase.setDatabaseName(databaseName.getText());
        dataBase.setIpAddress(ipAddress.getText());
        dataBase.setPortNum(portNumber.getText());
        dataBase.setPassword(password);
        dataBase.setUserName(userName.getText());

        File[] files;
        if ("all".equalsIgnoreCase(select.getSelectedItem().toString())) {
            files = PARENT_FILE.listFiles();
        } else {
            files = (File[]) Arrays.asList(new File(PARENT_FILE, select.getSelectedItem().toString())).toArray();
        }

        try {
            //循环整个模板的包目录
            for (File packageFile : files) {
                CommonModule commonModule = new CommonModule();
                if (packageFile.listFiles() != null && !packageFile.getName().equals(".svn")) {
                    int recordNum = 0;
                    //循环每个模板里面的文件是否符合
                    for (File moduleFile : packageFile.listFiles()) {
                        if (!moduleFile.getName().equals(".svn") || !moduleFile.getName().equals(".txt")) {
                            if (moduleFile.getName().equals(packageFile.getName() + ".groovy")) {
                                recordNum ++;
                            } else if (moduleFile.getName().equals(packageFile.getName() + ".vm")) {
                                recordNum ++;
                            } else if (moduleFile.getName().equals(packageFile.getName() + "Config.xml")) {
                                recordNum ++;
                            } else if (moduleFile.getName().equals(packageFile.getName() + "Edit.vm")) {
                                recordNum ++;
                            } else if (moduleFile.getName().equals(packageFile.getName() + ".js")) {
                                recordNum ++;
                            }
                        }
                    }
                    //当模块符合时对其进行操作
                    if (recordNum == 5) {
                        commonModule.setName(packageFile.getName());
                        commonModule = RenderUtil.readModuleDir(packageFile, commonModule);
                        ConnectionDatabase(dataBase, commonModule);
                    } else {
                        ignoreList.add(packageFile.getName());
                    }
                    if (error == 1) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            error = 1;
            e.printStackTrace();
        } finally {
            if (error == 1) {
                JOptionPane.showMessageDialog(f, "数据操作失败");
            } else {
                JOptionPane.showMessageDialog(f,
                        String.format("添加了 %d 个模块%s, 修改了 %d 个模块%s, 忽略了 %d 个模块%s",
                                insertList.size(), strList(insertList),
                                updateList.size(), strList(updateList),
                                ignoreList.size(), strList(ignoreList)));
                insertList.clear();
                updateList.clear();
                ignoreList.clear();
                error = 0;
            }
        }
    }

    private String strList(java.util.List<String> list) {
        StringBuilder sbd = new StringBuilder("(");

        for (int i = 0; i < list.size(); i++) {
            sbd.append(list.get(i)).append(",");
            if ((i + 1) % 5 == 0) sbd.append("\n");
        }
        sbd.append(")");

        if (sbd.length() == 2) sbd.delete(0, sbd.length());
        else if (sbd.length() > 2) sbd.delete(sbd.length() - 2, sbd.length() - 1);
        return sbd.toString();
    }

    /**
     * 根据参数链接数据库
     *
     * @param dataBase
     */
    private void ConnectionDatabase(DataBase dataBase, CommonModule commonModule) throws Exception {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet resultSet;
        String sql = "SELECT * FROM commonModule where name='" + commonModule.getName() + "'";

        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://" + dataBase.getIpAddress() + ":" + dataBase.getPortNum() + "/" + dataBase.getDatabaseName();

        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, dataBase.getUserName(), dataBase.getPassword());

            if (conn.isClosed()) {
                error = 1;
                JOptionPane.showMessageDialog(f, "数据库链接失败,请确认信息是否输入正确");
            }
            pst = conn.prepareStatement(sql);
            resultSet = pst.executeQuery();
            // 判断模板名称是否成在于数据库
            if (resultSet.next()) {
                sql = " update CommonModule set editModuleContent=?,editFormContent=?," +
                        "editLogicCode=?,editModuleJs=?,moduleConfig=?,config=?,moduleCssContent=? where name=?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, commonModule.getEditModuleContent());
                pst.setString(2, commonModule.getEditFormContent());
                pst.setString(3, commonModule.getEditLogicCode());
                pst.setString(4, commonModule.getEditModuleJs());
                pst.setString(5, commonModule.getModuleConfig());
                pst.setString(6, ModuleParamAndXmlConverter.readModuleConfig(commonModule.getModuleConfig()));
                pst.setString(7, commonModule.getModuleCssContent());
                pst.setString(8, commonModule.getName());
                pst.executeUpdate();

                updateList.add(commonModule.getName());
            } else {
                sql = " insert into CommonModule (editModuleContent,editFormContent,editLogicCode," +
                        "editModuleJs,name,version,moduleConfig,config,moduleCssContent) values (?,?,?,?,?,?,?,?,?)";
                pst = conn.prepareStatement(sql);
                pst.setString(1, commonModule.getEditModuleContent());
                pst.setString(2, commonModule.getEditFormContent());
                pst.setString(3, commonModule.getEditLogicCode());
                pst.setString(4, commonModule.getEditModuleJs());
                pst.setString(5, commonModule.getName());
                pst.setString(6, "1.0");
                pst.setString(7, commonModule.getModuleConfig());
                pst.setString(8, ModuleParamAndXmlConverter.readModuleConfig(commonModule.getModuleConfig()));
                pst.setString(9, commonModule.getModuleCssContent());
                pst.executeUpdate();

                insertList.add(commonModule.getName());
            }
        } catch (Exception e) {
            error = 1;
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (pst != null) {
                pst.close();
            }
        }
    }

    private static class DataBase {
        private String ipAddress;
        private String portNum;
        private String databaseName;
        private String userName;
        private String password;

        public String getDatabaseName() {
            return databaseName;
        }

        public void setDatabaseName(String databaseName) {
            this.databaseName = databaseName;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPortNum() {
            return portNum;
        }

        public void setPortNum(String portNum) {
            this.portNum = portNum;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}

