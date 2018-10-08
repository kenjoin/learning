package chiyue.learning.sun.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.alibaba.fastjson.JSONObject;

import chiyue.learning.sun.reflect.annotation.Self;

public class ReflectPractice {
	
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		String className = "chiyue.learning.sun.reflect.dto.Person";
		
		Class<?> clazz = Class.forName(className);
		
		System.out.println("the class name is ："+clazz.getName());
		
		/** annotations */
		Annotation[] annotations = clazz.getDeclaredAnnotations();
		for(int i=0; i< annotations.length; i++) {
			String logInfo = String.format("the class has annotation No%s , name:%s",i,annotations[i].annotationType().getName());
			System.out.println(logInfo);
		}
		
		/** superclass */
		String superclassName = clazz.getSuperclass().getName();
		System.out.println("superclassName: "+superclassName);
		
		Class<?>[] interfacees = clazz.getInterfaces();
		for(int i=0;i<interfacees.length;i++) {
			String logInfo = String.format("the class superclass No%s , name:%s", i, interfacees[i].getName());
			System.out.println(logInfo);
		}
		
		//实例化
		Object obj = clazz.newInstance();
	
		System.out.println(JSONObject.toJSONString(obj));
		
		/** constructors */
		
		//获取所有构造方法
		Constructor<?>[] constructors = clazz.getConstructors();
		
		Object obj1 = null;
		
		for(int i=0; i<constructors.length ; i++) {
			Constructor<?> constructor = constructors[i];
			String logInfo = String.format("Constructor No%s: ,parameterCount:%s, parameterClassType：%s", i, constructor.getParameterCount(), constructor.getGenericParameterTypes());
			System.out.println(logInfo);
			if(constructor.getParameterCount() == 2) {
				//通过指定构造方法实例化对象
				obj1 = constructor.newInstance(1, "ken by constructor");
			}
		}
		System.out.println("this is obj1 -> " + obj1);
		
		/** methods */
		//该方法仅获取该类自身的方法 //clazz.getMethods() --获取所有方法
		Method[] methods = clazz.getDeclaredMethods(); 
		Method publicSay = null;
		for(int i=0;i<methods.length;i++) {
			Method method = methods[i];
			String logInfo = String.format("Method No%s, name:%s, parameterCount:%s", i, method.getName(), method.getParameterCount());
			System.out.println(logInfo);
			if(method.getName().equals("publicSay")) {
				publicSay = method;
			}
		}
		
		if(publicSay != null) {
			publicSay.invoke(obj1, new Object[] {new String[] {"ken","jon"}});
		}
		
		Method getId = clazz.getMethod("getId", null);
		Object id = getId.invoke(obj1, null);
		System.out.println("current obj id is : " + id);
		
		Method setAddr = clazz.getMethod("setAddr", new Class<?>[] {String.class});
		setAddr.invoke(obj1, "ShenZhen NanShan");
		
		/** Fields */
		Field[] fields = clazz.getDeclaredFields();
		executeFieldMethod(clazz, obj1, fields);
		executeFieldMethod(clazz, obj1, clazz.getGenericSuperclass().getClass().getFields());
		
		
		/** check annotation */
		
	}

	private static void executeFieldMethod(Class<?> clazz, Object obj1, Field[] fields)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		for(int i=0 ; i<fields.length; i++) {
			Field field = fields[i];
			String fieldName = field.getName();
			String methodName = "get"+fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
			
			Method method = clazz.getMethod(methodName, null);
			
			Object returnObj = method.invoke(obj1, null);
			
			System.out.println(methodName +" -> "+ returnObj);
			
			if(method.isAnnotationPresent(Self.class)) {
				System.out.println(methodName +" persent annotation ->" + Self.class.getName());
			}
			
		}
	}
	
}
