package com.grasp.training.tool;

public class Goods {
    private String name;//名称
    private String wz;//位置
    private boolean dy; //是否开启
    private boolean add_zt;
    private String Im_url;
    private String Sid;
    private String type;
    private boolean jh_zt;  //设备是否在线
    private String sk1=null;


    public String getSk1() {
        return sk1;
    }

    public void setSk1(String sk1) {
        this.sk1 = sk1;
    }


    public boolean isJh_zt() {
        return jh_zt;
    }

    public void setJh_zt(boolean jh_zt) {
        this.jh_zt = jh_zt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSid() {
        return Sid;
    }

    public void setSid(String sid) {
        Sid = sid;
    }

    public String getIm_url() {
        return Im_url;
    }

    public void setIm_url(String im_url) {
        Im_url = im_url;
    }

    public boolean isAdd_zt() {
        return add_zt;
    }

    public void setAdd_zt(boolean add_zt) {
        this.add_zt = add_zt;
    }

    public String getWz() {
        return wz;
    }

    public void setWz(String wz) {
        this.wz = wz;
    }

    public boolean isDy() {
        return dy;
    }

    public void setDy(boolean dy) {
        this.dy = dy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
