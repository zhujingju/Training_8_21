package com.grasp.training.tool;

import java.io.Serializable;

public class DataStatus implements Serializable {
    private String if_left="";  //条件左边的显示
    private String if_right=""; //条件右边的显示
    private boolean if_add;    //条件是否能继续添加
    private int if_num=0;    //条件的类型
    private String num2_week="";  //条件 定时的重复 例子0000000
    private String num2_time="";  //条件 定时的时间 例子10:00

    private int num3_type=0;  //条件 室外天气的类型 1-4
    private int num3_num=0;  //条件 室外天气的类型的条件 1-2
    private int num3_num_var=0;  //条件 室外天气的类型的条件的值

    private String num4_name="";  //条件 智能设备的昵称
    private String num4_sid="";  //条件 智能设备的sid
    private String num4_type="";  //条件 智能设备的type
    private int num4_num;  //条件 智能设备的条件类型

    private String else_left="";  //结果 左边的显示
    private String else_right=""; //结果 右边的显示
    private int else_num=0;    //结果的类型

    private String else_num1_name="";  //结果 智能设备的昵称
    private String else_num1_sid="";  //结果 智能设备的sid
    private String else_num1_type="";  //结果 智能设备的type
    private int else_num1_num;  //结果 智能设备的条件类型

    private int else_num3_time=0;  //结果 延迟的时间


    public String getElse_left() {
        return else_left;
    }

    public void setElse_left(String else_left) {
        this.else_left = else_left;
    }

    public String getElse_right() {
        return else_right;
    }

    public void setElse_right(String else_right) {
        this.else_right = else_right;
    }

    public int getElse_num() {
        return else_num;
    }

    public void setElse_num(int else_num) {
        this.else_num = else_num;
    }

    public String getElse_num1_name() {
        return else_num1_name;
    }

    public void setElse_num1_name(String else_num1_name) {
        this.else_num1_name = else_num1_name;
    }

    public String getElse_num1_sid() {
        return else_num1_sid;
    }

    public void setElse_num1_sid(String else_num1_sid) {
        this.else_num1_sid = else_num1_sid;
    }

    public String getElse_num1_type() {
        return else_num1_type;
    }

    public void setElse_num1_type(String else_num1_type) {
        this.else_num1_type = else_num1_type;
    }

    public int getNum4_num() {
        return num4_num;
    }

    public void setNum4_num(int num4_num) {
        this.num4_num = num4_num;
    }

    public int getElse_num1_num() {
        return else_num1_num;
    }

    public void setElse_num1_num(int else_num1_num) {
        this.else_num1_num = else_num1_num;
    }

    public int getElse_num3_time() {
        return else_num3_time;
    }

    public void setElse_num3_time(int else_num3_time) {
        this.else_num3_time = else_num3_time;
    }

    public String getNum4_name() {
        return num4_name;
    }

    public void setNum4_name(String num4_name) {
        this.num4_name = num4_name;
    }

    public int getNum3_num_var() {
        return num3_num_var;
    }

    public void setNum3_num_var(int num3_num_var) {
        this.num3_num_var = num3_num_var;
    }

    public String getIf_left() {
        return if_left;
    }

    public void setIf_left(String if_left) {
        this.if_left = if_left;
    }

    public String getIf_right() {
        return if_right;
    }

    public void setIf_right(String if_right) {
        this.if_right = if_right;
    }

    public boolean isIf_add() {
        return if_add;
    }

    public void setIf_add(boolean if_add) {
        this.if_add = if_add;
    }

    public int getIf_num() {
        return if_num;
    }

    public void setIf_num(int if_num) {
        this.if_num = if_num;
    }

    public String getNum2_week() {
        return num2_week;
    }

    public void setNum2_week(String num2_week) {
        this.num2_week = num2_week;
    }

    public String getNum2_time() {
        return num2_time;
    }

    public void setNum2_time(String num2_time) {
        this.num2_time = num2_time;
    }

    public int getNum3_type() {
        return num3_type;
    }

    public void setNum3_type(int num3_type) {
        this.num3_type = num3_type;
    }

    public int getNum3_num() {
        return num3_num;
    }

    public void setNum3_num(int num3_num) {
        this.num3_num = num3_num;
    }

    public String getNum4_sid() {
        return num4_sid;
    }

    public void setNum4_sid(String num4_sid) {
        this.num4_sid = num4_sid;
    }

    public String getNum4_type() {
        return num4_type;
    }

    public void setNum4_type(String num4_type) {
        this.num4_type = num4_type;
    }


}
