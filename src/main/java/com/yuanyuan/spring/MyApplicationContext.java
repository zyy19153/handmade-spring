package com.yuanyuan.spring;


import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {

    private Class<?> configClass;

    private ConcurrentHashMap<String/*beanName*/, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String/*beanName*/, Object/*单例对象*/> singleObjects = new ConcurrentHashMap<>();

    private ArrayList<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public MyApplicationContext(Class<?> configClass) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        this.configClass = configClass;

        ComponentScan componentScan = configClass.getDeclaredAnnotation(ComponentScan.class);
        // 这里可以获取注解中标注的扫描路径: com.yuanyuan.service
        String path = componentScan.value();
        path = path.replace(".", "/");

        // 这里要把 classpath + 相对路径 拼接，得到扫描路径下的所有类
        ClassLoader classLoader = configClass.getClassLoader();
        URL url = classLoader.getResource(path);
        // 这里应该对 url.getFile() 判空
        File serviceDirectory = new File(url.getFile());

//        System.out.println("file + " + serviceDirectory);

        if(serviceDirectory.isDirectory()) {
            File[] fileNames = serviceDirectory.listFiles();

            // 其实这里的排列就是按字母顺序升序排列的
//            Arrays.sort(fileNames, (a, b) -> b.getAbsolutePath().compareTo(a.getAbsolutePath()));

            for (File fileName : fileNames) {
                String javaClassPath = fileName.getAbsolutePath();
                // 这里就是 service 包下每个类的绝对路径名: D:\JavaSpace\handmade-spring\target\classes\com\yuanyuan\service\Main.class
                /*
                后续的操作要看类上是否标注了 @Component 注解，也就需要反射来构建类，
                而 ClassLoader 需要一个相对路径：com.yuanyuan.service.Main 来加载类
                 */
                if (javaClassPath.endsWith(".class")) {
                    // 这里写的比较简单，实际上路径名未必有 com ，比如 org.springframework
                    javaClassPath = javaClassPath.substring(javaClassPath.indexOf("com"), javaClassPath.indexOf(".class"));
                    javaClassPath = javaClassPath.replace("\\", ".");

//                    System.out.println("javaClassPath = " + javaClassPath);

                    // 到这一步，我们成功获取了 com.yuanyuan.service 包下的所有类，后续的操作都基于此
                    Class<?> clazz = classLoader.loadClass(javaClassPath);

                    if (clazz.isAnnotationPresent(Component.class)) {

                        // 对 BeanPostProcessor 处理
                        // 就是看 clazz 是否是 BeanPostProcessor 的子类或实现类
                        if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                            BeanPostProcessor beanPostProcessor = (BeanPostProcessor) clazz.getConstructor().newInstance();
                            beanPostProcessors.add(beanPostProcessor);
                            singleObjects.put(getBeanName(clazz), beanPostProcessor);
                        }

                        // 如果有 @Component 注解，说明容器需要保管它
                        BeanDefinition bd = new BeanDefinition();
                        bd.setClazz(clazz);
                        if (clazz.isAnnotationPresent(Scope.class)) {
                            Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                            ScopeEnum scope = scopeAnnotation.value();
                            bd.setScope(scope.name().toLowerCase());
                        } else {
                            // 默认就是单例
                            bd.setScope("singleton");
                        }
                        // bean 的命名
                        String beanName = getBeanName(clazz);
                        beanDefinitionMap.put(beanName, bd);
                    }

                }
            }

            for (String beanName : beanDefinitionMap.keySet()) {
                getBean(beanName);
            }
        }
    }

    private static String getBeanName(Class<?> clazz) {
        String beanName = clazz.getDeclaredAnnotation(Component.class).value();
        if (beanName == null || beanName.isEmpty()) {
            beanName = Introspector.decapitalize(clazz.getSimpleName());
        }
        return beanName;
    }


    public Object getBean(String beanName) {
        BeanDefinition bd = beanDefinitionMap.get(beanName);
        if (bd == null) {
            throw new RuntimeException("BeanDefinition not found: " + beanName);
        }

        String scope = bd.getScope();
        Object bean;
        if ("singleton".equals(scope)) {
            bean = singleObjects.computeIfAbsent(beanName, name -> createBean(beanName, bd));
        } else {
            bean = createBean(beanName, bd);
        }
        return bean;
    }


    private Object createBean(String beanName, BeanDefinition bd) {
        Class<?> clazz = bd.getClazz();
        Object bean;
        try {
            bean = clazz.getConstructor().newInstance();

            // 简单的按字段名依赖注入
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                // 如果有 @Autowired 注解，就要进行依赖注入
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    // 这里就从 容器中找，看有没有字段名同名的bean，有的话塞进去
                    field.set(bean, getBean(field.getName()));
                }
            }

            /*
             Aware 是一种回调机制，spring 会告诉某个东西给被回调的 bean
             而 InitializingBean 作用是初始化，具体 afterPropertiesSet 会做什么，spring 不关心
             */

            // Aware 回调
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware) bean).setBeanName("BeanNameAware worked here!");
            }

            // 初始化阶段
            if (bean instanceof InitializingBean) {
                ((InitializingBean) bean).afterPropertiesSet();
            }

            // bean 的后处理扩展
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
//                bean = beanPostProcessor.postProcessBeforeInitialization(beanName, bean);
                bean = beanPostProcessor.postProcessAfterInitialization(beanName, bean);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bean;
    }
}
