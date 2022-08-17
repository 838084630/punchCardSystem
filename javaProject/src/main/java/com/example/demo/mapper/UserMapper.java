package com.example.demo.mapper;

import com.example.demo.pojo.Record;
import com.example.demo.pojo.User;
import org.apache.ibatis.annotations.*;

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


    long updateRecord(@Param("id") Integer id, @Param("punchInTime") LocalDateTime punchInTime, @Param("punchOutTime") LocalDateTime punchOutTime);

    long createRecord(@Param("username") String username, @Param("punchInTime") LocalDateTime in, @Param("punchOutTime") LocalDateTime out);

    List<Integer> getAbsenceRecord(@Param("username") String username, @Param("newDate") ArrayList<String> newDate);

    long getDay(String day);
}
