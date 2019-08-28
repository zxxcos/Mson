package com.medou.android.mson.processor;

import com.medou.android.mson.Mson;
import com.medou.android.mson.model.ClassModel;
import com.medou.android.mson.model.FieldModel;
import com.medou.android.mson.model.JavaFile;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.ElementKind;
import javax.tools.JavaFileObject;

class MsonCreator {

    private static Set<ClassModel> jsonClass = new HashSet<>();
    private static Set<JavaFile> classList = new HashSet<>();

    public static void addJsonClass(ClassModel model){
        jsonClass.add(model);
    }

    public void addJavaFile(JavaFile file){
        classList.add(file);
    }

    /**
     * @param mFiler
     * @param processor
     */
    public void generateClassFile(Filer mFiler, MsonProcessor processor){
        if(!checkCycleClass(processor)){
            return;
        }
//        for (JavaFile javaFile : classList) {
//            if (javaFile.enable && javaFile.classModel.fullName.contains("Cat")) {
//                processor.info(javaFile.toMsonString());
//                for (FieldModel fieldModel : javaFile.getFieldModelList()) {
//                    if (fieldModel.enable) {
//                        processor.info(fieldModel.toInfoString());
//                    }
//                }
//            }
//        }

        for (JavaFile javaFile : classList) {
            if (javaFile.enable) {
                createJavaFile(javaFile, mFiler);
            }
        }
    }

    private void createJavaFile(JavaFile javaFile, Filer mFiler){
        try { // write the file
            JavaFileObject source = mFiler.createSourceFile(javaFile.classModel.fullName + Mson.CLASSTARGET);
            Writer writer = source.openWriter();
            writer.write(javaFile.toMsonString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
        }
    }

    /**
     *
     * @param processor
     */
    private boolean checkCycleClass(MsonProcessor processor){
        for (JavaFile javaFile : classList){
            for (ClassModel classModel : jsonClass){
                if(classModel.fullName.equals(javaFile.classModel.fullName)){
                    if(classModel.kind == ElementKind.ENUM || classModel.isAbstract()){
                        javaFile.enable = false;
                    }
                }
                for (FieldModel fieldModel : javaFile.getFieldModelList()){
                    fieldModel.checkField(classModel);
                }
            }
        }
        List<String> fieldList = new ArrayList<>();
        Set<String> paramsSet = new HashSet<>();
        String checkString;
        for (JavaFile javaFile : classList){
            if(javaFile.enable){
                for (FieldModel fieldModel : javaFile.getFieldModelList()){
                    if(fieldModel.enable){
                        for (ClassModel classModel : jsonClass) {
                            if (classModel.kind == ElementKind.ENUM || classModel.isAbstract()) {
                                continue;
                            }
                            if(fieldModel.type.contains(classModel.fullName)){
                                fieldModel.isMson = true;
                                checkString = fieldModel.className + "#" + classModel.fullName;
                                if(!fieldList.contains(checkString)){
                                    fieldList.add(checkString);
                                }
                                paramsSet.add(fieldModel.className);
                                paramsSet.add(classModel.fullName);
                            }
                        }
                    }
                }
            }
        }
        DirectedGraph<String> graph = new DirectedGraph();
        for (String param : paramsSet){
            graph.addNode(param);
        }
        for (String field : fieldList){
            String[] split = field.split("#");
            graph.addEdge(split[0], split[1]);
        }
        CycleDetector<String> detector = new CycleDetector(graph);
        if(detector.containsCycle()){
            StringBuilder sb = new StringBuilder("A circular reference Inner Class:");
            for(String note : detector.getVerticesInCycles()){
                sb.append(note).append(";");
            }
            processor.info(sb.toString());
            return false;
        }
        return true;
    }

}
