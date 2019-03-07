/*
* 공공데이터포털에서 제공하는 기상청 API 예제 코드를 본 과제의 용도에 맞게 수정한 것입니다.
* 출처: https://www.data.go.kr/dataset/15000495/openapi.do
* 수정자: 한밭대학교 컴퓨터공학과 20141897 김성하
* */
package Project;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

class WeatherAPI {
    static String[] parsed = new String[8];
    private void requestAPI(String date) throws IOException, ParserConfigurationException, SAXException {
        String serviceKey = "WOYJtSZ06t3KeKHjPgWkbjV8PvoF%2B6b1Z0UaJwHcN33OO36VsFEymJHitvZsQRnQj7t3vXl2Q1wmXMNlhpLfLg%3D%3D";
        StringBuilder urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/MiddleFrcstInfoService/getMiddleLandWeather"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + serviceKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("regId", "UTF-8") + "=" + URLEncoder.encode("11B00000", "UTF-8")); /*예보구역코드 *활용 가이드 참조*/
        urlBuilder.append("&" + URLEncoder.encode("tmFc", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); /*발표 시각 *활용 가이드 참조*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/

        URL url = new URL(urlBuilder.toString());
        String urls = url.toString();

        // 파싱 밑작업
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        org.w3c.dom.Document doc = dBuilder.parse(urls);

        doc.getDocumentElement().normalize(); // 정리

        if(doc !=null) {
            NodeList nList = doc.getElementsByTagName("wf3Am"); // 3일 뒤
            Node node = nList.item(0); // 날씨
            parsed[0] = node.getFirstChild().getNodeValue();
            nList = doc.getElementsByTagName("wf4Am");
            node = nList.item(0); // 날씨
            parsed[1] = node.getFirstChild().getNodeValue();
            nList = doc.getElementsByTagName("wf5Am");
            node = nList.item(0); // 날씨
            parsed[2] = node.getFirstChild().getNodeValue();
            nList = doc.getElementsByTagName("wf6Am");
            node = nList.item(0); // 날씨
            parsed[3] = node.getFirstChild().getNodeValue();
            nList = doc.getElementsByTagName("wf7Am");
            node = nList.item(0); // 날씨
            parsed[4] = node.getFirstChild().getNodeValue();
            nList = doc.getElementsByTagName("wf8");
            node = nList.item(0); // 날씨
            parsed[5] = node.getFirstChild().getNodeValue();
            nList = doc.getElementsByTagName("wf9");
            node = nList.item(0); // 날씨
            parsed[6] = node.getFirstChild().getNodeValue();
            nList = doc.getElementsByTagName("wf10");
            node = nList.item(0); // 날씨
            parsed[7] = node.getFirstChild().getNodeValue();
        }
    }
    String[] getAPI() throws IOException, ParserConfigurationException, SAXException {
        final String AM = "0600", PM = "1800";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm"); // ex) 201805240600
        SimpleDateFormat simplerDateFormat = new SimpleDateFormat("yyyyMMdd"); // ex) 20180524
        Calendar calendar = new GregorianCalendar();

        StringBuilder now = new StringBuilder();
        StringBuilder yesterday = new StringBuilder();
        WeatherAPI a = new WeatherAPI();

        // 어제 시간 구하기
        now.append(simpleDateFormat.format(calendar.getTime())); // ex) 201805240600
        calendar.add(Calendar.DATE, -1); // back to yesterday
        yesterday.append(simplerDateFormat.format(calendar.getTime())); // ex) 20180523
        yesterday.append("1800"); // ex) 201805231800

        // 시간 선택
        // 기상청 API는 06시 18시에 갱신되며 최근 24시간 이내 데이터만 제공하므로 적절한 시간 선택이 필요함
        String s = now.substring(8, now.length());
        if (s.compareTo(AM) < 0) { // 00:00~05:59 = 전일 PM
            a.requestAPI(yesterday.substring(0));
        }
        else if (s.compareTo(AM) >= 0 && s.compareTo(PM) < 0 ){ // 06:00~17:59 = 당일 AM
            a.requestAPI(now.substring(0, 8)+AM); // 20180525 + 0600
        }
        else if (s.compareTo(PM) >= 0 ){ // 18:00~23:59 = 당일 PM
            a.requestAPI(now.substring(0, 8)+PM); // 20180525 + 1800
        }

        return parsed;
    }
}