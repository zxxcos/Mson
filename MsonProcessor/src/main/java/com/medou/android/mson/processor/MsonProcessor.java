package com.medou.android.mson.processor;

import com.medou.android.mson.annotations.JsonClass;
import com.medou.android.mson.annotations.JsonField;
import com.medou.android.mson.annotations.JsonIgnore;
import com.medou.android.mson.model.ClassModel;
import com.medou.android.mson.model.FieldModel;
import com.medou.android.mson.model.ImportModel;
import com.medou.android.mson.model.JavaFile;
import com.medou.android.mson.model.PackageModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic;

/**
 * Created by Administrator on 2018-03-22.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class MsonProcessor extends AbstractProcessor{

    private Filer mFiler;
    private Messager mMessager;
    private MsonCreator msonCreator = new MsonCreator();
    private List<FieldModel> annotationsList = new ArrayList<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(JsonClass.class.getCanonicalName());
        annotations.add(JsonField.class.getCanonicalName());
        annotations.add(JsonIgnore.class.getCanonicalName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set == null || set.isEmpty()) {
            return true;
        }
        processField(roundEnvironment);
        processIgnore(roundEnvironment);
        Set<? extends Element> classElements = roundEnvironment.getElementsAnnotatedWith(JsonClass.class);
        if (classElements == null || classElements.isEmpty()) {
            return false;
        }
        for (Element element : classElements) {
            if (element instanceof TypeElement) {
                processClass((TypeElement) element);
            }
        }
        msonCreator.generateClassFile(mFiler, this);
        return false;
    }

    /**
     * Process JsonField
     * @param environment
     */
    private synchronized void processField(RoundEnvironment environment){
        Set<? extends Element> fields = environment.getElementsAnnotatedWith(JsonField.class);
        if (fields == null || fields.isEmpty()) {
            return;
        }
        for (Element element : fields) {
            if (element.getKind() != ElementKind.FIELD) {
                continue;
            }
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            FieldModel model = new FieldModel();
            model.className = typeElement.getQualifiedName().toString();
            model.name = element.getSimpleName().toString();
            JsonField annotation = element.getAnnotation(JsonField.class);
            if(annotation != null){
                model.value = annotation.value();
            }
            model.annotation = JsonField.class.getName();
            annotationsList.add(model);
        }
    }

    /**
     * Process JsonField
     * @param environment
     */
    private synchronized void processIgnore(RoundEnvironment environment){
        Set<? extends Element> elements = environment.getElementsAnnotatedWith(JsonIgnore.class);
        if (elements == null || elements.isEmpty()) {
            return;
        }
        for (Element element : elements) {
            if (element.getKind() != ElementKind.FIELD) {
                continue;
            }
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            FieldModel model = new FieldModel();
            model.className = typeElement.getQualifiedName().toString();
            model.name = element.getSimpleName().toString();
            model.ignor = true;
            model.annotation = JsonIgnore.class.getName();
            annotationsList.add(model);
        }
    }

    /* 从TypeElement获取包装类型 */
    private static String getClassNameFromType(TypeElement element, String packageName) {
        int packageLen = packageName.length() + 1;
        return element.getQualifiedName().toString()
                .substring(packageLen).replace('.', '$');
    }


    private void processClass(TypeElement typeElement) {
        ClassModel classModel = new ClassModel();
        String modify = getModifier(typeElement.getModifiers());
        if(!modify.isEmpty()){
            classModel.setModifier(modify);
        }
        classModel.className = typeElement.getSimpleName().toString();
        classModel.fullName = typeElement.getQualifiedName().toString();
        classModel.kind = typeElement.getKind();
        msonCreator.addJsonClass(classModel);

        JavaFile javaFile = new JavaFile();
        PackageModel packageModel = new PackageModel(typeElement.getQualifiedName().toString(), typeElement.getSimpleName().toString());
        javaFile.setPackageModel(packageModel);
        ImportModel importModel = new ImportModel();
        javaFile.setImportModel(importModel);
        javaFile.setClassModel(classModel);

        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        for (Element element : enclosedElements) {
            if (element.getKind().isField()) {
                FieldModel fieldModel = new FieldModel();
                fieldModel.modifier = getModifier(element.getModifiers());
                fieldModel.className = typeElement.getQualifiedName().toString();
                fieldModel.type = element.asType().toString();
                fieldModel.typeKind = element.asType().getKind();
                fieldModel.name = element.getSimpleName().toString();
                if(fieldModel.typeKind == TypeKind.DECLARED){
                    importModel.modifyImportUtil(fieldModel.type, packageModel.packageName);
                }
                importModel.modifyImportAnnotations(annotationsList, fieldModel);
                javaFile.addFieldModelList(fieldModel);
            }
        }
        msonCreator.addJavaFile(javaFile);
    }

    private String getModifier(Set<Modifier> modifiers){
        StringBuilder sb = new StringBuilder();
        if(modifiers != null){
            Iterator<Modifier> it = modifiers.iterator();
            while (it.hasNext()) {
                if(sb.toString().isEmpty()){
                    sb.append(it.next().toString());
                } else {
                    sb.append(" ").append(it.next().toString());
                }
            }
        }
        return sb.toString();
    }

    public void info(String msg, Object... args) {
        mMessager.printMessage(
                Diagnostic.Kind.NOTE,
                String.format(msg, args));
    }

}
