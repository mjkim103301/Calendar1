/*
* 2018 �ѹ���б� JAVA Term Project
* ����: �ϱ⿹���� �ִ� �޷�
* �ۼ���: �ѹ���б� ��ǻ�Ͱ��а� 20141897 �輺��, 20171578 �����
* */
package Project;

import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.awt.event.ActionEvent;
import java.io.*;
import java.sql.Time;
import java.util.regex.Pattern;

class CalendarDataManager extends JFrame { // 6*7�迭�� ��Ÿ�� �޷� ���� ���ϴ� class
    static final int CAL_WIDTH = 7;
    static final int CAL_HEIGHT = 6;
    private final int calLastDateOfMonth[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    int calDates[][] = new int[CAL_HEIGHT][CAL_WIDTH];
    int calYear;
    int calMonth;
    private int calDayOfMon;
    private int calLastDate;
    Calendar today = Calendar.getInstance();
    private Calendar cal;

    CalendarDataManager() {
        setToday();
    }

    void setToday() {
        calYear = today.get(Calendar.YEAR);
        calMonth = today.get(Calendar.MONTH);
        calDayOfMon = today.get(Calendar.DAY_OF_MONTH);
        makeCalData(today);
    }

    private void makeCalData(Calendar today2) {
        // 1���� ��ġ�� ������ ��¥�� ����
        int calStartingPos = (today2.get(Calendar.DAY_OF_WEEK) + 7 - (today2.get(Calendar.DAY_OF_MONTH)) % 7) % 7;

        calLastDate = calLastDateOfMonth[calMonth];
        // �޷� �迭 �ʱ�ȭ
        for (int i = 0; i < CAL_HEIGHT; i++) {
            for (int j = 0; j < CAL_WIDTH; j++) {
                calDates[i][j] = 0;
            }
        }
        // �޷� �迭�� �� ä���ֱ�
        for (int i = 0, num = 1, k = 0; i < CAL_HEIGHT; i++) {
            if (i == 0)
                k = calStartingPos;

            else k = 0;
            for (int j = k; j < CAL_WIDTH; j++) {
                if (num <= calLastDate) calDates[i][j] = num++;
            }
        }
    }


    void moveMonth(int mon) { // n�� ���ĸ� ���ڷ� �޾� �޷� �迭�� ����� �Լ�(1������ ������ �Ѿ�ų� 12������ �ķ� �Ѿ�� )
        calMonth += mon;

        if (calMonth > 11)
            while (calMonth > 11) {

                calMonth -= 12;
            }
        else if (calMonth < 0)
            while (calMonth < 0) {

                calMonth += 12;
            }
        cal = new GregorianCalendar(calYear, calMonth, calDayOfMon);
        makeCalData(cal);
    }

}//class CalendarDataManager extends JFrame


public class MainCalendar extends CalendarDataManager {

    class myButton extends JButton{
        int month, date;

