package com.gdlgxy.navcompilermodule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gdlgxy.navannotationmodule.ActivityDestination;
import com.gdlgxy.navannotationmodule.FragmentDestination;
import com.google.auto.service.AutoService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes(
        {"com.gdlgxy.navannotationmodule.ActivityDestination",
                "com.gdlgxy.navannotationmodule.FragmentDestination"})
public class NavCompilerModule extends AbstractProcessor {
    private Messager messager;
    private Filer filer;
    private static final String OUTPUT_FILE_NAME = "destination.json";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> fragmentElements = roundEnv.getElementsAnnotatedWith(FragmentDestination.class);
        Set<? extends Element> activityElements = roundEnv.getElementsAnnotatedWith(ActivityDestination.class);
        if(!fragmentElements.isEmpty() || !activityElements.isEmpty()){
            HashMap<String, JSONObject>destMap = new HashMap<>();
            handleDestination(fragmentElements,FragmentDestination.class,destMap);
            handleDestination(fragmentElements,ActivityDestination.class,destMap);

            FileOutputStream fos = null;
            OutputStreamWriter writer = null;
            try{
                FileObject resource = filer.
                        createResource(StandardLocation.CLASS_OUTPUT,"",OUTPUT_FILE_NAME);
                String resourcePath = resource.toUri().getPath();
                messager.printMessage(Diagnostic.Kind.NOTE, resourcePath);

                String appPath = resourcePath.substring(0,resourcePath.indexOf("app")+4);
                String assetsPath = appPath + "src/main/assets/";
                File file = new File(assetsPath);
                if(!file.exists()){
                    file.mkdir();
                }

                File outPutFile = new File(file,OUTPUT_FILE_NAME);
                if(outPutFile.exists()){
                    outPutFile.delete();
                }
                outPutFile.createNewFile();
                String content = JSON.toJSONString(destMap);
                fos = new FileOutputStream(outPutFile);
                writer = new OutputStreamWriter(fos,"UTF-8");
                writer.write(content);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (writer != null){
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(fos != null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

    private void handleDestination(Set<? extends Element> elements, Class<? extends Annotation> annotationClass, HashMap<String, JSONObject> destMap) {
        for (Element element :elements){
            TypeElement typeElement = (TypeElement) element;
            String pageUrl = null;
            String className = typeElement.getQualifiedName().toString();
            int id = Math.abs(className.hashCode());
            boolean needLogin = false;
            boolean asStarter = false;
            boolean isFragment = true;
            Annotation annotation = typeElement.getAnnotation(annotationClass);
            if(annotation instanceof FragmentDestination){
                FragmentDestination dest = (FragmentDestination) annotation;
                pageUrl = dest.pageUrl();
                asStarter = dest.asStarter();
                needLogin = dest.needLogin();
            }else if(annotation instanceof ActivityDestination){
                ActivityDestination dest = (ActivityDestination) annotation;
                pageUrl = dest.pageUrl();
                asStarter = dest.asStarter();
                needLogin = dest.needLogin();
                isFragment = false;
            }

            if(destMap.containsKey(pageUrl)){
                messager.printMessage(Diagnostic.Kind.ERROR,"same pageUrl");
            }else {
                JSONObject object = new JSONObject();
                object.put("id",id);
                object.put("asStarter",asStarter);
                object.put("needLogin",needLogin);
                object.put("className",className);
                object.put("pageUrl",pageUrl);
                object.put("isFragment",isFragment);
                if(pageUrl !=null){
                    destMap.put(pageUrl,object);
                }
            }
        }
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }
}