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
private Map<Integer, List<Service>> startupMap = new HashMap<>();

private void addServiceToStartupMap(Service service) 
{
int priority = service.getPriority();
startupMap.putIfAbsent(priority, new ArrayList<>());
startupMap.get(priority).add(service);
}

// Method to iterate over the Map and process the Service objects
private void processServices() throws Exception
{
for (Map.Entry<Integer, List<Service>> entry : startupMap.entrySet()) 
{
int priority = entry.getKey();
List<Service> serviceList = entry.getValue();
// Process the Service objects in the list
for (Service service : serviceList) 
{
Class c=service.getServiceClass();
Method m=service.getMethod();
Class parameters[]=m.getParameterTypes();
if(parameters.length>0)
{
 throw new Exception("Parameters are not allowed exception in method : "+m.getName());
}
Class returnType=m.getReturnType();
if(returnType!=void.class)
{
throw new Exception("Return type of method "+m.getName()+" is not void"); 
}
Object instance = c.newInstance();
Object result = m.invoke(instance);
}
}
}

@Override
public void init()  
{
String packagePrefix= getServletConfig().getInitParameter("SERVICE_PACKAGE_PREFIX");
String jsFileName= getServletConfig().getInitParameter("jsFile");
checkAndCreateJSFolder(jsFileName);
try
{
List<Class> classesWithAnnotation = getClassesWithAnnotation(packagePrefix, Path.class);
for (Class clazz : classesWithAnnotation) 
{
analyzeMethodsForAnnotation(clazz, Path.class);
}
if(startupMap.size()>0) processServices();
if(WebRockModel.myMap.size()>0)
{
createJsFile(jsFileName);
}
}catch(Exception e)
{
System.out.println(e);
}
}


private void checkAndCreateJSFolder(String jsFileName) 
{
ServletContext servletContext = getServletContext();
String webInfPath = servletContext.getRealPath("/WEB-INF");
if (webInfPath != null) 
{
File webInfFolder = new File(webInfPath);
File jsFolder = new File(webInfFolder, "js");
if (!jsFolder.exists()) 
{
System.out.println("Creating 'js' folder in WEB-INF...");
boolean created = jsFolder.mkdir();
if (created) 
{
System.out.println("'js' folder created successfully.");
} 
else 
{
System.out.println("Failed to create 'js' folder.");
}
} 
else 
{
System.out.println("'js' folder already exists in WEB-INF.");
File sampleFile = new File(jsFolder, jsFileName);
// Delete the file if it exists
if (sampleFile.exists()) 
{
boolean deleted = sampleFile.delete();
if (deleted) 
{
System.out.println("Existing file deleted.");
} 
else 
{
System.out.println("Failed to delete existing file.");
return;
}
}
try 
{
boolean created = sampleFile.createNewFile();
if (created) 
{
System.out.println("File created successfully.");
} 
else 
{
System.out.println("Failed to create file.");
}
} 
catch (IOException e) 
{
System.out.println("IOException: " + e.getMessage());
}
}
} 
else 
{
System.out.println("WEB-INF folder not found.");
}
}


private  List<Class> getClassesWithAnnotation(String packagePrefix,Class annotation) throws IOException, ClassNotFoundException 
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

private  void processFileBasedClasses(String packagePrefix, File directory,List<Class> classesWithAnnotation, Class annotation) throws ClassNotFoundException 
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
private  String getClassName(String packagePrefix, File file) 
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

