package com.absurd.briefness;

import com.google.auto.service.AutoService;

import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;

/**
 * Author: mr-absurd
 * Github: http://github.com/mr-absurd
 * Data: 2017/9/16.
 */
@AutoService(Processor.class)
public class BriefnessProcessor extends AbstractBriefnessProcessor {
    @Override
    protected void processClick(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elementsWithBind = roundEnv.getElementsAnnotatedWith(BindClick.class);
        for (Element element : elementsWithBind) {
            if (!checkAnnotationValid(element, BindLayout.class)) continue;
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            ProxyInfo proxyInfo = mProxyMap.get(typeElement.getQualifiedName().toString());
            if (proxyInfo == null) {
                proxyInfo = new ProxyInfo(elementUtils, typeElement);
            }
            BindClick bindViewAnnotation = element.getAnnotation(BindClick.class);
            int[] id = bindViewAnnotation.value();
            proxyInfo.briefnessMethod.put(id, element);
        }
    }

    @Override
    protected void processViews(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elementsWithBind = roundEnv.getElementsAnnotatedWith(BindViews.class);
        for (Element element : elementsWithBind) {
            if (!checkAnnotationValid(element, BindViews.class)) continue;
            VariableElement variableElement = (VariableElement) element;
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
            String fullClassName = classElement.getQualifiedName().toString();
            ProxyInfo proxyInfo = mProxyMap.get(fullClassName);
            if (proxyInfo == null) {
                proxyInfo = new ProxyInfo(elementUtils, classElement);
                mProxyMap.put(fullClassName, proxyInfo);
            }
            BindViews bindViewAnnotation = variableElement.getAnnotation(BindViews.class);
            int[] id = bindViewAnnotation.value();
            proxyInfo.briefnessVariable.put(id, variableElement);
        }
    }

    @Override
    protected void processView(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elementsWithBind = roundEnv.getElementsAnnotatedWith(BindView.class);
        for (Element element : elementsWithBind) {
            if (!checkAnnotationValid(element, BindView.class)) continue;
            VariableElement variableElement = (VariableElement) element;
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
            String fullClassName = classElement.getQualifiedName().toString();
            ProxyInfo proxyInfo = mProxyMap.get(fullClassName);
            if (proxyInfo == null) {
                proxyInfo = new ProxyInfo(elementUtils, classElement);
                mProxyMap.put(fullClassName, proxyInfo);
            }
            BindView bindViewAnnotation = variableElement.getAnnotation(BindView.class);
            int id = bindViewAnnotation.value();
            proxyInfo.briefnessVariable.put(new int[]{id}, variableElement);
        }
    }

    @Override
    protected void processLayout(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> elementsWithBind = roundEnv.getElementsAnnotatedWith(BindLayout.class);
        for (Element element : elementsWithBind) {
            if (!checkAnnotationValid(element, BindLayout.class)) continue;

            String fullClassName = element.asType().toString();
            ProxyInfo proxyInfo = mProxyMap.get(fullClassName);
            if (proxyInfo == null) {
                proxyInfo = new ProxyInfo(elementUtils, (TypeElement) element);
                mProxyMap.put(fullClassName, proxyInfo);
            }
            BindLayout bindViewAnnotation = element.getAnnotation(BindLayout.class);
            int id = bindViewAnnotation.value();
            proxyInfo.briefnessVariable.put(new int[]{id}, null);
        }
    }

    @Override
    protected void process() {
        for (String key : mProxyMap.keySet()) {
            ProxyInfo proxyInfo = mProxyMap.get(key);
            try {
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                        proxyInfo.getProxyClassFullName(),
                        proxyInfo.getTypeElement()
                );
                Writer writer = jfo.openWriter();
                writer.write(proxyInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (Exception e) {
                error(proxyInfo.getTypeElement(), "Unable to write injector for type %s: %s", proxyInfo.getTypeElement(), e.getMessage());
                e.printStackTrace();
            }
        }
    }

}