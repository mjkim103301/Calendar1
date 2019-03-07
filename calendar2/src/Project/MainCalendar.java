/*
* 2018 한밭대학교 JAVA Term Project
* 제목: 일기예보가 있는 달력
* 작성자: 한밭대학교 컴퓨터공학과 20141897 김성하, 20171578 김민지
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

class CalendarDataManager extends JFrame { // 6*7배열에 나타낼 달력 값을 구하는 class
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
        // 1일의 위치와 마지막 날짜를 구함
        int calStartingPos = (today2.get(Calendar.DAY_OF_WEEK) + 7 - (today2.get(Calendar.DAY_OF_MONTH)) % 7) % 7;

        calLastDate = calLastDateOfMonth[calMonth];
        // 달력 배열 초기화
        for (int i = 0; i < CAL_HEIGHT; i++) {
            for (int j = 0; j < CAL_WIDTH; j++) {
                calDates[i][j] = 0;
            }
        }
        // 달력 배열에 값 채워넣기
        for (int i = 0, num = 1, k = 0; i < CAL_HEIGHT; i++) {
            if (i == 0)
                k = calStartingPos;

            else k = 0;
            for (int j = k; j < CAL_WIDTH; j++) {
                if (num <= calLastDate) calDates[i][j] = num++;
            }
        }
    }


    void moveMonth(int mon) { // n달 전후를 인자로 받아 달력 배열을 만드는 함수(1월에서 전으로 넘어가거나 12월에서 후로 넘어갈때 )
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

    private JButton btn_today; // 오늘 버튼
    private JButton btn_previousMonth; // 이전 달 버튼
    private JButton btn_nextMonth; // 다음 달 버튼
    private JButton btn_schedule; // 일정관리
    private JLabel label_month, label_year;

    private String[] parsed; // 날씨 저장

    private DialogSchedule dialogSchedule; // 일정관리를 위한 다이얼로그

    private MainCalendar() throws ParseException, IOException, ParserConfigurationException, SAXException {
        ////////////
        /* 프레임 */
        ////////////
        setTitle("Calendar"); // 프레임 타이틀
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // 프레임 윈도우를 닫으면 프로그램을 종료하도록 설정
        setSize(800, 800); // 프레임 크기
        setVisible(true); // 프레임 출력

        ////////////////
        /* 다이얼로그 */
        ////////////////
        dialogSchedule = new DialogSchedule(this, "일정관리"); // 다이얼로그 생성

        ////////////////
        /* API        */
        ////////////////
        WeatherAPI api = new WeatherAPI();
        parsed = api.getAPI();

        //////////////
        /* 컨텐트팬 */
        //////////////
        Container contentPane = getContentPane(); // 컨텐트팬
        contentPane.setLayout(new BorderLayout(0, 10)); // 컨텐트팬 배치관리자 (보더)

        ////////////////////////
        /* 컨트롤 패널        */
        ////////////////////////
        // 패널 초기화
        JPanel panel_control = new JPanel();
        JPanel panel_blank=new JPanel();

        contentPane.add(panel_control, BorderLayout.NORTH); // 위로 정렬
        panel_control.setLayout(new FlowLayout(FlowLayout.CENTER, 7, 3)); // 배치관리자 (플로우)
        panel_control.setBackground(new Color(160, 186, 237));
        contentPane.add(panel_blank, BorderLayout.SOUTH);
        // 컴포넌트 초기화
        label_year = new JLabel(calYear + "년");
        label_year.setForeground(new Color(255, 255, 255));
        btn_today = new JButton("오늘");

        btn_schedule = new JButton("일정관리");
        label_month = new JLabel((calMonth + 1) + "월");
        label_month.setForeground(new Color(255, 255, 255));
        btn_previousMonth = new JButton("<"); // 이전 달
        btn_nextMonth = new JButton(">"); // 다음 달

        // 컴포넌트 추가
        panel_control.add(btn_today);
        panel_control.add(btn_previousMonth);
        panel_control.add(label_year);
        panel_control.add(label_month);
        panel_control.add(btn_nextMonth);
        panel_control.add(btn_schedule);
        // 리스너
        ListenForCalOpButtons lForCalOpButtons = new ListenForCalOpButtons();
        btn_previousMonth.addActionListener(lForCalOpButtons);
        btn_nextMonth.addActionListener(lForCalOpButtons);
        btn_today.addActionListener(lForCalOpButtons);
        btn_schedule.addActionListener(lForCalOpButtons);

        ////////////////////////
        /* 캘린더 패널        */
        ////////////////////////
        JPanel panel_calendar = new JPanel();
        contentPane.add(panel_calendar, BorderLayout.CENTER); // 중앙 정렬
        panel_calendar.setLayout(new GridLayout(0, 7, 2, 2)); // 배치관리자 (그리드)
        panel_calendar.setBorder(BorderFactory.createEmptyBorder(25, 50, 0, 50)); // 경계 설정


        /////////////////
        /* 캘린더 구현 */
        /////////////////
        // 요일 사각형 표시
        final String c_DAY_OF_WEEK[] = {"일", "월", "화", "수", "목", "금", "토"};
        JButton[] dayOfWeekDisplay = new JButton[7];
        for (int i = 0; i < CAL_WIDTH; i++) {
            dayOfWeekDisplay[i] = new JButton(c_DAY_OF_WEEK[i]);
            dayOfWeekDisplay[i].setBorderPainted(false);
            dayOfWeekDisplay[i].setContentAreaFilled(false);
            dayOfWeekDisplay[i].setForeground(Color.WHITE);

            if (i == 0) dayOfWeekDisplay[i].setForeground(new Color(219, 0, 0));// 빨강
            else if (i == 6) dayOfWeekDisplay[i].setForeground(new Color(70, 65, 217));//파랑
            dayOfWeekDisplay[i].setBackground(new Color(189, 189, 189)); //회색
            dayOfWeekDisplay[i].setOpaque(true); // 변경사항 적용
            dayOfWeekDisplay[i].setFocusPainted(false);
            panel_calendar.add(dayOfWeekDisplay[i]);
        }
        // 날짜 사각형 표시
        for (int i = 0; i < CAL_HEIGHT; i++) {
            for (int j = 0; j < CAL_WIDTH; j++) {
                dateButs[i][j] = new myButton();

                dateButs[i][j].setBorderPainted(false);
                dateButs[i][j].setContentAreaFilled(false);
                dateButs[i][j].setBackground(Color.WHITE);

                //날짜 버튼 왼쪽 위로 정렬
                dateButs[i][j].setVerticalAlignment(dateButs[i][j].TOP);
                dateButs[i][j].setHorizontalAlignment(dateButs[i][j].LEFT);

                dateButs[i][j].setOpaque(true); // 변경사항 적용
                ListenForDateButs lForDateButs = new ListenForDateButs();
                dateButs[i][j].addActionListener(lForDateButs);
                panel_calendar.add(dateButs[i][j]);
            }
        }

        showCalDigitsAndText(); // 달력 표시

    }

    /////////////////
    /* 리스너 구현 */
    /////////////////
    // Listener 1 (날짜를 눌렀을 때)
    private class ListenForDateButs implements ActionListener {
        public void actionPerformed(ActionEvent e) {
      
            dialogSchedule.setVisible(true);
        }
    }

    // Listener 2 (버튼을 눌렀을 때)
    public class ListenForCalOpButtons implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // 버튼 구분
            if (e.getSource() == btn_today) // 오늘
                setToday();
            else if (e.getSource() == btn_previousMonth) // 이전 달(<)
                moveMonth(-1);
            else if (e.getSource() == btn_nextMonth) // 다음 달(>)
                moveMonth(1);
            else if (e.getSource() == btn_schedule) // 일정관리
                dialogSchedule.setVisible(true);
            else // 예외
                moveMonth(0);

            label_month.setText((calMonth + 1) + "월");
            label_year.setText((calYear) + "년");
            showCalDigitsAndText(); // 달력 표시
        }
    }

    ///////////////////////////////////////////
    /* 숫자, 오늘, 일정, 공휴일 표시 함수    */
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

                // 숫자 표시
                s.append("<font size=5><html><sup>" + calDates[i][j] + "</sup></font><br>");
                dateButs[i][j].set(calMonth + 1, calDates[i][j] );

                // 공휴일
                if (calMonth == 0 && calDates[i][j] == 1) {
                    s.append("신정");
                    fontColor = "red";
                }
                if (calMonth == 1 && (calDates[i][j] > 14 && calDates[i][j] < 18)) {
                    if (calDates[i][j] == 16) {
                        s.append("설날");
                        fontColor = "red";
                    } else
                        fontColor = "red";
                }
                if (calMonth == 2 && calDates[i][j] == 1) {
                    s.append("삼일절");
                    fontColor = "red";
                }
                if (calMonth == 4 && calDates[i][j] == 5) {
                    s.append("어린이날");
                    fontColor = "red";
                }
                if (calMonth == 4 && calDates[i][j] == 22) {
                    s.append("석가탄신일");
                    fontColor = "red";
                }
                if (calMonth == 5 && calDates[i][j] == 6) {
                    s.append("현충일");
                    fontColor = "red";
                }
                if (calMonth == 7 && calDates[i][j] == 15) {
                    s.append("광복절");
                    fontColor = "red";
                }
                if (calMonth == 8 && (calDates[i][j] > 22 && calDates[i][j] < 27)) {
                    if (calDates[i][j] == 25) {
                        s.append("추석");
                        fontColor = "red";
                    } else
                        fontColor = "red";
                }
                if (calMonth == 9 && calDates[i][j] == 3) {
                    s.append("개천절");
                    fontColor = "red";
                }
                if (calMonth == 9 && calDates[i][j] == 9) {
                    s.append("한글날");
                    fontColor = "red";
                }
                if (calMonth == 11 && calDates[i][j] == 25) {
                    s.append("성탄절");
                    fontColor = "red";
                }


                // 오늘에 해당하는 날짜의 글씨색 초록으로 하이라이트
                if (calMonth == today.get(Calendar.MONTH) &&
                        calYear == today.get(Calendar.YEAR) &&
                        calDates[i][j] == today.get(Calendar.DAY_OF_MONTH)) {

                    fontColor="GREEN";
                } else {
                    dateButs[i][j].setBackground(Color.WHITE);
                }

                // 날씨
                GregorianCalendar gc = new GregorianCalendar(calYear, calMonth, calDates[i][j]);
                GregorianCalendar gcToday = new GregorianCalendar();
                count = gc.getTimeInMillis() / 1000 / 60 / 60 / 24 - gcToday.getTimeInMillis() / 1000 / 60 / 60 / 24;
                if (count > 2 && count < 10) {
                    switch (parsed[(int) count - 2]) {
                        case "맑음":
                            dateButs[i][j].setIcon(new ImageIcon("weather_sunny.png"));

                            break;
                        case "구름조금":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud0.png"));
                            break;
                        case "구름많음":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud1.png"));
                            break;
                        case "구름많고 비":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud1_rain.png"));
                            break;
                        case "구름많고 눈":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud1_snow.png"));
                            break;
                        case "구름많고 비/눈":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud1_rainsnow.png"));
                            break;
                        case "구름많고 눈/비":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud1_snowrain.png"));
                            break;
                        case "흐림":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud2_rain.png"));
                            break;
                        case "흐리고 비":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud2_rain.png"));
                            break;
                        case "흐리고 눈":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud2_snow.png"));
                            break;
                        case "흐리고 비/눈":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud2_rainsnow.png"));
                            break;
                        case "흐리고 눈/비":
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud2_snowrain.png"));
                            break;
                        default:
                            dateButs[i][j].setIcon(new ImageIcon("weather_cloud0.png"));
                            break;


                    }

                }

                // 일정 보여주기
                String strSchedule = sdf.format(gc.getTime());
                try {
                    BufferedReader br = new BufferedReader(new FileReader(strSchedule + ".txt"));
                    s.append("<br>" + br.readLine());
                    br.close();
                } catch (IOException e) {

                }

                // 표시
                dateButs[i][j].setText("<html><b><font color=" + fontColor + ">" + s.substring(0) + "</font></b></html>");
                s.delete(0, s.length());
                dateButs[i][j].removeAll();

                // 0일 숨기기
                if (calDates[i][j] == 0) dateButs[i][j].setVisible(false);
                else dateButs[i][j].setVisible(true);
            }
        }
    }

    ////////////////////
    /* 일정 관리 함수 */
    ////////////////////
    private void schedule() {
    }

    /////////////////////
    /* 다이얼로그 구현 */
    /////////////////////
    class DialogSchedule extends JDialog {
        private JLabel label_schCombo = new JLabel("목록");
        private JComboBox<String> comboBox = new JComboBox<>();
        private JLabel label_schDate = new JLabel("날짜");
        // 정해진 자릿수의 숫자 외에는 받지 않음
        private JFormattedTextField tf_year = new JFormattedTextField(new MaskFormatter("2018"));
        private JFormattedTextField tf_month = new JFormattedTextField(new MaskFormatter("##"));
        private JFormattedTextField tf_date = new JFormattedTextField(new MaskFormatter("##"));
        private JLabel blank = new JLabel();
        private JLabel label_content = new JLabel("내용");
        private JTextField tf_content = new JTextField(20);
        private JButton btn_save = new JButton("저장");
        private JButton btn_del = new JButton("삭제");

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

            // 모양 설정
            tf_year.setColumns(4);
            tf_month.setColumns(2);
            tf_date.setColumns(2);


            // 그리드백
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(6, 6, 0, 0);
            gbc.gridx = GridBagConstraints.RELATIVE;
            gbc.gridy = 0;

            // 목록(콤보박스)
            add(label_schCombo, gbc);
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            add(comboBox, gbc);

            // 일시
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

            // 내용
            gbc.gridy++;
            gbc.gridwidth = 1;
            add(label_content, gbc);
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            add(tf_content, gbc);

            // 버튼
            gbc.gridy++;
            add(btn_save, gbc);
            gbc.gridy++;
            add(btn_del, gbc);

            // 버튼 리스너
            btn_save.addActionListener(lForSaveDelButton);
            btn_del.addActionListener(lForSaveDelButton);

            comboBoxInitializer();

            // 콤보박스 리스너(익명 클래스)
            comboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JComboBox<String> cb = (JComboBox<String>) e.getSource();
                    int index = cb.getSelectedIndex();  // 0, 1, ...
                    cb.setSelectedItem(index);
                    try {
                        String s = cb.getItemAt(index);
                        if (s != null) { // 삭제 직후 발생하는 action에서 예외를 처리하기 위함
                            BufferedReader br = new BufferedReader(new FileReader(s)); // s == 20140403.txt
                            StringBuilder sb = new StringBuilder();
                            sb.append(br.readLine()); // 내용 읽기
                            // 표시
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
        /* 콤보박스 초기화 작업 */
        //////////////////////////
        private void comboBoxInitializer() throws FileNotFoundException {
            File file = new File(System.getProperty("user.dir"));
            FilenameFilter filter = new FilenameFilter() {
                public Pattern p = Pattern.compile("[0-9]{8}?[.]txt"); // 8자리 수가 이름인 텍스트파일들을 찾는 정규식

                @Override
                public boolean accept(File dir, String name) {
                    return p.matcher(name).matches();
                }
            };
            File[] files = file.listFiles(filter); // 배열에 파일들을 입력

            comboBox.removeAllItems(); // 초기화

            for (int i = 0; i < files.length; i++) {
                comboBox.addItem(files[i].getName()); // 콤보박스에 추가
            }

        }

        //////////////////////////
        /* 저장 및 삭제 작업    */
        //////////////////////////
        private class ListenForSaveDelButton implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 일정 저장
                if (e.getSource() == btn_save) {
                    try {
                        FileWriter fw = new FileWriter(tf_year.getText() + tf_month.getText() + tf_date.getText() + ".txt"); // 20180523.txt
                        fw.write(tf_content.getText()); // 내용
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
                // 일정 삭제
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
                // 예외
                else {
                    System.out.println("Undefined button");
                }
            }
        }
    }

    public static void main(String[] args) throws ParseException, IOException, ParserConfigurationException, SAXException {
        MainCalendar frame = new MainCalendar();//스윙 프레임 생성
    }
}