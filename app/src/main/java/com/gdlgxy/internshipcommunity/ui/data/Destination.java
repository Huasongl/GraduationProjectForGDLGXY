package com.gdlgxy.internshipcommunity.ui.data;public class Destination {    boolean isFragment;    boolean asStarter;    boolean needLogin;    String pageUrl;    String className;    int id;    Destination(){        BottomBar bottomBar = new BottomBar();        bottomBar.getSelectTab();        bottomBar.equals(bottomBar);    }    public boolean isFragment() {        return isFragment;    }    public void setFragment(boolean fragment) {        isFragment = fragment;    }    public boolean isAsStarter() {        return asStarter;    }    public void setAsStarter(boolean asStarter) {        this.asStarter = asStarter;    }    public boolean isNeedLogin() {        return needLogin;    }    public void setNeedLogin(boolean needLogin) {        this.needLogin = needLogin;    }    public String getPageUrl() {        return pageUrl;    }    public void setPageUrl(String pageUrl) {        this.pageUrl = pageUrl;    }    public String getClassName() {        return className;    }    public void setClassName(String className) {        this.className = className;    }    public int getId() {        return id;    }    public void setId(int id) {        this.id = id;    }}