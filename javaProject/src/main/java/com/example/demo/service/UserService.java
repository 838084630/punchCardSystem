package com.example.demo.service;

import com.example.demo.config.Code;
import com.example.demo.config.Res;
import com.example.demo.mapper.UserMapper;
import com.example.demo.pojo.Record;
import com.example.demo.pojo.User;
import com.example.demo.utils.Base64Util;
import com.example.demo.utils.DateUtil;
import com.example.demo.utils.TokenUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.binding.BindingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class UserService {

    public static final String NORMAL_PUNCH_IN_TIME = "09:30";
    public static final String NORMAL_PUNCH_OUT_TIME = "18:00";
    public static final String SHIFT1_PUNCH_IN_TIME = "09:00";
    public static final String SHIFT1_PUNCH_OUT_TIME = "17:30";
    public static final String SHIFT2_PUNCH_IN_TIME = "10:00";
    public static final String SHIFT2_PUNCH_OUT_TIME = "18:30";
    public static final String SHIFT3_PUNCH_IN_TIME = "10:30";
    public static final String SHIFT3_PUNCH_OUT_TIME = "19:00";

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private Base64Util base64Util;

    @Autowired
    private DateUtil dateUtil;

    private Res<?> build;

    public Res<?> getUserInfo(User user) {
        User dbUser = userMapper.getUser(user.getUsername());
        if (dbUser == null || !user.getPassword().equals(base64Util.decode(dbUser.getPassword()))) {
            build = Res.builder().code(Code.VALIDERROR_EXCEPTION).data(null).build();
        } else {
            user.setToken(tokenUtil.generateToken());
            int recode = userMapper.updateUser(user.getUsername(), user.getToken());
            build = recode == 1 ? Res.builder().code(Code.SUCCESS).data(user).build() : Res.builder().code(Code.RUNERROR_EXCEPTION).data(user).build();
        }
        return build;
    }

    public Res<?> userCheck(User user) {
        if (userMapper.userCheck(user.getUsername(), user.getToken()) == 1) {
            build = Res.builder().code(Code.SUCCESS).data(user).build();
        } else {
            build = Res.builder().code(Code.NOLOGIN_EXCEPTION).data(null).build();
        }
        return build;
    }

    public Res<?> getRecord(String username, String date) {
        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(date)) {
            Record record = userMapper.getRecord(username, dateUtil.newDate(date));
            build = Res.builder().code(Code.SUCCESS).data(record).build();
        } else {
            build = Res.builder().code(Code.NOLOGIN_EXCEPTION).data(null).build();
        }
        return build;
    }

    public Res<?> writeRecord(Record record) {
        if (StringUtils.isEmpty(record.getUsername()) || (record.getPunchInTime() == null && record.getPunchOutTime() == null)) {
            build = Res.builder().code(Code.NOLOGIN_EXCEPTION).data(null).build();
        }
        Map<String, LocalDateTime> format = timeFormat(record.getPunchInTime(), record.getPunchOutTime());
        Map<String, Double> abnormalDate;
        long result;
        try {
            abnormalDate = getAbnormalDate(format);
            if (record.getId() != null) {
                result = userMapper.updateRecord(record.getId(), format.get("in"), format.get("out"), abnormalDate.get("comeLate"), abnormalDate.get("leaveEarly"));
            }else{
                result = userMapper.createRecord(record.getUsername(), format.get("in"), format.get("out"), abnormalDate.get("comeLate"), abnormalDate.get("leaveEarly"));
            }
            build = result == 1 ? Res.builder().code(Code.SUCCESS).data(record).build() : Res.builder().code(Code.SQLERR_EXCEPTION).data(record).build();
        } catch (ParseException e) {
            build = Res.builder().code(Code.VALIDERROR_EXCEPTION).data(record).build();
        }
        return build;
    }


    public Res<?> getAbsenceRecord(String username, String time, Integer days) {
        Calendar now = Calendar.getInstance();
        int thisMonth = Integer.parseInt((now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1)).replace("-", ""));
        if (thisMonth < Integer.parseInt(time.replace("-", ""))) {
            build = Res.builder().code(Code.NOLOGIN_EXCEPTION).data(null).build();
        }
        ArrayList<String> dayList = new ArrayList<>();
        for (int i = 1; i < days + 1; i++) {
            dayList.add(dateUtil.newDate(time + "-" + i));
        }
        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(time)) {
            //一日の出勤と退勤の一つだけを入力しないのデータを検索する
            List<Integer> record = userMapper.getAbsenceRecord(username, dayList);
            //一日の出勤と退勤どれも入力しないデータを検索する
            for (String day : dayList) {
                try {
                    userMapper.getDay(username, day);
                } catch (BindingException e) {
                    if (thisMonth == Integer.parseInt(time.replace("-", ""))) {
                        //删掉上一个sql查出的今天未打卡记录
                        for (int i = 0; i < record.size(); i++) {
                            if (record.get(i) == now.get(Calendar.DAY_OF_MONTH)) {
                                record.remove(i);
                            }
                        }
                        //只放入今天之前的
                        LocalDate queryData = LocalDate.parse(day, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        LocalDate today = LocalDate.parse(dateUtil.newDate(now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DAY_OF_MONTH)), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        if (queryData.isBefore(today)) {
                            record.add(Integer.valueOf(day.substring(8, 10)));
                        }
                    } else {
                        record.add(Integer.valueOf(day.substring(8, 10)));
                    }

                }
            }
//            record.stream().forEach(System.out::println);
            //只放日期处理
            build = Res.builder().code(Code.SUCCESS).data(record).build();
        } else {
            build = Res.builder().code(Code.NOLOGIN_EXCEPTION).data(null).build();
        }
        return build;
    }


    private Map<String, LocalDateTime> timeFormat(LocalDateTime punchInTime, LocalDateTime punchOutTime) {
        HashMap<String, LocalDateTime> map = new HashMap<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        if (punchInTime != null) {
            map.put("in", LocalDateTime.parse(punchInTime.format(df), df));
        }
        if (punchOutTime != null) {
            map.put("out", LocalDateTime.parse(punchOutTime.format(df), df));
        }
        return map;
    }

    public Res<?> getRecordByMonth(String username, String month) {

        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(month)) {
//            //ロジックA**************************************
//            //先月の25日から今月25日までの期間を今月の勤務時間として設置する
//            LocalDate timeTo = LocalDate.parse(month + "-26", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//            LocalDate timeFrom = timeTo.minusDays(1).minusMonths(1);
//            List<Record> recordByMonth = userMapper.getRecordByMonth(username,timeFrom, timeTo);
//            ArrayList<Map<String, String>> result = new ArrayList<>();
//            ArrayList<String> queryDays = new ArrayList<>();
//            ArrayList<String> allDays = new ArrayList<>();
//            for (Record item : recordByMonth) {
//                HashMap<String, String> recordMap = new HashMap<>();
//                recordMap.put("id", String.valueOf(item.getId()));
//                recordMap.put("date", String.valueOf(item.getPunchInTime()).substring(0, 10));
//                recordMap.put("punchInTime", String.valueOf(item.getPunchInTime()).substring(11));
//                String toTime = item.getPunchOutTime() != null ? String.valueOf(item.getPunchOutTime()).substring(11) :null;
//                recordMap.put("punchOutTime", toTime);
//                queryDays.add(String.valueOf(item.getPunchInTime()).substring(0, 10));
//                result.add(recordMap);
//            }
//            //出勤しない日付を追加する
//            long length = timeTo.toEpochDay() - timeFrom.toEpochDay();
//            for (long i = length; i > 0; i--) {
//                allDays.add(String.valueOf(timeTo.minusDays(i)));
//            }
//            allDays.removeAll(queryDays);
//            for (String day:allDays) {
//                HashMap<String, String> errRecordMap = new HashMap<>();
//                errRecordMap.put("id", null);
//                errRecordMap.put("date", day);
//                errRecordMap.put("punchInTime", null);
//                errRecordMap.put("punchOutTime", null);
//                result.add(errRecordMap);
//            }
//                Collections.sort(result,new Comparator<Map<String,String>>() {
//                    @Override
//                    public int compare(Map<String,String> o1, Map<String,String> o2) {
//                        Collator instance = Collator.getInstance();
//                        return instance.compare(o1.get("date"), o2.get("date"));
//                    }
//                });
//            result.stream().forEach(System.out::println);
//            build = Res.builder().code(Code.SUCCESS).data(result).build();


            //ロジックB*****************************************
            List<Record> recordByMonthB = userMapper.getRecordByMonthB(username, month);
            ArrayList<Map<String, String>> resultB = new ArrayList<>();
            ArrayList<String> queryDaysB = new ArrayList<>();
            ArrayList<String> allDaysB = new ArrayList<>();
            for (Record item : recordByMonthB) {
                HashMap<String, String> recordMap = new HashMap<>();
                recordMap.put("id", String.valueOf(item.getId()));
                recordMap.put("date", String.valueOf(item.getPunchInTime()).substring(0, 10));
                recordMap.put("punchInTime", String.valueOf(item.getPunchInTime()).substring(11));
                String toTime = item.getPunchOutTime() != null ? String.valueOf(item.getPunchOutTime()).substring(11) : null;
                recordMap.put("punchOutTime", toTime);
                String comeLate = item.getComeLate() != null ? String.valueOf(item.getComeLate()) : null;
                String leaveEarly = item.getLeaveEarly() != null ? String.valueOf(item.getLeaveEarly()) : null;
                recordMap.put("comeLate", comeLate);
                recordMap.put("leaveEarly", leaveEarly);
                queryDaysB.add(String.valueOf(item.getPunchInTime()).substring(0, 10));
                resultB.add(recordMap);
            }
            int lengthB = getLastDay(month).lengthOfMonth();
            for (long i = lengthB; i > 0; i--) {
                allDaysB.add(String.valueOf(getLastDay(month).minusDays(i - 1)));
            }
            allDaysB.removeAll(queryDaysB);
            String format = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate today = LocalDate.parse(format, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            for (String day : allDaysB) {
                LocalDate dayItem = LocalDate.parse(day, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (!today.isBefore(dayItem)) {
                    HashMap<String, String> errRecordMap = new HashMap<>();
                    errRecordMap.put("id", null);
                    errRecordMap.put("date", day);
                    errRecordMap.put("punchInTime", null);
                    errRecordMap.put("punchOutTime", null);
                    errRecordMap.put("comeLate", null);
                    errRecordMap.put("leaveEarly", null);
                    resultB.add(errRecordMap);
                }
            }
            Collections.sort(resultB, new Comparator<Map<String, String>>() {
                @Override
                public int compare(Map<String, String> o1, Map<String, String> o2) {
                    Collator instance = Collator.getInstance();
                    return instance.compare(o1.get("date"), o2.get("date"));
                }
            });
            build = Res.builder().code(Code.SUCCESS).data(resultB).build();

        } else {
            build = Res.builder().code(Code.VALIDERROR_EXCEPTION).data(null).build();
        }

        return build;
    }

    /**
     * 一か月の最初の日を取得する
     *
     * @param yearAndMonth yyyy-MM
     * @return 最初の日 yyyy-MM-dd
     */
    private LocalDate getFirstDay(String yearAndMonth) {
        String[] yearAndMonthArr = yearAndMonth.split("-");
        int year = Integer.parseInt(yearAndMonthArr[0]);
        String monthStr = yearAndMonthArr[1].startsWith("0") ? yearAndMonthArr[1].substring(yearAndMonthArr[1].length() - 1) : yearAndMonthArr[1];
        int month = Integer.parseInt(monthStr);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        LocalDate firstDay = LocalDate.parse(dateUtil.newDate(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH)), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return firstDay;
    }

    /**
     * 一か月の最後の日を取得する
     *
     * @param yearAndMonth yyyy-MM
     * @return 最後の日 yyyy-MM-dd
     */
    private LocalDate getLastDay(String yearAndMonth) {
        String[] yearAndMonthArr = yearAndMonth.split("-");
        int year = Integer.parseInt(yearAndMonthArr[0]);
        String monthStr = yearAndMonthArr[1].startsWith("0") ? yearAndMonthArr[1].substring(yearAndMonthArr[1].length() - 1) : yearAndMonthArr[1];
        int month = Integer.parseInt(monthStr);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        LocalDate lastDay = LocalDate.parse(dateUtil.newDate(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH)), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return lastDay;
    }

    /**
     * millisecondsをhoursに換算する
     *
     * @param time milliseconds
     * @return hours
     */
    public static String getHoursForLong(Long time) {
        float newTime;
        time = time / 1000;
        newTime = (float) time % (1000 * 60 * 60 * 24) / (1000 * 60 * 60);
        newTime = newTime * 1000;
        return String.format("%.2f", newTime);
    }

    private Map<String, Double> getAbnormalDate(Map<String, LocalDateTime> format) throws ParseException {
        HashMap<String, Double> abnormalDateMap = new HashMap<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm");
        String punchInTimeStr = df.format(format.get("in"));
        String punchOutTimeStr = format.get("out") != null ? df.format(format.get("out")) : null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date startTime;
        Date endTime;
        Date normalStartTime;
        Date normalEndTime;
        Double comeLate = null;
        Double leaveEarly = null;
        normalStartTime = sdf.parse(NORMAL_PUNCH_IN_TIME);
        normalEndTime = sdf.parse(NORMAL_PUNCH_OUT_TIME);
        if (StringUtils.isEmpty(punchOutTimeStr)) {
            startTime = sdf.parse(punchInTimeStr);
            if (startTime.after(normalStartTime)) {
                comeLate = new Double(getHoursForLong(startTime.getTime() - normalStartTime.getTime())) > 7.5 ?
                        7.5 : new Double(getHoursForLong(startTime.getTime() - normalStartTime.getTime()));
            }
            abnormalDateMap.put("comeLate", comeLate);
        } else {
            startTime = sdf.parse(punchInTimeStr);
            endTime = sdf.parse(punchOutTimeStr);
            if (startTime.after(normalStartTime)) {
                comeLate = new Double(getHoursForLong(startTime.getTime() - normalStartTime.getTime())) > 7.5 ?
                        7.5 : new Double(getHoursForLong(startTime.getTime() - normalStartTime.getTime()));
            }
            if (endTime.before(normalEndTime)) {
                leaveEarly = new Double(getHoursForLong(normalEndTime.getTime() - endTime.getTime())) > 7.5 ?
                        7.5 : new Double(getHoursForLong(normalEndTime.getTime() - endTime.getTime()));
            }
            abnormalDateMap.put("comeLate", comeLate);
            abnormalDateMap.put("leaveEarly", leaveEarly);
        }
        return abnormalDateMap;
    }
}
