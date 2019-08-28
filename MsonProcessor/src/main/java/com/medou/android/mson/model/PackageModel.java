package com.medou.android.mson.model;

/**
 * Created by Administrator on 2018-03-27.
 */

public class PackageModel {

    public String packageName;
    public String className;

    public PackageModel(String fullName, String className){
        this.className = className;
        this.packageName = fullName.substring(0, fullName.indexOf(className) - 1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";").append("\n");
        return sb.toString();
    }
}
