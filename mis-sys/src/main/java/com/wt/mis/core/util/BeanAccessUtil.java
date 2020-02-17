package com.wt.mis.core.util;

import org.apache.commons.beanutils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 创建人：　王涛
 * 创建日期：2008-10-15 15:43:16
 * 功能描述：
 * ==============================================
 * 修改历史
 * 修改人		修改时间		修改原因
 * <p>
 * ==============================================
 */
public class BeanAccessUtil {
    private static Log log;
    private Integer int1;

    private int int2;

//    public static void main(String[] args) {
//        try {
//			Account to = new Account();
//			Account from = new Account();
//			from.setName("111111");
//			from.setRealName("33333");
//			from.setSex("男");
//			to.setName("222222");
//			BeanAccessUtil.copyNotNullBeanProperties(to,from);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    /**
     * 把一个bean的域值拷贝到另一个BEAN的域值。
     *
     * @param toObj
     * @param fromObj
     */
    public static void copyBeanProperties(Object toObj, Object fromObj) {
        try {
            BeanUtils.copyProperties(toObj, fromObj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 将目标Bean（toObj）中为空的值更新成来源bean（fromObj）中的值
     *
     * @param toObj
     * @param fromObj
     */
    public static void copyNotNullBeanProperties(Object toObj, Object fromObj) {
        PropertyDescriptor[] origDescriptors
                = PropertyUtils.getPropertyDescriptors(fromObj);
        for (int i = 0; i < origDescriptors.length; i++) {
            String name = origDescriptors[i].getName();
            if (!"class".equals(name)
                    && PropertyUtils.isWriteable(fromObj, name)) {
                try {
                    //只对非基本类型的属性赋值
                    if (PropertyUtils.getPropertyType(toObj, name).equals(Integer.class) ||
                            PropertyUtils.getPropertyType(toObj, name).equals(Byte.class) ||
                            PropertyUtils.getPropertyType(toObj, name).equals(String.class) ||
                            PropertyUtils.getPropertyType(toObj, name).equals(Long.class) ||
                            PropertyUtils.getPropertyType(toObj, name).equals(Double.class) ||
                            PropertyUtils.getPropertyType(toObj, name).equals(Float.class) ||
                            PropertyUtils.getPropertyType(toObj, name).equals(Character.class) ||
                            PropertyUtils.getPropertyType(toObj, name).equals(Short.class) ||
                            PropertyUtils.getPropertyType(toObj, name).equals(Boolean.class) ||
                            PropertyUtils.getPropertyType(toObj, name).equals(Date.class) ||
                            PropertyUtils.getPropertyType(toObj, name).equals(LocalDateTime.class)) {
                        if (null == PropertyUtils.getProperty(toObj, name)) {
                            PropertyUtils.setProperty(toObj, name, PropertyUtils.getProperty(fromObj, name));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void clearBeanProperties(Object obj) {
        PropertyDescriptor[] origDescriptors
                = PropertyUtils.getPropertyDescriptors(obj);
        for (int i = 0; i < origDescriptors.length; i++) {
            String name = origDescriptors[i].getName();
            if (!"class".equals(name)
                    && PropertyUtils.isWriteable(obj, name)) {
                try {
                    if (PropertyUtils.getPropertyType(obj, name).equals(boolean.class)) {
                        copyProperty(obj, name, false);
                    } else if (PropertyUtils.getPropertyType(obj, name).equals(int.class)) {
                        copyProperty(obj, name, false);
                    } else {
                        copyProperty(obj, name, null);
                    }
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {
                } catch (NoSuchMethodException e) {
                }
            }
        }
    }

    /**
     * 将类中空字符的属性设置成空
     *
     * @param obj
     */
    public static void emptyStrToNullBeanProperties(Object obj) {
        PropertyDescriptor[] origDescriptors
                = PropertyUtils.getPropertyDescriptors(obj);
        for (int i = 0; i < origDescriptors.length; i++) {
            String name = origDescriptors[i].getName();
            if (!"class".equals(name)
                    && PropertyUtils.isWriteable(obj, name)) {
                try {
                    if (PropertyUtils.getPropertyType(obj, name).equals(String.class)) {
                        if ("".equals(BeanUtils.getProperty(obj, name))) {
                            copyProperty(obj, name, null);
                        }
                    }
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {
                } catch (NoSuchMethodException e) {
                }
            }
        }
    }

    public static void copyProperty(Object bean, String name, Object value) throws IllegalAccessException,
            InvocationTargetException {
        if (log.isTraceEnabled()) {
            StringBuffer sb = new StringBuffer("  copyProperty(");
            sb.append(bean);
            sb.append(", ");
            sb.append(name);
            sb.append(", ");
            if (value == null)
                sb.append("<NULL>");
            else if (value instanceof String)
                sb.append((String) value);
            else if (value instanceof String[]) {
                String[] values = (String[]) value;
                sb.append('[');
                for (int i = 0; i < values.length; i++) {
                    if (i > 0)
                        sb.append(',');
                    sb.append(values[i]);
                }
                sb.append(']');
            } else
                sb.append(value.toString());
            sb.append(')');
            log.trace(sb.toString());
        }
        Object target = bean;
        int delim = name.lastIndexOf('.');
        if (delim >= 0) {
            try {
                target = PropertyUtils.getProperty(bean, name.substring(0, delim));
            } catch (NoSuchMethodException e) {
                return;
            }
            name = name.substring(delim + 1);
            if (log.isTraceEnabled()) {
                log.trace("    Target bean = " + target);
                log.trace("    Target name = " + name);
            }
        }
        String propName = null;
        Class<?> type = null;
        int index = -1;
        String key = null;
        propName = name;
        int i = propName.indexOf('[');
        if (i >= 0) {
            int k = propName.indexOf(']');
            try {
                index = Integer.parseInt(propName.substring(i + 1, k));
            } catch (NumberFormatException e) {
                /* empty */
            }
            propName = propName.substring(0, i);
        }
        int j = propName.indexOf('(');
        if (j >= 0) {
            int k = propName.indexOf(')');
            try {
                key = propName.substring(j + 1, k);
            } catch (IndexOutOfBoundsException e) {
                /* empty */
            }
            propName = propName.substring(0, j);
        }
        if (target instanceof DynaBean) {
            DynaClass dynaClass = ((DynaBean) target).getDynaClass();
            DynaProperty dynaProperty = dynaClass.getDynaProperty(propName);
            if (dynaProperty == null)
                return;
            type = dynaProperty.getType();
        } else {
            PropertyDescriptor descriptor = null;
            do {
                try {
                    descriptor = PropertyUtils.getPropertyDescriptor(target, name);
                    if (descriptor != null)
                        break;
                } catch (NoSuchMethodException e) {
                    /* empty */
                }
                return;
            } while (false);
            type = descriptor.getPropertyType();
            if (type == null) {
                if (log.isTraceEnabled())
                    log.trace("    target type for property '" + propName + "' is null, so skipping ths setter");
                return;
            }
        }
        if (log.isTraceEnabled())
            log.trace("    target propName=" + propName + ", type=" + type + ", index=" + index + ", key=" + key);
        if (index >= 0) {
            Converter converter = ConvertUtils.lookup(type.getComponentType());
            if (converter != null) {
                log.trace("        USING CONVERTER " + converter);
                value = converter.convert(type, value);
            }
            try {
                PropertyUtils.setIndexedProperty(target, propName, index, value);
            } catch (NoSuchMethodException e) {
                throw new InvocationTargetException(e, "Cannot set " + propName);
            }
        } else if (key != null) {
            try {
                PropertyUtils.setMappedProperty(target, propName, key, value);
            } catch (NoSuchMethodException e) {
                throw new InvocationTargetException(e, "Cannot set " + propName);
            }
        } else {
            Converter converter = ConvertUtils.lookup(type);
            if (converter != null) {
                log.trace("        USING CONVERTER " + converter);
                value = converter.convert(type, value);
            }
            try {
                PropertyUtils.setSimpleProperty(target, propName, value);
            } catch (NoSuchMethodException e) {
                throw new InvocationTargetException(e, "Cannot set " + propName);
            }
        }
    }


    public static Class<?> getProptType(Object obj, String fldName) {
        PropertyDescriptor[] origDescriptors = PropertyUtils.getPropertyDescriptors(obj);
        for (int i = 0; i < origDescriptors.length; i++) {
            String name = origDescriptors[i].getName();
            if (name.equals(fldName)) {
                return origDescriptors[i].getPropertyType();
            }
        }
        return null;
    }

    /**
     * 获取指定对象中指定字段名的值
     *
     * @param obj
     * @param fldName
     * @return
     */
    private static String getPropertyValue(Object obj, String fldName) {
        try {
            String fldValue = BeanUtils.getProperty(obj, fldName);
            return fldValue;
        } catch (NoSuchMethodException ex) {
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 获得指定类的所有字段列表
     *
     * @param myClass
     * @return
     */
    public static List<Field> getFldNames(Class<?> myClass) {
        List<Field> result = new ArrayList<Field>();

        Class<?> superClass = myClass;
        while (superClass != null) {
            Field[] myFlds = superClass.getDeclaredFields();
            for (int i = 0; i < myFlds.length; i++) {
                result.add(myFlds[i]);
            }
            superClass = superClass.getSuperclass();
        }

        return result;
    }

    /**
     * 得到字段的属性值，属性值以字符类型返回。 注：要想得到该属性的值，该字段必须要有一个在pulic范围的get方法。
     *
     * @param myObj   要查找的字段的对象。
     * @param fldName 字段的名称。
     * @return 如果没有该字段或字段的属性值为NULL，则返回一个null。
     */
    public static String getFieldValue(Object myObj, String fldName) {
        try {
            String fldValue = BeanUtils.getProperty(myObj, fldName);

            return fldValue;
        } catch (NoSuchMethodException ex) {
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 开发人：王涛
     * 开发日期: 2008-10-29  下午12:08:10
     * 功能描述: 将一个对象中的数组属性中的某一个值拷贝到目标对象的同名属性中
     * 不支持基本数据类型
     * 方法的参数和返回值:
     *
     * @param to
     * @param from
     * @param index
     * @param proptName
     * @return
     */
    public static boolean copyAryPropt(Object to, Object from, int index, String proptName) {
        boolean change = false;
        try {
            Object value = PropertyUtils.getSimpleProperty(from, proptName);
            if (value instanceof Object[]) {
                Object[] ary = (Object[]) value;
                if (ary.length <= index || ObjEquals(PropertyUtils.getSimpleProperty(to, proptName), ary[index])) return false;
                BeanUtils.setProperty(to, proptName, ((Object[]) value)[index]);
                change = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return change;
    }

    /**
     * 开发人：王涛
     * 开发日期: 2008-10-29  下午12:08:43
     * 功能描述: 将一个对象中的所有数组属性中的某一个值拷贝到目标对象的同名属性中。
     * 不支持基本数据类型
     * 方法的参数和返回值:
     *
     * @param to
     * @param from
     * @param index
     * @return
     */
    public static boolean copyAryPropts(Object to, Object from, int index) {
        PropertyDescriptor[] origDescriptors = PropertyUtils.getPropertyDescriptors(to);
        boolean change = false;
        for (int i = 0; i < origDescriptors.length; i++) {
            String name = origDescriptors[i].getName();
            if (!"class".equals(name) && PropertyUtils.isReadable(from, name) && PropertyUtils.isWriteable(to, name)) {
                try {
                    Object value = PropertyUtils.getSimpleProperty(from, name);
                    if (value instanceof Object[]) {
                        Object[] ary = (Object[]) value;
                        if (ary.length <= index || ObjEquals(PropertyUtils.getSimpleProperty(to, name), ary[index])) continue;
                        BeanUtils.setProperty(to, name, ary[index]);
                        change = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return change;
    }

    /**
     * 开发人：王涛
     * 开发日期: 2008-11-30  上午10:54:48
     * 功能描述: 将对象中的属性以及属性对应的属性值组成一个字符串，如 name=john&sex=male&,其中split1和split2分别为= &
     * 方法的参数和返回值:
     *
     * @param obj
     * @param split1
     * @param split2
     * @return
     */
    public static String toJSon(Object obj, String split1, String split2) {
        PropertyDescriptor[] origDescriptors = PropertyUtils.getPropertyDescriptors(obj);
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < origDescriptors.length; i++) {
            String name = origDescriptors[i].getName();
            if (!"class".equals(name) && PropertyUtils.isWriteable(obj, name)) {
                try {
                    Object value = PropertyUtils.getSimpleProperty(obj, name);
                    res.append(name).append(split1).append(value == null ? "" : value).append(split2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return res.toString();
    }

    public static boolean isPropertyExist(Object obj, String proptName) {
        if (proptName == null) return false;
        try {
            PropertyDescriptor[] origDescriptors = PropertyUtils.getPropertyDescriptors(obj);
            for (int i = 0; i < origDescriptors.length; i++) {
                if (proptName.equals(origDescriptors[i].getName()))
                    return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 开发人：王涛
     * 开发日期: 2014-11-19  14:10:02
     * 功能描述: 返回指定对象的指定属性值，为null时返回指定的defaults值
     * 方法的参数和返回值:
     *
     * @param obj
     * @param proptName
     * @param defaults
     * @return
     */
    public static String getProptVal(Object obj, String proptName, String defaults) {
        String val = BeanAccessUtil.getFieldValue(obj, proptName);
        return val == null ? defaults : val;
    }

    /**
     * 开发人：王涛
     * 开发日期: 2014-11-19  14:10:02
     * 功能描述: 返回第一个非null的值，如均为null，则返回null
     * 方法的参数和返回值:
     *
     * @param s1
     * @param s2
     * @return
     */
    public static String nvl(String s1, String s2) {
        return s1 == null ? s2 : s1;
    }

    /**
     * 开发人：王涛
     * 开发日期: 2008-10-29  下午12:10:02
     * 功能描述: 比较两个对象是否等价，空字符串和null等价
     * 方法的参数和返回值:
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean ObjEquals(Object obj1, Object obj2) {
        if ("".equals(obj1)) obj1 = null;
        if ("".equals(obj2)) obj2 = null;
        if (obj1 == null && obj2 == null) return true;
        else if (obj1 == null || obj2 == null) return false;
        else return obj1.equals(obj2);
    }

    /**
     * @param testFld The testFld to set.
     */
    public void setTestFld(java.sql.Date testFld) {
    }

    /**
     * @return Returns the int1.
     */
    public Integer getInt1() {
        return int1;
    }

    /**
     * @param int1 The int1 to set.
     */
    public void setInt1(Integer int1) {
        this.int1 = int1;
    }

    /**
     * @return Returns the int2.
     */
    public int getInt2() {
        return int2;
    }

    /**
     * @param int2 The int2 to set.
     */
    public void setInt2(int int2) {
        this.int2 = int2;
    }

    static {
        log = LogFactory.getLog
                ("org.apache.commons.beanutils.BeanUtils");
    }
}
