package com.thinking.machines.webrock.TMWebRockStarter;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.model.*;
import com.thinking.machines.webrock.pojo.*;

import java.net.URL;
public class StartupServlet extends HttpServlet
{
@Override
public void init() throws ServletException 
{
String packagePrefix= getServletConfig().getInitParameter("SERVICE_PACKAGE_PREFIX");
try
{
List<Class> classesWithAnnotation = getClassesWithAnnotation(packagePrefix, Path.class);
for (Class clazz : classesWithAnnotation) {
System.out.println("Class with @Path annotation: " + clazz.getName());
analyzeMethodsForAnnotation(clazz, Path.class);
// how to put something in application scope here

}
}catch(Exception e)
{
System.out.println(e);
}
}


private static List<Class> getClassesWithAnnotation(String packagePrefix,Class annotation) throws IOException, ClassNotFoundException 
{
List<Class> classesWithAnnotation = new ArrayList<>();
ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
Enumeration<URL> resources = classLoader.getResources("");
while (resources.hasMoreElements()) 
{
URL resource = resources.nextElement();
if (resource.getProtocol().equals("file")) 
{
File file = new File(resource.getPath());
processFileBasedClasses(packagePrefix, file, classesWithAnnotation, annotation);
}
}
return classesWithAnnotation;
}

private static void processFileBasedClasses(String packagePrefix, File directory,List<Class> classesWithAnnotation, Class annotation) throws ClassNotFoundException 
{
if (directory.isDirectory()) 
{
File[] files = directory.listFiles();
if (files != null) 
{
for (File file : files) 
{
if (file.isDirectory()) 
{
// Recursively process subdirectories
processFileBasedClasses(packagePrefix, file, classesWithAnnotation, annotation);
} 
else if (file.isFile() && file.getName().endsWith(".class")) 
{
// Process individual class files
String className = getClassName(packagePrefix, file);
if (className != null) 
{
Class clazz = Class.forName(className);
if (isInSubpackage(clazz, packagePrefix) && hasAnnotation(clazz, annotation)) 
{
classesWithAnnotation.add(clazz);
}
}
}
}
}
}
}
private static String getClassName(String packagePrefix, File file) 
{
String filePath = file.getPath().replace(File.separator, ".");
int start = filePath.indexOf(packagePrefix);
if (start != -1) 
{
int end = filePath.lastIndexOf(".class");
return filePath.substring(start, end);
} 
else 
{
return null;
}
}

private static boolean isInSubpackage(Class clazz, String packagePrefix) 
{
return clazz.getPackage().getName().startsWith(packagePrefix);
}

private static boolean hasAnnotation(Class clazz, Class annotation) 
{
return clazz.isAnnotationPresent(annotation);
}

private static void analyzeMethodsForAnnotation(Class clazz, Class annotation) 
{
String classAnnotation="";
String methodAnnotation="";
String path="";
String forwardTo=null;
Boolean isGet=false;
Boolean isPost=false;
Boolean isClassHasMapping=false;
String classMapping="";
Annotation annotationInstance = clazz.getAnnotation(annotation);
if (annotationInstance != null && annotationInstance instanceof Path) 
{
Path ann = (Path) annotationInstance;
classAnnotation=ann.value();
}
if ( clazz.isAnnotationPresent(Get.class)) 
{
isClassHasMapping=true;
classMapping="Get";
System.out.println("------------(" + classAnnotation+ ") and it is of type  get -------------------");
}
else if( clazz.isAnnotationPresent(Post.class))
{
isClassHasMapping=true;
classMapping="Post";
System.out.println("------------(" + classAnnotation+ ") and it is of post type-------------------");
}
Method[] methods = clazz.getDeclaredMethods();
for (Method method : methods) 
{
if (hasAnnotation(method, annotation)) {
System.out.println("  Method with @Path annotation: " + method.getName());
annotationInstance=method.getAnnotation(annotation);
if (annotationInstance != null && annotationInstance instanceof Path) 
{
Path ann = (Path) annotationInstance;
methodAnnotation=ann.value();
}
if(method.isAnnotationPresent(Forward.class))
{
System.out.println("Helllllllllllllllllllllooooooooooooooooo");
Forward anns = method.getAnnotation(Forward.class);
forwardTo=anns.value();
System.out.println("Method : " +method.getName()+" also has forward annotation : "+forwardTo);
}

if (method.isAnnotationPresent(Get.class)) 
{
isGet=true;
}
else if(method.isAnnotationPresent(Post.class))
{
isPost=true;
}
if(isGet)
{
System.out.println("Final path to kept in map is "+classAnnotation+methodAnnotation+" and it is of get type");
isGet=true;
}
else if(isPost)
{
System.out.println("Final path to kept in map is "+classAnnotation+methodAnnotation+" and it is of post type");
isPost=true;
}
else
{
if(classMapping.equals("Get"))
{
System.out.println("Final path to kept in map is "+classAnnotation+methodAnnotation+" and it is of get type");
isGet=true;
}
else if(classMapping.equals("Post"))
{
System.out.println("Final path to kept in map is "+classAnnotation+methodAnnotation+" and it is of post type");
isPost=true;
}
else
{
System.out.println("Final path to kept in map is "+classAnnotation+methodAnnotation+" and it is of default type");
}
}
path=classAnnotation+methodAnnotation;
Service service=new Service();
service.setServiceClass(clazz);
service.setPath(path);
service.setMethod(method);
service.setForwardTo(forwardTo);
if(isGet) service.setMappingType("Get");
else if(isPost)service.setMappingType("Post");
else service.setMappingType("Get");
WebRockModel.myMap.put(path,service);
System.out.println("Adding key to map : "+path+"and now map size is : "+WebRockModel.myMap.size());
isGet=false;
isPost=false;
path="";
forwardTo=null;
}
}
}

private static boolean hasAnnotation(Method method, Class annotation) 
{
return method.isAnnotationPresent(annotation);
}
}

  
