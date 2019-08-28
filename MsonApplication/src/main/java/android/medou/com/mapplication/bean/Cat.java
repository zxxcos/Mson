package android.medou.com.mapplication.bean;

import android.medou.com.mapplication.Student;
import android.medou.com.mapplication.TeacherType;
import android.view.View;

import com.medou.android.mson.annotations.JsonClass;
import com.medou.android.mson.annotations.JsonField;
import com.medou.android.mson.annotations.JsonIgnore;

import java.util.List;

/**
 * Created by Administrator on 2018-03-27.
 */
@JsonClass
public class Cat {

    @JsonField("name")
    public String name;
    public TeacherType typs;
    @JsonIgnore
    public List<TeacherType> types;
    @JsonField("test")
    public View.OnClickListener onClickListener;
    public Name cps;
    public Student student;

}
