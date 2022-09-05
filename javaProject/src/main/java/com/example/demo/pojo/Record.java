package com.example.demo.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class Record {
    private Integer id;
    private String username;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime punchInTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime punchOutTime;

    private Double comeLate;

    private Double leaveEarly;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getPunchInTime() {
        return punchInTime;
    }

    public void setPunchInTime(LocalDateTime punchInTime) {
        this.punchInTime = punchInTime;
    }

    public LocalDateTime getPunchOutTime() {
        return punchOutTime;
    }

    public void setPunchOutTime(LocalDateTime punchOutTime) {
        this.punchOutTime = punchOutTime;
    }

    public Double getComeLate() {
        return comeLate;
    }

    public void setComeLate(Double comeLate) {
        this.comeLate = comeLate;
    }

    public Double getLeaveEarly() {
        return leaveEarly;
    }

    public void setLeaveEarly(Double leaveEarly) {
        this.leaveEarly = leaveEarly;
    }
}
