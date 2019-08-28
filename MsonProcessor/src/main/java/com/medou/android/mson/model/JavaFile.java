package com.medou.android.mson.model;

import com.medou.android.mson.Mson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-03-27.
 */

public class JavaFile {

    public boolean enable = true;
    public PackageModel packageModel;
    public ImportModel importModel;
    public ClassModel classModel;
    public List<FieldModel> fieldModelList;

    public void setPackageModel(PackageModel packageModel) {
        this.packageModel = packageModel;
    }

    public void setImportModel(ImportModel importModel) {
        this.importModel = importModel;
    }

    public void setClassModel(ClassModel classModel) {
        this.classModel = classModel;
    }

    public void addFieldModelList(FieldModel fieldModel) {
        if (fieldModelList == null) {
            fieldModelList = new ArrayList<>();
        }
        this.fieldModelList.add(fieldModel);
    }

    public List<FieldModel> getFieldModelList() {
        return fieldModelList;
    }

    /**
     * @return
     */
    public String toMsonString() {
        if(!enable){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(packageModel.toString()).append("\n")
                .append(importModel.toString()).append("\n")
                .append(classModel.toString()).append("\n");

//        for (FieldModel model : fieldModelList) {
//            if(model.enable){
//                sb.append(model.toString());
//            }
//        }
        sb.append("\n");
        //add toJson
        sb.append(FieldModel.FORMATE).append("public String " + Mson.TOJSON + "(" + classModel.className + " target) {\n");
        sb.append(FieldModel.FORMATE2).append("java.io.StringWriter writer = new java.io.StringWriter();\n");
        sb.append(FieldModel.FORMATE2).append("JsonWriter jsonWriter = new JsonWriter(writer);\n");
        sb.append(FieldModel.FORMATE2).append("try{\n");
        sb.append(FieldModel.FORMATE3).append("jsonWriter.setIndent(\"  \");\n");
        sb.append(FieldModel.FORMATE3).append("jsonWriter.beginObject();\n");
        for (FieldModel model : fieldModelList) {
            if(model.enable && !model.ignor){
                sb.append(model.toJsonString());
            }
        }
        sb.append(FieldModel.FORMATE3).append("jsonWriter.endObject();\n");
        sb.append(FieldModel.FORMATE3).append("jsonWriter.flush();\n");
        sb.append(FieldModel.FORMATE3).append("jsonWriter.close();\n");
        sb.append(FieldModel.FORMATE2).append("} catch(java.io.IOException e){\n");
        sb.append(FieldModel.FORMATE3).append("return null;\n");
        sb.append(FieldModel.FORMATE2).append("}\n");
        sb.append(FieldModel.FORMATE2).append("return writer.toString();\n");
        sb.append(FieldModel.FORMATE).append("}\n\n");
        //add fromJson
        sb.append(FieldModel.FORMATE).append("public " + classModel.className + " " + Mson.FROMJSON + "(String json) {\n");
        sb.append(FieldModel.FORMATE2).append("java.io.StringReader stringReader = new java.io.StringReader(json);\n");
        sb.append(FieldModel.FORMATE2).append("JsonReader reader = new JsonReader(stringReader);\n");
        sb.append(FieldModel.FORMATE2).append(classModel.className + " object = new " + classModel.className + "();\n");
        sb.append(FieldModel.FORMATE2).append("try{\n");
        sb.append(FieldModel.FORMATE3).append("reader.beginObject();\n");
        sb.append(FieldModel.FORMATE3).append("while (reader.hasNext()) {\n");
        sb.append(FieldModel.FORMATE4).append("String field = reader.nextName();\n");
        int index = 0;
        for (FieldModel model : fieldModelList) {
            if(model.enable && !model.ignor){
                sb.append(model.fromJsonString(index));
                index ++;
            }
        }
        sb.append(" else {\n");
        sb.append(FieldModel.FORMATE5).append("reader.skipValue();\n");
        sb.append(FieldModel.FORMATE4).append("}\n");
        sb.append(FieldModel.FORMATE3).append("}\n");
        sb.append(FieldModel.FORMATE3).append("reader.endObject();\n");
        sb.append(FieldModel.FORMATE3).append("reader.close();\n");
        sb.append(FieldModel.FORMATE2).append("} catch(java.io.IOException e){\n");
        sb.append(FieldModel.FORMATE3).append("return null;\n");
        sb.append(FieldModel.FORMATE2).append("}\n");
        sb.append(FieldModel.FORMATE2).append("return object;\n");
        sb.append(FieldModel.FORMATE).append("}\n\n");
        //add fromJson(JsonReader reader)
        sb.append(FieldModel.FORMATE).append("public static " + classModel.className + " " + Mson.FROMJSON + "(JsonReader reader) {\n");
        sb.append(FieldModel.FORMATE2).append(classModel.className + " object = new " + classModel.className + "();\n");
        sb.append(FieldModel.FORMATE2).append("try{\n");
        sb.append(FieldModel.FORMATE3).append("reader.beginObject();\n");
        sb.append(FieldModel.FORMATE3).append("while (reader.hasNext()) {\n");
        sb.append(FieldModel.FORMATE4).append("String field = reader.nextName();\n");
        int index2 = 0;
        for (FieldModel model : fieldModelList) {
            if(model.enable && !model.ignor){
                sb.append(model.fromJsonString(index2));
                index2 ++;
            }
        }
        sb.append(" else {\n");
        sb.append(FieldModel.FORMATE5).append("reader.skipValue();\n");
        sb.append(FieldModel.FORMATE4).append("}\n");
        sb.append(FieldModel.FORMATE3).append("}\n");
        sb.append(FieldModel.FORMATE3).append("reader.endObject();\n");
        sb.append(FieldModel.FORMATE2).append("} catch(java.io.IOException e){\n");
        sb.append(FieldModel.FORMATE3).append("return null;\n");
        sb.append(FieldModel.FORMATE2).append("}\n");
        sb.append(FieldModel.FORMATE2).append("return object;\n");
        sb.append(FieldModel.FORMATE).append("}\n");

        sb.append("\n}");
        return sb.toString();
    }

}
