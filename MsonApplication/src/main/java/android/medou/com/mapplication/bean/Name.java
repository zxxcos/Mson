package android.medou.com.mapplication.bean;

import com.medou.android.mson.annotations.JsonClass;

@JsonClass
public abstract class Name {
    String name;

    public String getName() {
        return name;
    }

    public abstract String getNames();
}
