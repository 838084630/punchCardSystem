package com.example.demo.controller;

import com.example.demo.config.Code;
import com.example.demo.config.Res;
import com.example.demo.pojo.Record;
import com.example.demo.pojo.User;
import com.example.demo.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sunql
 * @description controller层
 * @date 2020/12/29 15:30
 */
@RestController
@RequestMapping(value = "user")
public class UserController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserService userService;

    @PostMapping(value = "/login")
    public Res<?> login(@RequestBody User user){
        Res<?> response;
        if(StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())){
            response = Res.builder().code(Code.VALIDERROR_EXCEPTION).data(user).build();
        }else {
            response = userService.getUserInfo(user);
        }
        return response;
    }
    @PostMapping(value = "/userCheck")
    public Res<?> userCheck(@RequestBody User user){
        user.setToken(request.getHeader("token"));
        Res<?> response;
        if(StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getToken())){
            response = Res.builder().code(Code.NOLOGIN_EXCEPTION).data(user).build();
        }else {
            response = userService.userCheck(user);
        }
        return response;

    }

    @GetMapping(value = "/record")
    public Res<?> getRecord(String username, String date){
        Res<?> record = userService.getRecord(username, date);
        return record;
    }

    @PostMapping(value = "/punchCard")
    public Res<?> writeRecord(@RequestBody Record record){
        return userService.writeRecord(record);
    }

    @GetMapping(value = "/absence")
    public Res<?> getAbsenceRecord(String username, String time, Integer days){
        return userService.getAbsenceRecord(username,time,days);
    }

    @GetMapping(value = "/monthData")
    public Res<?> getRecordByMonth(String username, String month){
        return userService.getRecordByMonth(username,month);
    }

}