        myButton(){
            this.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialogSchedule.set(month, date);
                }
            });
        }

        void set(int month, int date) {
            this.month = month;
            this.date = date;
        }
    }

    private myButton dateButs[][] = new myButton[6][7];

    private JButton btn_today; // ���� ��ư
    private JButton btn_previousMonth; // ���� �� ��ư
    private JButton btn_nextMonth; // ���� �� ��ư
    private JButton btn_schedule; // ��������
    private JLabel label_month, label_year;

    private String[] parsed; // ���� ����

    private DialogSchedule dialogSchedule; // ���������� ���� ���̾�α�

    private MainCalendar() throws ParseException, IOException, ParserConfigurationException, SAXException {
        ////////////
        /* ������ */
        ////////////
        setTitle("Calendar"); // ������ Ÿ��Ʋ
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // ������ �����츦 ������ ���α׷��� �����ϵ��� ����
        setSize(800, 800); // ������ ũ��
        setVisible(true); // ������ ���

        ////////////////
        /* ���̾�α� */
        ////////////////
        dialogSchedule = new DialogSchedule(this, "��������"); // ���̾�α� ����

        ////////////////
        /* API        */
        ////////////////
        WeatherAPI api = new WeatherAPI();
        parsed = api.getAPI();

        //////////////
        /* ����Ʈ�� */
        //////////////
        Container contentPane = getContentPane(); // ����Ʈ��
        contentPane.setLayout(new BorderLayout(0, 10)); // ����Ʈ�� ��ġ������ (����)

        ////////////////////////
        /* ��Ʈ�� �г�        */
        ////////////////////////
        // �г� �ʱ�ȭ
        JPanel panel_control = new JPanel();
        JPanel panel_blank=new JPanel();

        contentPane.add(panel_control, BorderLayout.NORTH); // ���� ����
        panel_control.setLayout(new FlowLayout(FlowLayout.CENTER, 7, 3)); // ��ġ������ (�÷ο�)
        panel_control.setBackground(new Color(160, 186, 237));
        contentPane.add(panel_blank, BorderLayout.SOUTH);
        // ������Ʈ �ʱ�ȭ
        label_year = new JLabel(calYear + "��");
        label_year.setForeground(new Color(255, 255, 255));
        btn_today = new JButton("����");

        btn_schedule = new JButton("��������");
        label_month = new JLabel((calMonth + 1) + "��");
        label_month.setForeground(new Color(255, 255, 255));
        btn_previousMonth = new JButton("<"); // ���� ��
        btn_nextMonth = new JButton(">"); // ���� ��

        // ������Ʈ �߰�
        panel_control.add(btn_today);
        panel_control.add(btn_previousMonth);
        panel_control.add(label_year);
        panel_control.add(label_month);
        panel_control.add(btn_nextMonth);
        panel_control.add(btn_schedule);
        // ������
        ListenForCalOpButtons lForCalOpButtons = new ListenForCalOpButtons();
        btn_previousMonth.addActionListener(lForCalOpButtons);
        btn_nextMonth.addActionListener(lForCalOpButtons);
        btn_today.addActionListener(lForCalOpButtons);
        btn_schedule.addActionListener(lForCalOpButtons);

        ////////////////////////
        /* Ķ���� �г�        */
        ////////////////////////
        JPanel panel_calendar = new JPanel();
        contentPane.add(panel_calendar, BorderLayout.CENTER); // �߾� ����
        panel_calendar.setLayout(new GridLayout(0, 7, 2, 2)); // ��ġ������ (�׸���)
        panel_calendar.setBorder(BorderFactory.createEmptyBorder(25, 50, 0, 50)); // ��� ����


        /////////////////
        /* Ķ���� ���� */
        /////////////////
        // ���� �簢�� ǥ��
        final String c_DAY_OF_WEEK[] = {"��", "��", "ȭ", "��", "��", "��", "��"};
        JButton[] dayOfWeekDisplay = new JButton[7];
        for (int i = 0; i < CAL_WIDTH; i++) {
            dayOfWeekDisplay[i] = new JButton(c_DAY_OF_WEEK[i]);
            dayOfWeekDisplay[i].setBorderPainted(false);
            dayOfWeekDisplay[i].setContentAreaFilled(false);
            dayOfWeekDisplay[i].setForeground(Color.WHITE);

            if (i == 0) dayOfWeekDisplay[i].setForeground(new Color(219, 0, 0));// ����
            else if (i == 6) dayOfWeekDisplay[i].setForeground(new Color(70, 65, 217));//�Ķ�
            dayOfWeekDisplay[i].setBackground(new Color(189, 189, 189)); //ȸ��
            dayOfWeekDisplay[i].setOpaque(true); // ������� ����
            dayOfWeekDisplay[i].setFocusPainted(false);
            panel_calendar.add(dayOfWeekDisplay[i]);
        }
        // ��¥ �簢�� ǥ��
        for (int i = 0; i < CAL_HEIGHT; i++) {
            for (int j = 0; j < CAL_WIDTH; j++) {
                dateButs[i][j] = new myButton();

                dateButs[i][j].setBorderPainted(false);
                dateButs[i][j].setContentAreaFilled(false);
                dateButs[i][j].setBackground(Color.WHITE);

                //��¥ ��ư ���� ���� ����
                dateButs[i][j].setVerticalAlignment(dateButs[i][j].TOP);
                dateButs[i][j].setHorizontalAlignment(dateButs[i][j].LEFT);

                dateButs[i][j].setOpaque(true); // ������� ����
                ListenForDateButs lForDateButs = new ListenForDateButs();
                dateButs[i][j].addActionListener(lForDateButs);
                panel_calendar.add(dateButs[i][j]);
            }
        }

        showCalDigitsAndText(); // �޷� ǥ��

    }

    /////////////////
    /* ������ ���� */
    /////////////////
    // Listener 1 (��¥�� ������ ��)
    private class ListenForDateButs implements ActionListener {
        public void actionPerformed(ActionEvent e) {
      
            dialogSchedule.setVisible(true);
        }
    }

    // Listener 2 (��ư�� ������ ��)
    public class ListenForCalOpButtons implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // ��ư ����
            if (e.getSource() == btn_today) // ����
                setToday();
            else if (e.getSource() == btn_previousMonth) // ���� ��(<)
                moveMonth(-1);
            else if (e.getSource() == btn_nextMonth) // ���� ��(>)
                moveMonth(1);
            else if (e.getSource() == btn_schedule) // ��������
                dialogSchedule.setVisible(true);
            else // ����
                moveMonth(0);

            label_month.setText((calMonth + 1) + "��");
            label_year.setText((calYear) + "��");
            showCalDigitsAndText(); // �޷� ǥ��
        }
    }

    ///////////////////////////////////////////
    /* ����, ����, ����, ������ ǥ�� �Լ�    */
    ///////////////////////////////////////////
    private void showCalDigitsAndText() {
        StringBuilder s = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        long count;
        for (int a = 0; a < CAL_HEIGHT; a++) {
            for (int b = 0; b < CAL_WIDTH; b++) {
                dateButs[a][b].setIcon(new ImageIcon("blank.png"));

            }
        }
        for (int i = 0; i < CAL_HEIGHT; i++) {
            for (int j = 0; j < CAL_WIDTH; j++) {
                String fontColor = "black";

                if (j == 0) fontColor = "red";
                else if (j == 6) fontColor="blue";

                // ���� ǥ��
                s.append("<font size=5><html><sup>" + calDates[i][j] + "</sup></font><br>");
                dateButs[i][j].set(calMonth + 1, calDates[i][j] );

                // ������
                if (calMonth == 0 && calDates[i][j] == 1) {
                    s.append("����");
                    fontColor = "red";
                }
                if (calMonth == 1 && (calDates[i][j] > 14 && calDates[i][j] < 18)) {
                    if (calDates[i][j] == 16) {
                        s.append("����");
                        fontColor = "red";
                    } else
                        fontColor = "red";
                }
                if (calMonth == 2 && calDates[i][j] == 1) {
                    s.append("������");
                    fontColor = "red";
                }
                if (calMonth == 4 && calDates[i][j] == 5) {
                    s.append("��̳�");
                    fontColor = "red";
                }
                if (calMonth == 4 && calDates[i][j] == 22) {
                    s.append("����ź����");
                    fontColor = "red";
                }
                if (calMonth == 5 && calDates[i][j] == 6) {
                    s.append("������");
                    fontColor = "red";
                }
                if (calMonth == 7 && calDates[i][j] == 15) {
                    s.append("������");
                    fontColor = "red";
                }
                if (calMonth == 8 && (calDates[i][j] > 22 && calDates[i][j] < 27)) {
                    if (calDates[i][j] == 25) {
                        s.append("�߼�");
                        fontColor = "red";
                    } else
                        fontColor = "red";
                }
                if (calMonth == 9 && calDates[i][j] == 3) {
                    s.append("��õ��");
                    fontColor = "red";
                }
                if (calMonth == 9 && calDates[i][j] == 9) {
                    s.append("�ѱ۳�");
                    fontColor = "red";
                }
                if (calMonth == 11 && calDates[i][j] == 25) {
                    s.append("��ź��");
                    fontColor = "red";
                }


                // ���ÿ� �ش��ϴ� ��¥�� �۾��� �ʷ����� ���̶���Ʈ
                if (calMonth == today.get(Calendar.MONTH) &&
                        calYear == today.get(Calendar.YEAR) &&
                        calDates[i][j] == today.get(Calendar.DAY_OF_MONTH)) {

                    fontColor="GREEN";
                } else {
                    dateButs[i][j].setBackground(Color.WHITE);
                }

                // ����
                GregorianCalendar gc = new GregorianCalendar(calYear, calMonth, calDates[i][j]);
                GregorianCalendar gcToday = new GregorianCalendar();
                count = gc.getTimeInMillis() / 1000 / 60 / 60 / 24 - gcToday.getTimeInMillis() / 1000 / 60 / 60 / 24;
                if (count > 2 && count < 10) {
                    switch (parsed[(int) count - 2]) {
                        case "����":
                            dateButs[i][j].setIcon(new ImageIcon("weather_sunny.png"));

                            break;
                        case "��������":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud0.png"));
                            break;
                        case "��������":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud1.png"));
                            break;
                        case "�������� ��":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud1_rain.png"));
                            break;
                        case "�������� ��":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud1_snow.png"));
                            break;
                        case "�������� ��/��":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud1_rainsnow.png"));
                            break;
                        case "�������� ��/��":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud1_snowrain.png"));
                            break;
                        case "�帲":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud2_rain.png"));
                            break;
                        case "�帮�� ��":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud2_rain.png"));
                            break;
                        case "�帮�� ��":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud2_snow.png"));
                            break;
                        case "�帮�� ��/��":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud2_rainsnow.png"));
                            break;
                        case "�帮�� ��/��":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud2_snowrain.png"));
                            break;
                        default:
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud0.png"));
                            break;


                    }

                }

                // ���� �����ֱ�
                String strSchedule = sdf.format(gc.getTime());
                try {
                    BufferedReader br = new BufferedReader(new FileReader(strSchedule + ".txt"));
                    s.append("<br>" + br.readLine());
                    br.close();
                } catch (IOException e) {

                }

                // ǥ��
                dateButs[i][j].setText("<html><b><font color=" + fontColor + ">" + s.substring(0) + "</font></b></html>");
                s.delete(0, s.length());
                dateButs[i][j].removeAll();

                // 0�� �����
                if (calDates[i][j] == 0) dateButs[i][j].setVisible(false);
                else dateButs[i][j].setVisible(true);
            }
        }
    }

    ////////////////////
    /* ���� ���� �Լ� */
    ////////////////////
    private void schedule() {
    }

    /////////////////////
    /* ���̾�α� ���� */
    /////////////////////
    class DialogSchedule extends JDialog {
        private JLabel label_schCombo = new JLabel("���");
        private JComboBox<String> comboBox = new JComboBox<>();
        private JLabel label_schDate = new JLabel("��¥");
        // ������ �ڸ����� ���� �ܿ��� ���� ����
        private JFormattedTextField tf_year = new JFormattedTextField(new MaskFormatter("2018"));
        private JFormattedTextField tf_month = new JFormattedTextField(new MaskFormatter("##"));
        private JFormattedTextField tf_date = new JFormattedTextField(new MaskFormatter("##"));
        private JLabel blank = new JLabel();
        private JLabel label_content = new JLabel("����");
        private JTextField tf_content = new JTextField(20);
        private JButton btn_save = new JButton("����");
        private JButton btn_del = new JButton("����");

        ListenForSaveDelButton lForSaveDelButton = new ListenForSaveDelButton();

        public void set(int month, int date) {
            tf_month.setText(String.valueOf(month));
            if(month<10) {
                tf_month.setText("0"+String.valueOf(month));

            }
            else {
                tf_month.setText(String.valueOf(month));
            }
            tf_date.setText(String.valueOf(date));
            if(date<10) {
                tf_date.setText("0"+String.valueOf(date));
            }

        }


        private DialogSchedule(JFrame frame, String title) throws ParseException, FileNotFoundException {
            super(frame, title);
            setSize(400, 200);//400, 200
            setLayout(new GridBagLayout());

            // ��� ����
            tf_year.setColumns(4);
            tf_month.setColumns(2);
            tf_date.setColumns(2);


            // �׸����
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(6, 6, 0, 0);
            gbc.gridx = GridBagConstraints.RELATIVE;
            gbc.gridy = 0;

            // ���(�޺��ڽ�)
            add(label_schCombo, gbc);
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            add(comboBox, gbc);

            // �Ͻ�
            gbc.gridy++;
            gbc.gridwidth = GridBagConstraints.WEST;
            add(label_schDate, gbc);
            gbc.weightx = 0.1f;
            add(tf_year, gbc);
            gbc.weightx = 0.05f;
            add(tf_month, gbc);
            gbc.weightx = 0.05f;
            add(tf_date, gbc);
            gbc.weightx = 0.8f;
            add(blank, gbc);
            gbc.weightx = 0;

            // ����
            gbc.gridy++;
            gbc.gridwidth = 1;
            add(label_content, gbc);
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            add(tf_content, gbc);

            // ��ư
            gbc.gridy++;
            add(btn_save, gbc);
            gbc.gridy++;
            add(btn_del, gbc);

            // ��ư ������
            btn_save.addActionListener(lForSaveDelButton);
            btn_del.addActionListener(lForSaveDelButton);

            comboBoxInitializer();

            // �޺��ڽ� ������(�͸� Ŭ����)
            comboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JComboBox<String> cb = (JComboBox<String>) e.getSource();
                    int index = cb.getSelectedIndex();  // 0, 1, ...
                    cb.setSelectedItem(index);
                    try {
                        String s = cb.getItemAt(index);
                        if (s != null) { // ���� ���� �߻��ϴ� action���� ���ܸ� ó���ϱ� ����
                            BufferedReader br = new BufferedReader(new FileReader(s)); // s == 20140403.txt
                            StringBuilder sb = new StringBuilder();
                            sb.append(br.readLine()); // ���� �б�
                            // ǥ��
                            tf_content.setText(sb.substring(0, sb.length()));
                            tf_year.setText(s.substring(0, 4));
                            tf_month.setText(s.substring(4, 6));
                            tf_date.setText(s.substring(6, 8));

                            br.close();
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            });


        }

        //////////////////////////
        /* �޺��ڽ� �ʱ�ȭ �۾� */
        //////////////////////////
        private void comboBoxInitializer() throws FileNotFoundException {
            File file = new File(System.getProperty("user.dir"));
            FilenameFilter filter = new FilenameFilter() {
                public Pattern p = Pattern.compile("[0-9]{8}?[.]txt"); // 8�ڸ� ���� �̸��� �ؽ�Ʈ���ϵ��� ã�� ���Խ�

                @Override
                public boolean accept(File dir, String name) {
                    return p.matcher(name).matches();
                }
            };
            File[] files = file.listFiles(filter); // �迭�� ���ϵ��� �Է�

            comboBox.removeAllItems(); // �ʱ�ȭ

            for (int i = 0; i < files.length; i++) {
                comboBox.addItem(files[i].getName()); // �޺��ڽ��� �߰�
            }

        }

        //////////////////////////
        /* ���� �� ���� �۾�    */
        //////////////////////////
        private class ListenForSaveDelButton implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ���� ����
                if (e.getSource() == btn_save) {
                    try {
                        FileWriter fw = new FileWriter(tf_year.getText() + tf_month.getText() + tf_date.getText() + ".txt"); // 20180523.txt
                        fw.write(tf_content.getText()); // ����
                        fw.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    showCalDigitsAndText();
                    try {
                        comboBoxInitializer();
                    }
                    catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
                // ���� ����
                else if (e.getSource() == btn_del) {
                    File file = new File(tf_year.getText() + tf_month.getText() + tf_date.getText() + ".txt");
                    if (file.exists()) {
                        try {
                            Files.delete(file.toPath());
                            comboBoxInitializer();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    showCalDigitsAndText();
                }
                // ����
                else {
                    System.out.println("Undefined button");
                }
            }
        }
    }

    public static void main(String[] args) throws ParseException, IOException, ParserConfigurationException, SAXException {
        MainCalendar frame = new MainCalendar();//���� ������ ����
    }
}