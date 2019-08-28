package com.medou.android.mson.model;

import com.medou.android.mson.Mson;
import com.medou.android.mson.annotations.JsonField;

import java.text.MessageFormat;

import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeKind;

/**
 * Created by Administrator on 2018-03-27.
 */

public class FieldModel {

    public static String FORMATE = "    ";
    public static String FORMATE2 = "        ";
    public static String FORMATE3 = "            ";
    public static String FORMATE4 = "                ";
    public static String FORMATE5 = "                    ";
    public static String FORMATE6 = "                        ";
    public static String FORMATE7 = "                            ";
    public static String FORMATE8 = "                                ";
    public static final String[] BASEINFO = {"boolean", "char", "short", "int", "byte", "long", "float", "double"};
    public static final String[] BASEOBJECT = {"java.lang.Boolean", "java.lang.Character", "java.lang.Short",
            "java.lang.Long", "java.lang.Integer", "java.lang.Byte","java.lang.Float", "java.lang.Double"
            , "java.lang.String", "String"};

    public String getKlass(String type){
        if(type.contains("<") && type.contains(">")){
            type = type.substring(type.indexOf("<") + 1, type.lastIndexOf(">"));
            return getKlass(type);
        }
        return type;
    }

    public boolean checkCollection(String cls){
        try {
            Class clz = Class.forName(cls);
            Class[] interfaces = clz.getInterfaces();
            if(interfaces != null){
                for (Class imp : interfaces){
                    if(imp.getName().equals("java.util.Collection") || imp.getName().equals("java.util.Map")){
                        return true;
                    }
                }
            }
            if(clz.getSuperclass() != null){
                return checkCollection(clz.getSuperclass().getName());
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     *
     */
    public void checkField(ClassModel classModel){
        if(modifier.contains("transient") || modifier.contains("static")){
            enable = false;
        }
        String klass = getKlass(type);
        if(checkCollection(klass)){
            enable = false;
        }
        if(type.contains(classModel.fullName)){
            if(classModel.isAbstract()){
                enable = false;
            }
            if(classModel.kind == ElementKind.ENUM){
                isEnum = true;
            }
        }
    }

    public boolean isMson = false;
    public String annotation;
    public String modifier;
    public TypeKind typeKind;
    public String className;
    public String type;
    public String name;
    public boolean ignor; //JsonIgnore
    public String value;//JsonField ->value()
    public boolean enable = true;
    public boolean isEnum = false;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (annotation != null && !"".equals(annotation)) {
            String smplie = annotation.substring(annotation.lastIndexOf(".") + 1);
            if (JsonField.class.getSimpleName().contains(smplie)) {
                sb.append(FORMATE).append("@").append(smplie).append("(\"").append(value).append("\")\n");
            } else {
                sb.append(FORMATE).append("@").append(smplie).append("\n");
            }
        }
        sb.append(FORMATE);
        if(modifier != null && !modifier.isEmpty()){
            sb.append(modifier).append(" ");
        }
        sb.append(type).append(" ").append(name).append(";\n");
        return sb.toString();
    }

    public String toInfoString() {
        return "FieldModel{" +
                "typeKind=" + typeKind +
                ", className='" + className + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", ignor=" + ignor +
                ", value='" + value + '\'' +
                ", enable='" + enable + '\'' +
                ", isMson=" + isMson + '}';
    }

    public String getName() {
        if(value != null && !value.isEmpty()){
            return value;
        }
        return name;
    }

    public String toJsonString() {
        String name = "target." + this.name;
        if (typeKind == TypeKind.DECLARED) {
            return toJsonDeclaredString();
        } else if (typeKind == TypeKind.ARRAY) {
            StringBuilder sb = new StringBuilder();
            sb.append(FORMATE3).append("if(").append(name).append(" != null){\n");
            sb.append(FORMATE4).append(MessageFormat.format("jsonWriter.name(\"{0}\");\n", getName()));
            String typeF = type.substring(0, type.lastIndexOf("[]"));
            toAppendForeach(sb, typeF, name, 0);
            sb.append(FORMATE3).append("}\n");
            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(FORMATE3).append("jsonWriter.name(\"").append(getName()).append("\").value(").append(name).append(");\n");
            return sb.toString();
        }
    }

    private StringBuilder fromatAppend(StringBuilder sb, int index){
        for (int i = 0; i < index; i++) {
            sb.append(FORMATE);
        }
        return sb;
    }

    public boolean checkMap(String cls){
        try {
            cls = cls.trim();
            if(cls.equals("java.util.Map")){
                return true;
            }
            Class clz = Class.forName(cls);
            Class[] interfaces = clz.getInterfaces();
            if(interfaces != null){
                for (Class imp : interfaces){
                    if(imp.getName().equals("java.util.Map")){
                        return true;
                    }
                }
            }
            if(clz.getSuperclass() != null){
                return checkMap(clz.getSuperclass().getName());
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public boolean checkList(String cls){
        cls = cls.trim();
        try {
            if(cls.equals("java.util.Collection")){
                return true;
            }
            Class clz = Class.forName(cls);
            Class[] interfaces = clz.getInterfaces();
            if(interfaces != null){
                for (Class imp : interfaces){
                    if(imp.getName().equals("java.util.Collection")){
                        return true;
                    }
                }
            }
            if(clz.getSuperclass() != null){
                return checkList(clz.getSuperclass().getName());
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private void toListAppendNullValue(StringBuilder sb, String condition, int index){
        fromatAppend(sb, index).append(FORMATE4).append("if(").append(condition).append(" == null){\n");
        fromatAppend(sb, index).append(FORMATE5).append("jsonWriter.nullValue();\n");
        fromatAppend(sb, index).append(FORMATE5).append("continue;\n");
        fromatAppend(sb, index).append(FORMATE4).append("}\n");
    }

    private void toMapAppendNullValue(StringBuilder sb, String condition, int index){
        fromatAppend(sb, index).append(FORMATE4).append("if(").append(condition).append(" == null){\n");
        fromatAppend(sb, index).append(FORMATE5).append("jsonWriter.nullValue();\n");
        fromatAppend(sb, index).append(FORMATE4).append("} else {\n");
    }

    private String toAppendCollection(String type, int index, String name, String value) {
        type = type.replace(" ", "");
        StringBuilder sb = new StringBuilder();
        if (type.contains("<") && type.contains(">")) {
            String clz = type.substring(0, type.indexOf("<"));
            if(checkList(clz)){
                if(name != null){
                    fromatAppend(sb, index).append(FORMATE4).append(MessageFormat.format("jsonWriter.name({0});\n", name));
                }
                fromatAppend(sb, index).append(FORMATE4).append("jsonWriter.beginArray();\n");
                type = type.substring(type.indexOf("<") + 1, type.lastIndexOf(">"));
                fromatAppend(sb, index).append(FORMATE4).append("for (").append(type).append(" item" + index + " : ").append(value).append(") {\n");
                toListAppendNullValue(sb, "item" + index, index + 1);
                sb.append(toAppendCollection(type, index + 1, null, "item" + index));
                fromatAppend(sb, index).append(FORMATE4).append("}\n");
                fromatAppend(sb, index).append(FORMATE4).append("jsonWriter.endArray();\n");
            } else if(checkMap(clz)){
                if(name != null){
                    fromatAppend(sb, index).append(FORMATE4).append(MessageFormat.format("jsonWriter.name({0});\n", name));
                }
                String condition = value + ".keySet()";
                if(index == 0){
                    toMapAppendNullValue(sb, condition, index);
                } else {
                    toListAppendNullValue(sb, condition, index + 1);
                }
                fromatAppend(sb, index).append(FORMATE5).append("jsonWriter.beginObject();\n");
                type = type.substring(type.indexOf("<") + 1, type.lastIndexOf(">"));
                String[] split = new String[2];
                split[0] = type.substring(0, type.indexOf(","));
                split[1] = type.substring(type.indexOf(",") + 1, type.length());
                type = split[1].trim();
                fromatAppend(sb, index).append(FORMATE5).append("for (").append(split[0].trim()).append(" item" + index + " : ").append(value).append(".keySet()) {\n");
                sb.append(toAppendCollection(type, index + 1, "item" + index, value + ".get(item" + index + ")"));
                fromatAppend(sb, index).append(FORMATE5).append("}\n");
                fromatAppend(sb, index).append(FORMATE5).append("jsonWriter.endObject();\n");
                if(index == 0){
                    fromatAppend(sb, index).append(FORMATE4).append("}\n");
                }
            }
        } else {
            if(name != null){
                toListAppendNullValue(sb, value, index + 1);
                if(isMson){
                    toAppendMsonClass(sb, index + 5, type, value, true);
//                    fromatAppend(sb, index).append(FORMATE5).append("jsonWriter.name(").append(name).append(".toString()).jsonValue(").append(value).append(".toJsonString()").append(");\n");
                } else if(isEnum){
                    fromatAppend(sb, index).append(FORMATE5).append("jsonWriter.name(").append(name).append(".toString()).value(").append(value).append(".toString());\n");
                } else {
                    fromatAppend(sb, index).append(FORMATE5).append("jsonWriter.name(").append(name).append(".toString()).value(").append(value).append(");\n");
                }
            } else {
                if(isMson){
                    toAppendMsonClass(sb, index + 5, type, value, false);
//                    fromatAppend(sb, index).append(FORMATE4).append("jsonWriter.jsonValue(").append(value).append(".toJsonString()").append(");\n");
                } else if(isEnum){
                    fromatAppend(sb, index).append(FORMATE4).append("jsonWriter.value(" + value + ".toString());\n");
                } else {
                    fromatAppend(sb, index).append(FORMATE4).append("jsonWriter.value(" + value + ");\n");
                }
            }
        }
        return sb.toString();
    }

    private boolean checkBaseInfo(String type){
        for (String item : BASEINFO) {
            if(item.equals(type)){
                return true;
            }
        }
        for (String item : BASEOBJECT) {
            if(item.equals(type)){
                return true;
            }
        }
        return false;
    }

    private void toAppendMsonClass(StringBuilder sb, int formate, String type, String name, boolean flag){
        if(type.contains("<") && type.contains(">")){
            type = type.substring(0, type.indexOf("<"));
        }
        if(type.contains("[]")){
            type = type.substring(0, type.indexOf("[]"));
        }
        //String json = new Cat$$Mson().toJsonString(target.mpCat);
        fromatAppend(sb, formate).append("String json = new ").append(type).append(Mson.CLASSTARGET).append("().").append(Mson.TOJSON).append("(").append(name).append(");\n");
        if(flag){
            fromatAppend(sb, formate).append("jsonWriter.name(\"").append(getName()).append("\").jsonValue(json);\n");
        } else {
            fromatAppend(sb, formate).append("jsonWriter.jsonValue(json);\n");
        }
    }

    /**
     *
     * @return
     */
    private String toJsonDeclaredString() {
        String name = "target." + this.name;
        StringBuilder sb = new StringBuilder();
        sb.append(FORMATE3).append("if(").append(name).append(" != null){\n");
        if (type.contains("<") && type.contains(">")) {
            sb.append(FORMATE4).append(MessageFormat.format("jsonWriter.name(\"{0}\");\n", getName()));
            sb.append(toAppendCollection(type, 0, null, name));
        } else {
            if(isMson){
                toAppendMsonClass(sb, 4, type, name, true);
//                sb.append(FORMATE4).append("jsonWriter.name(\"").append(getName()).append("\").jsonValue(").append(name).append(Mson.TOJSON_).append(");\n");
            } else if(isEnum){
                sb.append(FORMATE4).append("jsonWriter.name(\"").append(getName()).append("\").value(").append(name).append(".toString());\n");
            } else {
                if(checkBaseInfo(type)){
                    sb.append(FORMATE4).append("jsonWriter.name(\"").append(getName()).append("\").value(").append(name).append(");\n");
                }
            }
        }
        sb.append(FORMATE3).append("}\n");
        return sb.toString();
    }

    private void toArrayAppendNullValue(StringBuilder sb, String name, int index){
        fromatAppend(sb, index).append(FORMATE5).append("if(").append(name).append("[index").append(index).append("] == null){\n");
        fromatAppend(sb, index).append(FORMATE6).append("jsonWriter.nullValue();\n");
        fromatAppend(sb, index).append(FORMATE6).append("continue;\n");
        fromatAppend(sb, index).append(FORMATE5).append("}\n");
    }

    private boolean checkArrayType(String type){
        if(type.contains("[]")){
            type = type.substring(0, type.indexOf("[]"));
        }
        for (String item : BASEINFO) {
            if(item.equals(type)){
                return true;
            }
        }
        return false;
    }

    private void toAppendForeach(StringBuilder sb, String type, String name, int index){
        boolean flag = false;
        fromatAppend(sb, index).append(FORMATE4).append("jsonWriter.beginArray();\n");
        fromatAppend(sb, index).append(FORMATE4).append("for (int index").append(index).append(" = 0; index").append(index).append(" < ").append(name).append(".length; index").append(index).append("++) {\n");
        if(type.contains("[]")){
            toArrayAppendNullValue(sb, name, index);
            type = type.substring(0, type.lastIndexOf("[]"));
            name = name + "[index" + index + "]";
            index ++;
            flag = true;
            toAppendForeach(sb, type, name, index);
        } else {
            if(!checkArrayType(type)){
                toArrayAppendNullValue(sb, name, index);
            }
            if(isMson){
                toAppendMsonClass(sb, index + 5, type, name + "[index" + index + "]", false);
//                fromatAppend(sb, index).append(FORMATE5).append(MessageFormat.format("jsonWriter.jsonValue({0}[{1}]" + Mson.TOJSON_ + ");\n", name, "index" + index));
            } else if(isEnum){
                fromatAppend(sb, index).append(FORMATE5).append(MessageFormat.format("jsonWriter.value({0}[{1}].toString());\n", name, "index" + index));
            } else {
                fromatAppend(sb, index).append(FORMATE5).append(MessageFormat.format("jsonWriter.value({0}[{1}]);\n", name, "index" + index));
            }
        }
        if(flag){
            fromatAppend(sb, index - 1).append(FORMATE4).append("}\n");
            fromatAppend(sb, index - 1).append(FORMATE4).append("jsonWriter.endArray();\n");
        } else {
            fromatAppend(sb, index).append(FORMATE4).append("}\n");
            fromatAppend(sb, index).append(FORMATE4).append("jsonWriter.endArray();\n");
        }
    }

    /**************************************************************************************************************/
    public String fromJsonString(int index) {
        StringBuilder sb = new StringBuilder();
        if(index == 0){
            sb.append(FORMATE4).append("if(\"").append(getName()).append("\".equals(field)) {\n");
        } else {
            sb.append(" else if(\"").append(getName()).append("\".equals(field)) {\n");
        }
        if (typeKind == TypeKind.BOOLEAN || "java.lang.Boolean".equals(type)) {
            sb.append(FORMATE5).append("object.").append(name).append(" = reader.nextBoolean();\n");
        } else if (typeKind == TypeKind.BYTE || "java.lang.Byte".equals(type)) {
            sb.append(FORMATE5).append("object.").append(name).append(" = (byte)reader.nextInt();\n");
        } else if (typeKind == TypeKind.SHORT || "java.lang.Short".equals(type)) {
            sb.append(FORMATE5).append("object.").append(name).append(" = (short)reader.nextInt();\n");
        } else if (typeKind == TypeKind.INT || "java.lang.Integer".equals(type)) {
            sb.append(FORMATE5).append("object.").append(name).append(" = reader.nextInt();\n");
        } else if (typeKind == TypeKind.LONG || "java.lang.Long".equals(type)) {
            sb.append(FORMATE5).append("object.").append(name).append(" = reader.nextLong();\n");
        } else if (typeKind == TypeKind.CHAR || "java.lang.Character".equals(type)) {
            sb.append(FORMATE5).append("object.").append(name).append(" = reader.nextString().charAt(0);\n");
        } else if (typeKind == TypeKind.FLOAT || "java.lang.Float".equals(type)) {
            sb.append(FORMATE5).append("object.").append(name).append(" = (float)reader.nextDouble();\n");
        } else if (typeKind == TypeKind.DOUBLE || "java.lang.Double".equals(type)) {
            sb.append(FORMATE5).append("object.").append(name).append(" = reader.nextDouble();\n");
        } else if(typeKind == TypeKind.ARRAY){
            String klass = type.substring(0, type.indexOf("[]"));;
            sb.append(FORMATE5).append("if (reader.peek() == JsonToken.NULL) {\n");
            sb.append(FORMATE6).append("reader.nextNull();\n");
            sb.append(FORMATE6).append("object.").append(name).append(" = null;\n");
            sb.append(FORMATE5).append("} else {\n");
            int lenght = getArraysLeng(type);
            fromAppendArrayList(sb, type, klass, lenght);
            fromAppendForeach(sb, type, klass, lenght, "list" + lenght);
            sb.append(FORMATE6).append("object.").append(name).append(" = array").append(lenght).append(";\n");
            sb.append(FORMATE5).append("}\n");
        } else if(typeKind == TypeKind.DECLARED){
            if("java.lang.String".equals(type)){
                sb.append(FORMATE5).append("if (reader.peek() == JsonToken.NULL) {\n");
                sb.append(FORMATE6).append("reader.nextNull();\n");
                sb.append(FORMATE6).append("object.").append(name).append(" = null;\n");
                sb.append(FORMATE5).append("} else {\n");
                sb.append(FORMATE6).append("object.").append(name).append(" = reader.nextString();\n");
                sb.append(FORMATE5).append("}\n");
            } else if (type.contains("<") && type.contains(">")) {
                sb.append(FORMATE5).append("if (reader.peek() == JsonToken.NULL) {\n");
                sb.append(FORMATE6).append("reader.nextNull();\n");
                sb.append(FORMATE6).append("object.").append(name).append(" = null;\n");
                sb.append(FORMATE5).append("} else {\n");
                fromAppendCollection(sb, type, name, 0);
                sb.append(FORMATE6).append("object.").append(name).append(" = list0").append(";\n");
                sb.append(FORMATE5).append("}\n");
            } else if(isEnum){
                sb.append(FORMATE5).append("if (reader.peek() == JsonToken.NULL) {\n");
                sb.append(FORMATE6).append("reader.nextNull();\n");
                sb.append(FORMATE6).append("object.").append(name).append(" = null;\n");
                sb.append(FORMATE5).append("} else {\n");
                sb.append(FORMATE6).append("object.").append(name).append(" = ").append(type).append(".valueOf(reader.nextString());\n");
                sb.append(FORMATE5).append("}\n");
            } else if(isMson){
                sb.append(FORMATE5).append("if (reader.peek() == JsonToken.NULL) {\n");
                sb.append(FORMATE6).append("reader.nextNull();\n");
                sb.append(FORMATE6).append("object.").append(name).append(" = null;\n");
                sb.append(FORMATE5).append("} else {\n");
                sb.append(FORMATE6).append("object.").append(name).append(" = ").append(type).append(Mson.CLASSTARGET).append(".").append(Mson.FROMJSON).append("(reader);\n");
                sb.append(FORMATE5).append("}\n");
            }
        }
        sb.append(FORMATE4).append("}");
        return sb.toString();
    }

    public int getArraysLeng(String type){
        int sum = 0;
        if(type.contains("[]")){
            type = type.substring(0, type.lastIndexOf("[]"));
            sum ++;
            return sum += getArraysLeng(type);
        } else {
            return 0;
        }
    }

    private void fromAppendArrayList(StringBuilder sb, int index, String klass, int lenght){
        fromatAppend(sb, lenght - index).append(FORMATE6);
        for (int i = 0; i < index; i++) {
            sb.append("ArrayList<");
        }
        sb.append(klass);
        for (int i = 0; i < index; i++) {
            sb.append(">");
        }
        sb.append(" list").append(index).append(" = new ");
        for (int i = 0; i < index; i++) {
            sb.append("ArrayList<");
        }
        sb.append(klass);
        for (int i = 0; i < index; i++) {
            sb.append(">");
        }
        sb.append("();\n");
    }

    private String[] getArrayListKlass(String klass){
        String[] klassNext = new String[3];
        if(klass.equals("byte") || klass.equals("java.lang.Byte")){
            klassNext[0] = "Byte";
            klassNext[1] = "byte";
            klassNext[2] = "nextInt()";
        } else if(klass.equals("boolean") || klass.equals("java.lang.Boolean")){
            klassNext[0] = "Boolean";
            klassNext[1] = "boolean";
            klassNext[2] = "nextBoolean()";
        } else if(klass.equals("char") || klass.equals("java.lang.Character")){
            klassNext[0] = "Character";
            klassNext[1] = "Character";
            klassNext[2] = "nextString().charAt(0)";
        } else if(klass.equals("int") || klass.equals("java.lang.Integer")){
            klassNext[0] = "Integer";
            klassNext[1] = "Integer";
            klassNext[2] = "nextInt()";
        } else if(klass.equals("short") || klass.equals("java.lang.Short")){
            klassNext[0] = "Short";
            klassNext[1] = "short";
            klassNext[2] = "nextInt()";
        } else if(klass.equals("long") || klass.equals("java.lang.Long")){
            klassNext[0] = "Long";
            klassNext[1] = "long";
            klassNext[2] = "nextLong()";
        } else if(klass.equals("float") || klass.equals("java.lang.Float")){
            klassNext[0] = "Float";
            klassNext[1] = "float";
            klassNext[2] = "nextDouble()";
        } else if(klass.equals("double") || klass.equals("java.lang.Double")){
            klassNext[0] = "Double";
            klassNext[1] = "double";
            klassNext[2] = "nextDouble()";
        } else if(klass.equals("String") || klass.equals("java.lang.String")){
            klassNext[0] = "String";
            klassNext[1] = "String";
            klassNext[2] = "nextString()";
        } else if(isEnum){
            klassNext[0] = klass;
            klassNext[1] = klass;
            klassNext[2] = "nextString()";
        } else if(isMson){
            klassNext[0] = klass;
            klassNext[1] = klass;
            klassNext[2] = Mson.FROMJSON;
        }
        return klassNext;
    }

    private void fromAppendArrayList(StringBuilder sb, String type, String klass, int lenght){
        String[] kNext = getArrayListKlass(klass);
        int index = getArraysLeng(type);
        fromAppendArrayList(sb, index, kNext[0], lenght);
        fromatAppend(sb, lenght - index).append(FORMATE6).append("reader.beginArray();\n");
        fromatAppend(sb, lenght - index).append(FORMATE6).append("while (reader.hasNext()) {\n");
        fromatAppend(sb, lenght - index).append(FORMATE7).append("if (reader.peek() == JsonToken.NULL) {\n");
        fromatAppend(sb, lenght - index).append(FORMATE8).append("reader.nextNull();\n");
        fromatAppend(sb, lenght - index).append(FORMATE8).append("continue;\n");
        fromatAppend(sb, lenght - index).append(FORMATE7).append("}\n");
        type = type.substring(0, type.lastIndexOf("[]"));
        if(type.contains("[]")){
            fromAppendArrayList(sb, type, klass, lenght);
        } else {
            if(isEnum){
                fromatAppend(sb, lenght - index).append(FORMATE7).append("list").append(index).append(".add(").append(kNext[1]).append(".valueOf(reader.").append(kNext[2]).append("));\n");
            } else if(isMson){
                fromatAppend(sb, lenght - index).append(FORMATE7).append("list").append(index).append(".add(").append(kNext[1]).append(Mson.CLASSTARGET).append(".").append(kNext[2]).append("(reader));\n");
            } else {
                fromatAppend(sb, lenght - index).append(FORMATE7).append("list").append(index).append(".add((").append(kNext[1]).append(")reader.").append(kNext[2]).append(");\n");
            }
        }
        fromatAppend(sb, lenght - index).append(FORMATE6).append("}\n");
        fromatAppend(sb, lenght - index).append(FORMATE6).append("reader.endArray();\n");
        if(index < lenght){
            fromatAppend(sb, lenght - index).append(FORMATE6).append("list").append(index + 1).append(".add(list").append(index).append(");\n");
        }
    }

    private void fromAppendArray(StringBuilder sb, int index, String klass, int lenght, String list){
        fromatAppend(sb, lenght - index).append(FORMATE6).append(klass);
        for (int i = 0; i < index; i++) {
            sb.append("[]");
        }
        sb.append(" array").append(index).append(" = new ").append(klass);
        for (int i = 0; i < index; i++) {
            if(i == 0){
                sb.append("[").append(list).append(".size()]");
            } else {
                sb.append("[]");
            }
        }
        sb.append(";\n");
    }

    private void fromAppendForeach(StringBuilder sb, String type, String klass, int lenght, String list){
        int index = getArraysLeng(type);
        fromAppendArray(sb, index, klass, lenght, list);
//		for (int index0 = 0; index0 < list3.size(); index0++) {
//		for (int index1 = 0; index1 < list3.get(index0).size(); index1++) {
        fromatAppend(sb, lenght - index).append(FORMATE6).append("for (int index").append(lenght - index).append(" = 0; index").append(lenght - index).append(" < ");
        sb.append(list).append(".size(); ").append("index").append(lenght - index).append("++ ){\n");
        type = type.substring(0, type.lastIndexOf("[]"));
        if(type.contains("[]")){
            list += ".get(index" + (lenght - index) + ")";
            fromAppendForeach(sb, type, klass, lenght, list);
        } else {
            //array3[index2] = list3.get(index0).get(index1).get(index2);
            fromatAppend(sb, lenght - index).append(FORMATE7).append("array").append(index).append("[index").append(lenght - index);
            sb.append("] = ").append(list).append(".get(index").append(lenght - index).append(");\n");
        }
        fromatAppend(sb, lenght - index).append(FORMATE6).append("}\n");
        //array2[index1] = array1;
        if(index < lenght){
            fromatAppend(sb, lenght - index).append(FORMATE6).append("array").append(index + 1).append("[index").append(lenght - index - 1).append("] = array").append(index).append(";\n");
        }
    }

    private void appendMapByKey(StringBuilder sb, String key, int index){
        String[] klassNext = new String[2];
        if (key.equals("byte") || key.equals("java.lang.Byte")) {
            klassNext[0] = "Byte";
            klassNext[1] = "Byte.parseByte(reader.nextName())";
        } else if (key.equals("boolean") || key.equals("java.lang.Boolean")) {
            klassNext[0] = "Boolean";
            klassNext[1] = "Boolean.parseBoolean(reader.nextName())";
        } else if (key.equals("char") || key.equals("java.lang.Character")) {
            klassNext[0] = "Character";
            klassNext[1] = "nextString().charAt(0)";
        } else if (key.equals("int") || key.equals("java.lang.Integer")) {
            klassNext[0] = "Integer";
            klassNext[1] = "parseInt(reader.nextName())";
        } else if (key.equals("short") || key.equals("java.lang.Short")) {
            klassNext[0] = "Short";
            klassNext[1] = "Short.parseShort(reader.nextName())";
        } else if (key.equals("long") || key.equals("java.lang.Long")) {
            klassNext[0] = "Long";
            klassNext[1] = "Long.parseLong(reader.nextName())";
        } else if (key.equals("float") || key.equals("java.lang.Float")) {
            klassNext[0] = "Float";
            klassNext[1] = "Float.parseFloat(reader.nextName())";
        } else if (key.equals("double") || key.equals("java.lang.Double")) {
            klassNext[0] = "Double";
            klassNext[1] = "Double.parseDouble(reader.nextName())";
        } else if (key.equals("String") || key.equals("java.lang.String")) {
            klassNext[0] = "String";
            klassNext[1] = "reader.nextName()";
        } else if (isEnum) {
            klassNext[0] = key;
            klassNext[1] = key + ".valueOf(nextString())";
        }
        if(klassNext[0] != null){
            fromatAppend(sb, index).append(FORMATE7).append(klassNext[0]).append(" item").append(index).append(" = ").append(klassNext[1]).append(";\n");
        }
    }

    /**
     *  must contain all collection class
     * @param sb
     * @param type
     * @param index
     */
    private void fromAppendNewCollection(StringBuilder sb, String type, int index) {
        String clz = type.substring(0, type.indexOf("<"));
        String newClz = null;
        if (ImportModel.IMPORT_LIST.equals(clz) || ImportModel.IMPORT_ARRAYLIST.equals(clz)
                || "java.util.AbstractList".equals(clz) || "java.util.AbstractCollection".equals(clz)
                || "java.util.Collection".equals(clz)) {
            newClz = ImportModel.IMPORT_ARRAYLIST + type.substring(type.indexOf("<"));
        } else if (ImportModel.IMPORT_LINKEDLIST.equals(clz) || "java.util.AbstractSequentialList".equals(clz)) {
            newClz = ImportModel.IMPORT_LINKEDLIST + type.substring(type.indexOf("<"));
        } else if (ImportModel.IMPORT_SET.equals(clz) || ImportModel.IMPORT_HASHSET.equals(clz)
                || "java.util.AbstractSet".equals(clz)) {
            newClz = ImportModel.IMPORT_HASHSET + type.substring(type.indexOf("<"));
        } else if (ImportModel.IMPORT_MAP.equals(clz) || ImportModel.IMPORT_HASHMAP.equals(clz)
                || "java.util.AbstractMap".equals(clz)) {
            newClz = ImportModel.IMPORT_HASHMAP + type.substring(type.indexOf("<"));
        } else if (ImportModel.IMPORT_HASHTABLE.equals(clz) || "java.util.Dictionary".equals(clz)) {
            newClz = ImportModel.IMPORT_HASHTABLE + type.substring(type.indexOf("<"));
        } else if ("java.util.TreeSet".equals(clz) || "java.util.NavigableSet".equals(clz)
                || "java.util.SortedSet".equals(clz)) {
            newClz = "java.util.TreeSet" + type.substring(type.indexOf("<"));
        } else if ("java.util.Vector".equals(clz)) {
            newClz = "java.util.Vector" + type.substring(type.indexOf("<"));
        } else if ("java.util.Stack".equals(clz)) {
            newClz = "java.util.Stack" + type.substring(type.indexOf("<"));
        } else if ("java.util.LinkedHashMap".equals(clz)) {
            newClz = "java.util.LinkedHashMap" + type.substring(type.indexOf("<"));
        } else if ("java.util.LinkedHashSet".equals(clz)) {
            newClz = "java.util.LinkedHashSet" + type.substring(type.indexOf("<"));
        } else if ("java.util.WeakHashMap".equals(clz)) {
            newClz = "java.util.WeakHashMap" + type.substring(type.indexOf("<"));
        } else if ("java.util.IdentityHashMap".equals(clz)) {
            newClz = "java.util.IdentityHashMap" + type.substring(type.indexOf("<"));
        } else if ("java.util.Properties".equals(clz)) {
            newClz = "java.util.Properties" + type.substring(type.indexOf("<"));
        } else if ("java.util.TreeMap".equals(clz) || "java.util.SortedMap".equals(clz)
                || "java.util.NavigableMap".equals(clz)) {
            newClz = "java.util.TreeMap" + type.substring(type.indexOf("<"));
        } else if ("java.util.ArrayDeque".equals(clz) || "java.util.Deque".equals(clz)) {
            newClz = "java.util.ArrayDeque" + type.substring(type.indexOf("<"));
        } else if ("java.util.PriorityQueue".equals(clz) || "java.util.AbstractQueue".equals(clz)
                || "java.util.Queue".equals(clz)) {
            newClz = "java.util.PriorityQueue" + type.substring(type.indexOf("<"));
        }
        fromatAppend(sb, index).append(FORMATE6).append(type).append(" list")
                .append(index).append(" = new ").append(newClz).append("();\n");
    }

    private void fromAppendCollection(StringBuilder sb, String type, String name, int index) {
        if (type.contains("<") && type.contains(">")) {
            fromAppendNewCollection(sb, type, index);
            String clz = type.substring(0, type.indexOf("<"));
            if (checkList(clz)) {
                type = type.substring(type.indexOf("<") + 1, type.lastIndexOf(">"));
                fromatAppend(sb, index).append(FORMATE6).append("reader.beginArray();\n");
                fromatAppend(sb, index).append(FORMATE6).append("while (reader.hasNext()) {\n");
                fromatAppend(sb, index).append(FORMATE7).append("if (reader.peek() == JsonToken.NULL) {\n");
                fromatAppend(sb, index).append(FORMATE8).append("reader.nextNull();\n");
                fromatAppend(sb, index).append(FORMATE8).append("continue;\n");
                fromatAppend(sb, index).append(FORMATE7).append("}\n");
                index++;
                fromAppendCollection(sb, type, null, index);
                fromatAppend(sb, index - 1).append(FORMATE6).append("}\n");
                fromatAppend(sb, index - 1).append(FORMATE6).append("reader.endArray();\n");
            } else if (checkMap(clz)) {
                type = type.substring(type.indexOf("<") + 1, type.lastIndexOf(">"));
                String[] split = new String[2];
                split[0] = type.substring(0, type.indexOf(","));
                split[1] = type.substring(type.indexOf(",") + 1, type.length());
                type = split[1].trim();
                fromatAppend(sb, index).append(FORMATE6).append("reader.beginObject();\n");
                fromatAppend(sb, index).append(FORMATE6).append("while (reader.hasNext()) {\n");
                fromatAppend(sb, index).append(FORMATE7).append("if (reader.peek() == JsonToken.NULL) {\n");
                fromatAppend(sb, index).append(FORMATE8).append("reader.nextNull();\n");
                fromatAppend(sb, index).append(FORMATE8).append("continue;\n");
                fromatAppend(sb, index).append(FORMATE7).append("}\n");
//				String item = reader.nextName();
                appendMapByKey(sb, split[0], index);
                index++;
                fromAppendCollection(sb, type, "item" + (index -1), index);
                fromatAppend(sb, index - 1).append(FORMATE6).append("}\n");
                fromatAppend(sb, index - 1).append(FORMATE6).append("reader.endObject();\n");
            }
            if (index > 1) {
                if(name != null && !name.isEmpty()){
                    fromatAppend(sb, index - 1).append(FORMATE6).append("list").append(index - 2).append(".put(item")
                            .append(index - 2).append(", list").append(index - 1).append(");\n");
                } else {
                    fromatAppend(sb, index - 1).append(FORMATE6).append("list").append(index - 2)
                            .append(".add(list").append(index - 1).append(");\n");
                }
            }
        } else {
            String[] klassNext = getArrayListKlass(getKlass(type));
            if(name != null && !name.isEmpty()){
                if (isEnum) {
                    fromatAppend(sb, index).append(FORMATE6).append("list")
                            .append(index - 1).append(".put(").append(name).append(", ").append(klassNext[0])
                            .append(".valueOf(reader.").append(klassNext[2])
                            .append("));\n");
                } else if (isMson) {
                    fromatAppend(sb, index).append(FORMATE6).append("list")
                            .append(index - 1).append(".put(").append(name).append(", ").append(klassNext[0])
                            .append(Mson.CLASSTARGET)
                            .append(".").append(klassNext[2])
                            .append("(reader));\n");
                } else {
                    fromatAppend(sb, index).append(FORMATE6).append("list")
                            .append(index - 1).append(".put(").append(name).append(", (")
                            .append(klassNext[0]).append(")reader.")
                            .append(klassNext[2]).append(");\n");
                }
            } else {
                if (isEnum) {
                    fromatAppend(sb, index).append(FORMATE6).append("list")
                            .append(index - 1).append(".add(").append(klassNext[0])
                            .append(".valueOf(reader.").append(klassNext[2])
                            .append("));\n");
                } else if (isMson) {
                    fromatAppend(sb, index).append(FORMATE6).append("list")
                            .append(index - 1).append(".add(").append(klassNext[0]).append(Mson.CLASSTARGET)
                            .append(".").append(klassNext[2])
                            .append("(reader));\n");
                } else {
                    fromatAppend(sb, index).append(FORMATE6).append("list")
                            .append(index - 1).append(".add((")
                            .append(klassNext[0]).append(")reader.")
                            .append(klassNext[2]).append(");\n");
                }
            }
        }
    }

}
