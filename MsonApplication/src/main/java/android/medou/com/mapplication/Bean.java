package android.medou.com.mapplication;

import android.medou.com.mapplication.bean.Cat;
import android.medou.com.mapplication.bean.Name;

import com.medou.android.mson.annotations.JsonClass;
import com.medou.android.mson.annotations.JsonField;
import com.medou.android.mson.annotations.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-03-22.
 */
@JsonClass
public class Bean {

    @JsonIgnore
    public String goodname;
    @JsonField("stype")
    public int type;
    public Integer max;
    public String[] values;
    public List<String> dieas;
    public List horts;
    public Cat mpCat;
    public Cat[] cats;
    public Klass[] klasss;
    @JsonField("catListgggg")
    public List<Cat> catList;
    @JsonField("intImpKhgs")
    public ArrayList<Integer> intImp;
    public Student student;
    public Name cps;
    public ArrayList<List<Integer>> k6;

}