private  boolean isInSubpackage(Class clazz, String packagePrefix) 
{
return clazz.getPackage().getName().startsWith(packagePrefix);
}
private  boolean hasAnnotation(Class clazz, Class annotation) 
{
return clazz.isAnnotationPresent(annotation);
}
private  void analyzeMethodsForAnnotation(Class clazz, Class annotation) 
{
String classAnnotation="";
String methodAnnotation="";
String path="";
int onStartup=-1;
String forwardTo=null;
Boolean isGet=false;
Boolean isPost=false;
Boolean isClassHasMapping=false;
Boolean isSecuredAccessOnClass=false;
Boolean isSecuredAccessOnMethod=false;
String classMapping="";
Boolean isInjectApplicationDirectory=false;
Boolean isInjectApplicationScope=false;
Boolean isInjectSessionScope=false;
Boolean isInjectRequestScope=false;
Boolean isInjectRequestParameter=false;
Annotation annotationInstance = clazz.getAnnotation(annotation);
if (annotationInstance != null && annotationInstance instanceof Path) 
{
Path ann = (Path) annotationInstance;
classAnnotation=ann.value();
}
if ( clazz.isAnnotationPresent(Get.class)) 
{
isClassHasMapping=true;
classMapping="GET";
}
else if( clazz.isAnnotationPresent(Post.class))
{
isClassHasMapping=true;
classMapping="POST";
}
if(clazz.isAnnotationPresent(SecuredAccess.class))
{
isSecuredAccessOnClass=true;
}
if(clazz.isAnnotationPresent(InjectApplicationDirectory.class))
{
isInjectApplicationDirectory=true;
}
if(clazz.isAnnotationPresent(InjectApplicationScope.class))
{
isInjectApplicationScope=true;
}
if(clazz.isAnnotationPresent(InjectSessionScope.class))
{
isInjectSessionScope=true;
}
if(clazz.isAnnotationPresent(InjectRequestScope.class))
{
isInjectRequestScope=true;
}
Method[] methods = clazz.getDeclaredMethods();
for (Method method : methods) 
{
annotationInstance=method.getAnnotation(annotation);
if (annotationInstance != null && annotationInstance instanceof Path) 
{
Path ann = (Path) annotationInstance;
methodAnnotation=ann.value();
}
if(method.isAnnotationPresent(Forward.class))
{
Forward anns = method.getAnnotation(Forward.class);
forwardTo=anns.value();
}
if(method.isAnnotationPresent(OnStartup.class))
{
OnStartup annss = method.getAnnotation(OnStartup.class);
onStartup = annss.priority();
}
if(method.isAnnotationPresent(SecuredAccess.class))
{
isSecuredAccessOnMethod=true;
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
isGet=true;
}
else if(isPost)
{
isPost=true;
}
else
{
if(classMapping.equals("Get"))
{
isGet=true;
}
else if(classMapping.equals("Post"))
{
isPost=true;
}
else
{
}
}
path=classAnnotation+methodAnnotation;
Service service=new Service();
service.setServiceClass(clazz);
service.setPath(path);
service.setMethod(method);
service.setIsInjectApplicationDirectory(isInjectApplicationScope);
service.setIsInjectApplicationScope(isInjectApplicationScope);
service.setIsInjectSessionScope(isInjectSessionScope);
service.setIsInjectRequestScope(isInjectRequestScope);
service.setIsSecuredAccessOnMethod(isSecuredAccessOnMethod);
service.setIsSecuredAccessOnClass(isSecuredAccessOnClass);
if(isGet) service.setMappingType("GET");
else if(isPost)service.setMappingType("POST");
else service.setMappingType("GET");
try
{
Field[] fields = clazz.getDeclaredFields();
 // Check if MyAnnotation is applied to each field
for (Field field : fields) 
{
if (field.isAnnotationPresent(Autowired.class)) 
{
Autowired annotationss = field.getAnnotation(Autowired.class);
List<FieldValuePair> d=service.getArrayList();
service.addToArrayList(field.getType(),annotationss.name(),field.getName());
}
if(field.isAnnotationPresent(InjectRequestParameter.class))
{
isInjectRequestParameter=true;
}
}
service.setIsInjectRequestParameter(isInjectRequestParameter);
if(onStartup!=-1) 
{
service.setOnStartup(true);
service.setPriority(onStartup);
addServiceToStartupMap(service);
service.setForwardTo(null);
}
else
{
service.setForwardTo(forwardTo);
}
}catch(Exception eee)
{
System.out.println(eee);
}
if (hasAnnotation(method, annotation))  
{
WebRockModel.myMap.put(path,service);
}
isGet=false;
isPost=false;
path="";
forwardTo=null;
isSecuredAccessOnMethod=false;
onStartup=-1;
isInjectRequestParameter=false;
}
}

