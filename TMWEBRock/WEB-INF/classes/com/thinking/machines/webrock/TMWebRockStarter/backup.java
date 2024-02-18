package com.thinking.machines.webrock.TMWebRockStarter;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import com.thinking.machines.webrock.annotations.*;
import java.net.URL;
public class StartupServlet extends HttpServlet
{
@Override
public void init() throws ServletException 
{
String packagePrefix= getServletConfig().getInitParameter("SERVICE_PACKAGE_PREFIX");
try
{
List<Class<?>> classesWithAnnotation = getClassesWithAnnotation(packagePrefix, Path.class);
System.out.println("Hello");
System.out.println(classesWithAnnotation.size());
for (Class<?> clazz : classesWithAnnotation) {
System.out.println("Class with @Path annotation: " + clazz.getName());
analyzeMethodsForAnnotation(clazz, Path.class);
}
}catch(Exception e)
{
System.out.println(e);
}
}


private static List<Class<?>> getClassesWithAnnotation(String packagePrefix,Class<? extends Annotation> annotation) throws IOException, ClassNotFoundException 
{
 List<Class<?>> classesWithAnnotation = new ArrayList<>();
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

private static void processFileBasedClasses(String packagePrefix, File directory,
            List<Class<?>> classesWithAnnotation, Class<? extends Annotation> annotation)
            throws ClassNotFoundException 
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
Class<?> clazz = Class.forName(className);
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

private static boolean isInSubpackage(Class<?> clazz, String packagePrefix) 
{
return clazz.getPackage().getName().startsWith(packagePrefix);
}

private static boolean hasAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) 
{
return clazz.isAnnotationPresent(annotation);
}

private static void analyzeMethodsForAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) 
{
Method[] methods = clazz.getDeclaredMethods();
for (Method method : methods) 
{
            if (hasAnnotation(method, annotation)) {
                System.out.println("  Method with @Path annotation: " + method.getName());
            }
        }
    }

    private static boolean hasAnnotation(Method method, Class<? extends Annotation> annotation) {
        return method.isAnnotationPresent(annotation);
    }
}

  
