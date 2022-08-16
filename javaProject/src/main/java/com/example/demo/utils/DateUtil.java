package com.example.demo.utils;

import org.springframework.stereotype.Component;

@Component
public class DateUtil {

    public String newDate(String date){
        String month = "";
        String day = "";
        String[] split = date.split("-");
        month = split[1].length()==1?"0"+split[1]:split[1];
        day = split[2].length()==1?"0"+split[2]:split[2];
        //不优雅，以后改
        return split[0]+"-"+month+"-"+day;
    }
}
