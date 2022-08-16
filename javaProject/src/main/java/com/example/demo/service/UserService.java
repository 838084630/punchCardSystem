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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

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
        if (StringUtils.isEmpty(record.getUsername())) {
            build = Res.builder().code(Code.NOLOGIN_EXCEPTION).data(null).build();
        }
        Map<String, LocalDateTime> format = timeFormat(record.getPunchInTime(), record.getPunchOutTime());
        if (record.getId() != null) {
            long result = userMapper.updateRecord(record.getId(), format.get("in"), format.get("out"));
            build = result == 1 ? Res.builder().code(Code.SUCCESS).data(record).build() : Res.builder().code(Code.SQLERR_EXCEPTION).data(null).build();
        } else {
            userMapper.createRecord(record.getUsername(), format.get("in"), format.get("out"));

        }
        return build;
    }

    private Map<String, LocalDateTime> timeFormat(LocalDateTime punchInTime, LocalDateTime punchOutTime) {
        HashMap<String, LocalDateTime> map = new HashMap<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (punchInTime != null) {
            map.put("in", LocalDateTime.parse(punchInTime.format(df), df));
        }
        if (punchOutTime != null) {
            map.put("out", LocalDateTime.parse(punchOutTime.format(df), df));
        }
        return map;
    }

}