private  boolean hasAnnotation(Method method, Class annotation) 
{
return method.isAnnotationPresent(annotation);
}
private void createJsFile(String jsFileName)
{
ServletContext servletContext = getServletContext();
String fileName="/WEB-INF/js/"+jsFileName;
String webInfPath = servletContext.getRealPath(fileName);
try 
{
            // Create a FileWriter with the given file path
FileWriter writer = new FileWriter(webInfPath);

            // Write content to the file
writer.write("class ");
int firstTime=0;

 for (Map.Entry<String,Service> entry : WebRockModel.myMap.entrySet())
{  
Service s=entry.getValue();
String path=entry.getKey();
if(firstTime==0)
{
writer.write(s.getServiceClass().getSimpleName());
writer.write("\n");
writer.write("{\n");
firstTime=1;
}
writer.write(s.getMethod().getName());
writer.write("(formID)\n");
writer.write("{\n");
Method method=s.getMethod();
Parameter[] parameters=method.getParameters();
if (parameters.length > 0) 
{
for (Parameter parameter : parameters) 
{
Class parameterType = parameter.getType();
if (!parameterType.isPrimitive() && parameterType!=String.class) 
{
Field[] fields = parameterType.getDeclaredFields();

writer.write("if(formID)\n");
writer.write("{\n");
for (Field field : fields) 
{
writer.write("var "+field.getName()+"=$('#'+formID+'  #"+field.getName()+"\').val();\n");
}
writer.write("}\n");
writer.write("else\n");
writer.write("{\n");
fields = parameterType.getDeclaredFields();
for (Field field : fields) 
{
writer.write("var "+field.getName()+"=$('#"+field.getName()+"\').val();\n");
}
writer.write("}\n");
                        // You can add more checks or access properties of non-primitive parameters here
int first=0;
fields = parameterType.getDeclaredFields();
writer.write("var requestData={\n");
for (Field field : fields) 
{
if(first!=0)
{
writer.write(",\n");
}
first=1;                        
writer.write(field.getName());
writer.write(":");
writer.write(field.getName());
}
writer.write("\n};\n");
String g=s.getServiceClass().getSimpleName();
g = String.valueOf(g.charAt(0)).toLowerCase() + g.substring(1);

String ss="var url='/TMWEBRock/"+g+path+"';";
writer.write(ss);
writer.write("\nvar prm=new Promise(function(done,problem){\n");
writer.write("$.ajax({\n");
writer.write("url:url,\n");
writer.write("type:'");
String mappingType=s.getMappingType();
writer.write(mappingType);
writer.write("\',\n");
writer.write("data: JSON.stringify(requestData),\n");
writer.write("contentType: 'application/json',\n");
writer.write("success: function (response) {\n");
writer.write("done(response);\n");
writer.write("},\n");
writer.write("error:function(response){\n");
writer.write("problem(response);\n");
writer.write("}\n");
writer.write("});\n");
writer.write("});\n");
writer.write("return prm;\n");
} 
else 
{

//done done
String pp="";
Annotation annotationInstance = parameter.getAnnotation(RequestParameter.class);
if (annotationInstance != null && annotationInstance instanceof RequestParameter) 
{
RequestParameter ann = (RequestParameter) annotationInstance;
pp=ann.value();
}
writer.write("var prm=new Promise(function(done,problem)\n");
writer.write("{\n");
writer.write("if(formID)\n"); 
writer.write("{\n");
writer.write("var roll = $('#' + formID + ' #"+pp+"\').val();\n");
writer.write("}\n");
writer.write("else\n");
writer.write("{\n");
writer.write("var roll=document.getElementById("+pp+").value;\n");
writer.write("}\n");
writer.write("var url=encodeURIComponent(roll);\n");
String g=s.getServiceClass().getSimpleName();
g = String.valueOf(g.charAt(0)).toLowerCase() + g.substring(1);
String ss="'/TMWEBRock/"+g+path;

writer.write("$.ajax({\n");
writer.write("url:"+ss+"?"+pp+"='+url,\n");
writer.write("type:'");
String mappingType=s.getMappingType();
writer.write(mappingType);
writer.write("\',\n");

writer.write("success: function (response) {\n");
writer.write("done(response);\n");
writer.write("},\n");
writer.write("error:function(response){\n");
writer.write("problem(response);\n");
writer.write("}\n");
writer.write("});\n");
writer.write("});\n");
writer.write("return prm;\n");

}
}  //for
}  // if no parameter
else 
{
writer.write("var prm=new Promise(function(done,problem){\n");
String g=s.getServiceClass().getSimpleName();
g = String.valueOf(g.charAt(0)).toLowerCase() + g.substring(1);
String ss="'/TMWEBRock/"+g+path;
writer.write("$.ajax({\n");

writer.write("url:");
writer.write(ss);
writer.write("',\n");
writer.write("type:'");
String mappingType=s.getMappingType();
writer.write(mappingType);
writer.write("\',\n");
writer.write("success: function (response) {\n");
writer.write("done(response);\n");
writer.write("},\n");
writer.write("error:function(response){\n");
writer.write("problem(response);\n");
writer.write("}\n");
writer.write("});\n");
writer.write("});\n");
writer.write("return prm;\n");
}
writer.write("}\n");
} // main for 
writer.write("}");


            // Close the FileWriter to save changes
writer.close();

System.out.println("Content written to file: " + webInfPath);
}catch(IOException e) 
{
e.printStackTrace();
}
}
}

  
