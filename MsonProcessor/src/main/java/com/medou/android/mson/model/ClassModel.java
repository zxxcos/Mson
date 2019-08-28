package com.medou.android.mson.model;

import com.medou.android.mson.Mson;

import javax.lang.model.element.ElementKind;

/**
 * Created by Administrator on 2018-03-27.
 */

public class ClassModel {

    public String modifier = "public";
    public String className;
    public String fullName;
    public ElementKind kind;

    public ClassModel setModifier(String modifier) {
        this.modifier = modifier;
        return this;
    }

    private String getClassName() {
        StringBuilder sb = new StringBuilder();
        sb.append(className).append(Mson.CLASSTARGET).append("<T extends ").append(className).append(">");
        return sb.toString();
    }

    public boolean isAbstract(){
        return modifier.contains("abstract");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
//        sb.append("@JsonClass\n");
        sb.append(modifier).append(" final class ").append(getClassName()).append("{\n");
        return sb.toString();
    }
}
