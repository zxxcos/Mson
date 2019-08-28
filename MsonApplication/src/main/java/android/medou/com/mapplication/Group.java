package android.medou.com.mapplication;

import com.medou.android.mson.annotations.JsonClass;
import com.medou.android.mson.annotations.JsonIgnore;

import java.util.List;
import java.util.Map;

@JsonClass
public class Group {
    public int _id;
    Student student;
    Klass klass;
    public Map<String, List<String>> lps;
    @JsonIgnore
    public Map<String, String> kpi;
    public Map<String, Klass> kps;
    public Map<String, TeacherType> ols;
}
