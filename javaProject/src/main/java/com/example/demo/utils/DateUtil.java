package com.example.demo.utils;

import org.springframework.stereotype.Component;

@Component
public class DateUtil {

    public String newDate(String date){
        String newTime = "";
        String month = "";
        String day = "";
        String[] split = date.split("-");
        month = split[1].length()==1?"0"+split[1]:split[1];
        if (split.length==3){
            day = split[2].length()==1?"0"+split[2]:split[2];
            newTime = split[0]+"-"+month+"-"+day;
        }else {
            newTime = split[0]+"-"+month;
        }

        return newTime;
    }
}
