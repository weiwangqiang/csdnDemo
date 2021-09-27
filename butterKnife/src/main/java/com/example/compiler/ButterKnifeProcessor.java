package com.example.compiler;

import com.example.annotation.BindView;
import com.example.annotation.Onclick;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;

// 用于声明该类为注解处理器
@AutoService(Processor.class)
public class ButterKnifeProcessor extends AbstractProcessor {
    // 用于打印日志信息
    private Messager mMessager;
    // 用于解析 Element
    private Elements mElements;
    // 存储某个类下面对应的BindModel
    private Map<TypeElement, List<BindModel>> mTypeElementMap = new HashMap<>();
    // 存储id绑定的方法，即OnClick
    private Map<Integer, Element> mOnclickElementMap = new HashMap<>();
    // 用于将创建的java程序输出到相关路径下。
    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mElements = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
    }

    /**
     * 此方法用来设置支持的注解类型，没有设置的无效（获取不到）
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        // 把支持的类型添加进去
        supportTypes.add(BindView.class.getCanonicalName());
        supportTypes.add(Onclick.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "===============process start =============");
        mTypeElementMap.clear();
        // Process each @BindView element.
        for (Element element : roundEnv.getElementsAnnotatedWith(BindView.class)) {
            verifyAnnotation(element, BindView.class, ElementKind.FIELD);
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            Name qualifiedName = enclosingElement.getQualifiedName();
            Name simpleName = element.getSimpleName();

            // Assemble information on the field.
            int id = element.getAnnotation(BindView.class).value();
            String content = String.format("====> qualifiedName: %s simpleName: %s id: %d"
                    , qualifiedName, simpleName, id);
            mMessager.printMessage(Diagnostic.Kind.NOTE, content);
            List<BindModel> modelList = mTypeElementMap.get(enclosingElement);
            if (modelList == null) {
                modelList = new ArrayList<>();
            }
            modelList.add(new BindModel(element, id));
            mTypeElementMap.put(enclosingElement, modelList);
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(Onclick.class)) {
            verifyAnnotation(element, Onclick.class, ElementKind.METHOD);
            int[] ids = element.getAnnotation(Onclick.class).value();
            for (int id : ids) {
                mOnclickElementMap.put(id, element);
            }
        }

        mTypeElementMap.forEach((typeElement, bindModels) -> {
            String packageName = mElements.getPackageOf(typeElement)
                    .getQualifiedName().toString();
            String className = typeElement.getSimpleName().toString();
            String bindClass = className + "_ViewBind";
            // 生成构造函数
            MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC) // 声明为public
                    .addParameter(ClassName.bestGuess(className), "target"); // 添加构造参数
            bindModels.forEach(model -> {
                // 构造函数内添加代码
                builder.addStatement("target.$L = ($L)target.findViewById($L)",
                        model.getViewFieldName(), model.getViewFieldType(), model.getResId());
            });
            String viewPath = "android.view.View";
            mOnclickElementMap.forEach((id, element) -> {
                // 构造函数内添加代码
                builder.addStatement("(($L) target.findViewById($L)).setOnClickListener((view) -> {\n" +
                                "            target.$L();\n" +
                                "        })",
                        viewPath, id, element.getSimpleName().toString());
            });
            // 构建类
            TypeSpec typeSpec = TypeSpec.classBuilder(bindClass)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(builder.build())
                    .build();
            // 生成java file
            JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                    .addFileComment("auto create by ButterKnife ")
                    .build();
            try {
                // javaFile 写到指定路径下
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        mMessager.printMessage(Diagnostic.Kind.NOTE, "===============process end=============");
        return true;
    }

    // 做校验动作
    private boolean verifyAnnotation(Element element, Class<?> annotationClass, ElementKind targetKind) {
        if (element.getKind() != targetKind) {
            error(element, "%s must be declared on field.", annotationClass.getSimpleName());
            return false;
        }
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
            error(element, " %s %s must not be private or static.",
                    annotationClass.getSimpleName(),
                    element.getSimpleName());
            return false;
        }
        return true;
    }

    /**
     * 打印错误日志方法
     */
    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, message, element);
    }
}