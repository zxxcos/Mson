package com.medou.android.mson.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2018-03-22.
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface JsonClass {
}
