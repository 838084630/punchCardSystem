package com.example.demo.mapper;

import com.example.demo.pojo.Record;
import com.example.demo.pojo.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select * from attendance.user where username = #{username}")
    User getUser(@Param("username") String username);

    @Update("update attendance.user set token  = #{token} where username = #{username}")
    int updateUser(@Param("username") String username, @Param("token") String token);

    @Select("select count(username) from attendance.user where username = #{username} and token = #{token}")
    int userCheck(@Param("username") String username, @Param("token") String token);

    @Select("select * from attendance.record where username = #{username} and (substring(to_char(punch_in_time, 'YYYY-MM-DD HH24:MI:SS'),0,11) = #{date} or substring(to_char(punch_out_time, 'YYYY-MM-DD HH24:MI:SS'),0,11) = #{date})")
    Record getRecord(@Param("username") String username, @Param("date") String date);


    long updateRecord(Integer id,LocalDateTime punchOutTime,Double leaveEarly);

    long createRecord(String username,LocalDateTime punchInTime,Double comeLate);
//    long createRecord(@Param("username")String username,@Param("punchInTime")LocalDateTime punchInTime,@Param("comeLate")Double comeLate);

    List<Integer> getAbsenceRecord(@Param("username") String username, @Param("newDate") ArrayList<String> newDate);

    long getDay(String day);

    @Select("select id,username,punch_in_time,punch_out_time from attendance.record r where (punch_in_time between #{timeFrom} and #{timeTo}) and username = #{username} order by punch_in_time;")
    List<Record> getRecordByMonth(String username,LocalDate timeFrom, LocalDate timeTo);

    @Select("select * from attendance.record r where substring(to_char(punch_in_time, 'YYYY-MM-DD HH24:MI:SS'),0,8) = #{month} and username = #{username} order by punch_in_time;")
    List<Record> getRecordByMonthB(String username, String month);
}
