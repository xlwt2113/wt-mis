package com.wt.mis.core.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 反射工具类
 *
 * @author mac

 */
@Slf4j
@SuppressWarnings("AlibabaRemoveCommentedCode")
public class ReflectUtils {

    /**
     * 实体类缓存
     */
    private static Map<String, Object> entityMap = new HashMap<>();

    private ReflectUtils() {
    }

    /**
     * 创建实例方法
     *
     * @param clazz
     * @param interfaceIndex
     * @param typeIndex
     * @return
     */
    public static Object interfaceGenericTypeInstance(Class<?> clazz, int interfaceIndex, int typeIndex) {
        return entityMap.computeIfAbsent(clazz.getName(), v -> {
            try {
                return ReflectUtils.getInterfaceGenericType(clazz, interfaceIndex, typeIndex).newInstance();
            } catch (Exception e) {
                throw new UnsupportedOperationException(e);
            }
        });
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param pack      包路径
     * @param recursive 是否循环迭代
     * @return
     * @author mac

     */
    public static Set<Class<?>> getClasses(final String pack, final boolean recursive) {

        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        // 获取包的名字 并进行替换
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(
                    packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    log.info("file 类型的扫描 class 文件");
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath,
                            recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    System.err.println("jar类型的扫描");
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection())
                                .getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx)
                                            .replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class")
                                            && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(
                                                packageName.length() + 1, name
                                                        .length() - 6);
                                        try {
                                            // 添加到classes
                                            classes.add(Class
                                                    .forName(packageName + '.'
                                                            + className));
                                        } catch (ClassNotFoundException e) {
                                            // log.error("添加用户自定义视图类错误 找不到此类的.class文件")
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
//                         log.error("在扫描用户定义视图时从jar包获取文件出错")
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    public static Set<Class<?>> getClasses(String pack) {
        return getClasses(pack, true);
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    public static void findAndAddClassesInPackageByFile(
            String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件")
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        File[] dirfiles = dir.listFiles(file -> (recursive && file.isDirectory())
                || (file.getName().endsWith(".class")));
        // 循环所有文件
        if (dirfiles != null)
            for (File file : dirfiles) {
                // 如果是目录 则继续扫描
                if (file.isDirectory()) {
                    findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
                            file.getAbsolutePath(), recursive, classes);
                } else {
                    // 如果是java类文件 去掉后面的.class 只留下类名
                    String className = file.getName().substring(0,
                            file.getName().length() - 6);
                    try {
                        // 添加到集合中去
                        //classes.add(Class.forName(packageName + '.' + className))
                        //经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                        classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                    } catch (ClassNotFoundException e) {
                        // error("添加用户自定义视图类错误 找不到此类的.class文件");
                        System.err.println(e.getMessage());
                    }
                }
            }
    }

    /**
     * 执行私有方法
     *
     * @param subject
     * @param methodName
     * @return
     */
    public static Object invokeDeclaredMethod(Object subject, String methodName, Object... parameters) {
        Object result = null;
        try {
            Class[] classes = new Class[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                classes[i] = parameters[i].getClass();
            }
            Method method = subject.getClass().getDeclaredMethod(methodName, classes);
            method.setAccessible(true);
            result = method.invoke(subject, parameters);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    /**
     * 获取该 类 上的泛型
     *
     * @param clazz
     * @param typeIndex
     * @return
     */
    public static Class getClassGenericType(final Class clazz, final int typeIndex) {
        ParameterizedType genericType = (ParameterizedType) clazz.getGenericSuperclass();

        Type[] classTypes = genericType.getActualTypeArguments();
        assert typeIndex > 0 && typeIndex < classTypes.length;
        assert classTypes[typeIndex] instanceof Class;

        return (Class) classTypes[typeIndex];
    }

    /**
     * 获取某个 接口 上的泛型
     *
     * @param clazz
     * @param interfaceIndex
     * @param typeIndex
     * @return
     */
    public static Class getInterfaceGenericType(final Class clazz, final int interfaceIndex, final int typeIndex) {
        Type[] interfacesTypes = clazz.getGenericInterfaces();
        assert interfaceIndex > 0 && interfaceIndex < interfacesTypes.length;

        Type[] classTypes = ((ParameterizedType) interfacesTypes[interfaceIndex]).getActualTypeArguments();
        assert typeIndex > 0 && typeIndex < classTypes.length;
        assert classTypes[typeIndex] instanceof Class;

        return (Class) classTypes[typeIndex];
    }

}
