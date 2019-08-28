package com.medou.android.mson.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018-03-27.
 */

public class ImportModel {

    /**
     * import com.medou.android.mson.stream.JsonReader;
     import com.medou.android.mson.stream.JsonToken;
     import com.medou.android.mson.stream.JsonWriter;
     import java.io.IOException;
     import java.io.StringReader;
     */

    public static String IMPORT_LIST = "java.util.List";
    public static String IMPORT_ARRAYLIST = "java.util.ArrayList";
    public static String IMPORT_LINKEDLIST = "java.util.LinkedList";
    public static String IMPORT_SET = "java.util.Set";
    public static String IMPORT_HASHSET = "java.util.HashSet";
    public static String IMPORT_MAP = "java.util.Map";
    public static String IMPORT_HASHMAP = "java.util.HashMap";
    public static String IMPORT_HASHTABLE = "java.util.Hashtable";
    public static String IMPORT_TREESET = "java.util.TreeSet";
    public static String IMPORT_QUEUE = "java.util.Queue";
    public static String IMPORT_PRIORITYQUEUE = "java.util.PriorityQueue";
    public static String IMPORT_SPARSEARRAY = "android.util.SparseArray";


    private static List<String> importLists;

    static {
        importLists = new ArrayList<>();
        importLists.add(IMPORT_LIST);
        importLists.add(IMPORT_ARRAYLIST);
        importLists.add(IMPORT_LINKEDLIST);
        importLists.add(IMPORT_SET);
        importLists.add(IMPORT_HASHSET);
        importLists.add(IMPORT_MAP);
        importLists.add(IMPORT_HASHMAP);
        importLists.add(IMPORT_HASHTABLE);
        importLists.add(IMPORT_TREESET);
        importLists.add(IMPORT_QUEUE);
        importLists.add(IMPORT_PRIORITYQUEUE);
        importLists.add(IMPORT_SPARSEARRAY);
    }

    private Set<String> imports;

    public ImportModel(){
        imports = new HashSet<>();
//        imports.add("com.medou.android.mson.annotations.JsonClass");
        imports.add("com.medou.android.mson.stream.JsonReader");
        imports.add("com.medou.android.mson.stream.JsonToken");
        imports.add("com.medou.android.mson.stream.JsonWriter");
    }

    public void modifyImportUtil(String fieldModel, String pack){
        if(fieldModel.startsWith("java.lang")){
            return;
        }
        for (String imp : importLists){
            if(fieldModel.contains(imp)){
                addImport(imp);
                return;
            }
        }
        //if diff package
        if(!pack.equals(fieldModel.substring(0, fieldModel.lastIndexOf(".")))){
            addImport(fieldModel);
            return;
        }
    }

    public void modifyImportAnnotations(List<FieldModel> annotationsList, FieldModel model){
        for (int i = annotationsList.size() - 1; i >= 0; i--) {
            FieldModel annotation = annotationsList.get(i);
            if(model.className.equals(annotation.className) && model.name.equals(annotation.name)){
                addImport(annotation.annotation);
                model.annotation = annotation.annotation;
                model.ignor = annotation.ignor;
                model.value = annotation.value;
                annotationsList.remove(i);
            }
        }
    }

    public void addImport(String string){
        imports.add(string);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String imp : imports){
            sb.append("import ").append(imp).append(";\n");
        }
        return sb.toString();
    }
}
