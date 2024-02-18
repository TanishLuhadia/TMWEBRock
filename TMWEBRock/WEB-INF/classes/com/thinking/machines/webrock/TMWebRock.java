package com.thinking.machines.webrock;
import java.util.*;
import java.io.*;
import com.google.gson.*;
import com.thinking.machines.webrock.pojo.*;
import com.thinking.machines.webrock.model.*;
import com.thinking.machines.webrock.scope.*;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.Exceptions.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.reflect.*;
public class TMWebRock extends HttpServlet
{

// Method to handle request forwarding

private void isSecuredAccess(Service s,HttpServletResponse response,HttpServletRequest request)
{
Method guardMethod=null;
Object result=null;
try
{
if(s.getIsSecuredAccessOnMethod())
{
Method m=s.getMethod();
if (m.isAnnotationPresent(SecuredAccess.class)) 
{
SecuredAccess securedAccessAnnotation = m.getAnnotation(SecuredAccess.class);
String checkPostValue = securedAccessAnnotation.checkPost();
String guardValue = securedAccessAnnotation.guard();
Class checkPostClass = Class.forName(checkPostValue);
Object instance = checkPostClass.newInstance();
Method[] methods = checkPostClass.getDeclaredMethods();
isInject(checkPostClass,instance,request);
for (Method method : methods) 
{
if (method.getName().equals(guardValue)) 
{
guardMethod = method;
break;
}
}
if (guardMethod == null) 
{
response.sendError(HttpServletResponse.SC_NOT_FOUND);
return;
} 
else 
{
Parameter p[]=guardMethod.getParameters();
if(p.length<=0)
{
try
{
result = guardMethod.invoke(instance);
}catch(Exception eee)
{
response.sendError(HttpServletResponse.SC_NOT_FOUND);
return;

}

}
else
{
List<Object> parameterValues=processMethodValuesForSecuredAccess(request,instance,m);
try
{
result = guardMethod.invoke(instance,parameterValues.toArray());
}catch(Exception e)
{
response.sendError(HttpServletResponse.SC_NOT_FOUND);
return;

}



}
}








}
}
if(s.getIsSecuredAccessOnClass())
{

}
}catch(ClassNotFoundException |  IllegalAccessException | InstantiationException | IOException e)
{
System.out.println(e);
}
}

private void handleForward(Service service, HttpServletRequest request, HttpServletResponse response) 
{
try
{
String forwardTo = service.getForwardTo();	
while (forwardTo != null) 
{
Service forwardService = WebRockModel.myMap.get(forwardTo);
if (forwardService != null) 
{
Method forwardMethod = forwardService.getMethod();
Class forwardClass = forwardService.getServiceClass();
String forwardMapping = forwardService.getMappingType();
Object forwardInstance = forwardClass.newInstance();
List<FieldValuePair> d=forwardService.getArrayList();
processFieldValues(d,request,forwardInstance,forwardService,forwardClass); 
if(forwardService.getIsInjectApplicationDirectory() || forwardService.getIsInjectApplicationScope() || forwardService.getIsInjectSessionScope() || forwardService.getIsInjectRequestScope() || forwardService.getIsInjectRequestParameter())
{
inject(forwardService,forwardClass,forwardInstance,request);
}
isSecuredAccess(forwardService,response,request);
Object forwardResult=null;
if(isJSONInRequest(request))
{
List<Object> pv=null;
pv=checkJsonAndPrepareIt(request,forwardInstance,forwardService,forwardMethod);
try
{
forwardResult = forwardMethod.invoke(forwardInstance,pv.toArray());
}catch (InvocationTargetException e) 
{
Throwable originalException = e.getCause();
System.out.println(originalException);
return;
}
}
else
{
List<Object> parameterValues=processMethodValues(request,forwardInstance,forwardService,forwardMethod);
try
{
forwardResult = forwardMethod.invoke(forwardInstance,parameterValues.toArray());
}catch (InvocationTargetException e) 
{
Throwable originalException = e.getCause();
System.out.println(originalException);
return;
}
}

if(forwardResult!=null)
{
String resultJson = new Gson().toJson(forwardResult);
response.setContentType("application/json");
response.getWriter().write(resultJson);
}

// Update forwardTo for the next iteration
forwardTo = forwardService.getForwardTo();
 // Perform any additional logic if needed
}
else
{
RequestDispatcher  requestDispatcher;
requestDispatcher=request.getRequestDispatcher(forwardTo);
requestDispatcher.forward(request,response);
break;
}
}
}catch (ServiceException se) 
{
System.out.println("Service Exception : "+se);
try
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
}catch(IOException io)
{
}
return;
}catch (Exception e) 
{
System.out.println(e);
}
}

public Boolean isJSONInRequest(HttpServletRequest request)
{
String contentType = request.getContentType();
if (contentType != null && contentType.startsWith("application/json"))  return true;

return false;
}



public void doPost(HttpServletRequest request, HttpServletResponse response)
{
try
{

System.out.println("Great doPost go invoked");
String requestUrl = request.getRequestURL().toString();
String contextPath = request.getContextPath();
String pathInfo = requestUrl.substring(requestUrl.indexOf(contextPath) + contextPath.length());
int firstSlashIndex = pathInfo.indexOf('/');
int secondSlashIndex = pathInfo.indexOf('/', firstSlashIndex + 1);
requestUrl = pathInfo.substring(secondSlashIndex);
Service s=WebRockModel.myMap.get(requestUrl);
Method m=s.getMethod();
Class c=s.getServiceClass();
String mapping=s.getMappingType();
Object instance = c.newInstance();
List<FieldValuePair> d=s.getArrayList();
processFieldValues(d,request,instance,s,c);
if(mapping.equals("GET"))
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
return;
}
if(s.getIsInjectApplicationDirectory() || s.getIsInjectApplicationScope() || s.getIsInjectSessionScope() || s.getIsInjectRequestScope() || s.getIsInjectRequestParameter())
{
inject(s,c,instance,request);
}
isSecuredAccess(s,response,request);
Object result=null;
if(isJSONInRequest(request))
{
List<Object> pv=null;
pv=checkJsonAndPrepareIt(request,instance,s,m);
try
{
result = m.invoke(instance,pv.toArray());
}catch (InvocationTargetException e) 
{
Throwable originalException = e.getCause();
System.out.println(originalException);
return;
}
}
else
{
List<Object> parameterValues=processMethodValues(request,instance,s,m);
try
{
result = m.invoke(instance,parameterValues.toArray());
}catch (InvocationTargetException e) 
{
Throwable originalException = e.getCause();
System.out.println(originalException);
return;
}
}
System.out.println("Method result: " + result);
if(result!=null)
{
String resultJson = new Gson().toJson(result);
response.setContentType("application/json");
response.getWriter().write(resultJson);
}

handleForward(s,request,response);
}catch (ServiceException se) 
{
System.out.println("Service Exception : "+se);
try
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
}catch(IOException io)
{
}
return;
}catch(Exception e)
{
System.out.println("DO post exception "+ e.getMessage());
}
}

public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{

System.out.println("Great doGet got invoked");
String requestUrl = request.getRequestURL().toString();
String contextPath = request.getContextPath();
String pathInfo = requestUrl.substring(requestUrl.indexOf(contextPath) + contextPath.length());
int firstSlashIndex = pathInfo.indexOf('/');
int secondSlashIndex = pathInfo.indexOf('/', firstSlashIndex + 1);
requestUrl = pathInfo.substring(secondSlashIndex);
Service s=WebRockModel.myMap.get(requestUrl);
Method m=s.getMethod();
Class c=s.getServiceClass();
String mapping=s.getMappingType();
Object instance = c.newInstance();
List<FieldValuePair> d=s.getArrayList();
processFieldValues(d,request,instance,s,c);
if(mapping.equals("POST"))
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
return;
}
if(s.getIsInjectApplicationDirectory() || s.getIsInjectApplicationScope() || s.getIsInjectSessionScope() || s.getIsInjectRequestScope() || s.getIsInjectRequestParameter())
{
inject(s,c,instance,request);
}
isSecuredAccess(s,response,request);
Object result=null;
if(isJSONInRequest(request))
{
List<Object> pv=null;
pv=checkJsonAndPrepareIt(request,instance,s,m);
try
{
result = m.invoke(instance,pv.toArray());
}catch (InvocationTargetException e) 
{
Throwable originalException = e.getCause();
System.out.println(originalException);
return;
}

}
else
{
List<Object> parameterValues=processMethodValues(request,instance,s,m);
try
{
result = m.invoke(instance,parameterValues.toArray());
}catch (InvocationTargetException e) 
{
Throwable originalException = e.getCause();
System.out.println(originalException);
return;
}
}
System.out.println("Method result: " + result);
if(result!=null)
{
String resultJson = new Gson().toJson(result);
response.setContentType("application/json");
response.getWriter().write(resultJson);
}
handleForward(s,request,response);
}catch (ServiceException se) 
{
System.out.println("Service Exception : "+se);
try
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
}catch(IOException io)
{
}
return;
}catch(Exception e)
{
System.out.println(e);
}
}



private void inject(Service s,Class c,Object instance,HttpServletRequest request)
{
try
{
Field fields[];
fields=c.getDeclaredFields();
Field f;
String fieldName;
Class fieldType;
Object o=null;
for(int e=0;e<fields.length;e++)
{
f=fields[e];
fieldName=f.getName();
fieldType=f.getType();
if(fieldType==ApplicationDirectory.class &&  s.getIsInjectApplicationDirectory())
{
ServletContext servletContext = getServletContext();
File file=new File(servletContext.getRealPath("/"));
String setterMethodName=generateSetterMethodName(fieldName);
ApplicationDirectory applicationDirectory;
applicationDirectory=new ApplicationDirectory(file);
try
{
Method setApplicationDirectoryMethod = c.getDeclaredMethod(setterMethodName,ApplicationDirectory.class);
setApplicationDirectoryMethod.setAccessible(true);
setApplicationDirectoryMethod.invoke(instance, applicationDirectory);
}catch(Exception ee)
{
System.out.println("Some exception");
System.out.println(ee);
}
} else if(fieldType==ApplicationScope.class &&  s.getIsInjectApplicationScope())
{
String setterMethodName=generateSetterMethodName(fieldName);
ApplicationScope applicationScope;
applicationScope=new ApplicationScope();
ServletContext application = getServletContext();
Class appScope = ApplicationScope.class;
Field field = appScope.getDeclaredField("servletContext");
field.setAccessible(true);
field.set(applicationScope, application);
System.out.println("nice you initialize a applicationScope");
try
{
Method setApplicationScopeMethod = c.getDeclaredMethod(setterMethodName, ApplicationScope.class);
setApplicationScopeMethod.setAccessible(true);
setApplicationScopeMethod.invoke(instance, applicationScope);
}catch(Exception ee)
{
System.out.println(ee);
}
}else if(fieldType==SessionScope.class &&  s.getIsInjectSessionScope())
{
String setterMethodName=generateSetterMethodName(fieldName);
SessionScope sessionScope;
sessionScope=new SessionScope();
HttpSession session;
session=request.getSession();
Class sesScope = SessionScope.class;
Field field = sesScope.getDeclaredField("session");
field.setAccessible(true);
field.set(sessionScope, session);
try
{
Method setSessionScopeMethod = c.getDeclaredMethod(setterMethodName, SessionScope.class);
setSessionScopeMethod.setAccessible(true);
setSessionScopeMethod.invoke(instance, sessionScope);
}catch(Exception ee)
{
System.out.println(ee);
}
}else if(fieldType==RequestScope.class &&  s.getIsInjectRequestScope())
{
String setterMethodName=generateSetterMethodName(fieldName);
RequestScope requestScope;
requestScope=new RequestScope();
Class reqScope = RequestScope.class;
Field field = reqScope.getDeclaredField("request");
field.setAccessible(true);
field.set(requestScope, request);
try
{
Method setRequestScopeMethod = c.getDeclaredMethod(setterMethodName, RequestScope.class);
setRequestScopeMethod.setAccessible(true);
setRequestScopeMethod.invoke(instance, requestScope);
}catch(Exception ee)
{
System.out.println(ee);
}
}
else
{
if(s.getIsInjectRequestParameter())
{
InjectRequestParameter irp = f.getAnnotation(InjectRequestParameter.class);
String annotationValue = irp.value();
String setterMethodName=generateSetterMethodName(fieldName);
Method setRequestMethod = c.getDeclaredMethod(setterMethodName,fieldType);
Parameter []p=setRequestMethod.getParameters();
if(p.length<0) throw new Exception("Wrong setter getter method");
for(Parameter pp:p)
{
Class targetType=pp.getType();
if (targetType.equals(String.class)) 
{
String result=request.getParameter(annotationValue);
setRequestMethod.setAccessible(true);
setRequestMethod.invoke(instance,result);
}
else if (targetType.equals(int.class) || targetType.equals(Integer.class)) 
{
int result= Integer.parseInt(request.getParameter(annotationValue));
setRequestMethod.setAccessible(true);
setRequestMethod.invoke(instance,result);
} else if (targetType.equals(double.class) || targetType.equals(Double.class)) 
{
Double result= Double.parseDouble(request.getParameter(annotationValue));
setRequestMethod.setAccessible(true);
setRequestMethod.invoke(instance,result);
}else if(targetType.equals(long.class) || targetType.equals(Long.class))
{
Long result= Long.parseLong(request.getParameter(annotationValue));
setRequestMethod.setAccessible(true);
setRequestMethod.invoke(instance,result);
}else if(targetType.equals(short.class) || targetType.equals(Short.class))
{
Short result= Short.parseShort(request.getParameter(annotationValue));
setRequestMethod.setAccessible(true);
setRequestMethod.invoke(instance,result);
}else if(targetType.equals(boolean.class) || targetType.equals(Boolean.class))
{
Boolean result=Boolean.parseBoolean(request.getParameter(annotationValue));
setRequestMethod.setAccessible(true);
setRequestMethod.invoke(instance,result);
}else if(targetType.equals(float.class) || targetType.equals(Float.class))
{
Float result= Float.parseFloat(request.getParameter(annotationValue));
setRequestMethod.setAccessible(true);
setRequestMethod.invoke(instance,result);
}
else if(targetType.equals(char.class) || targetType.equals(Character.class))
{
String parameterValue = request.getParameter(annotationValue);
char result = parameterValue.charAt(0);
setRequestMethod.setAccessible(true);
setRequestMethod.invoke(instance,result);
}
else if(targetType.equals(byte.class) || targetType.equals(Byte.class))
{
Byte result= Byte.parseByte(request.getParameter(annotationValue));
setRequestMethod.setAccessible(true);
setRequestMethod.invoke(instance,result);
}
else
{
o= request.getParameter(annotationValue);
setRequestMethod.setAccessible(true);
setRequestMethod.invoke(instance,o);
}
}
}
}
}
}catch(Exception exception)
{
System.out.println("Exception in inject method : "+exception);
}
}
public static String capitalizeFirstLetter(String input) 
{
if (input == null || input.isEmpty()) 
{
return input;
}
return input.substring(0, 1).toUpperCase() + input.substring(1);
}
public static String generateSetterMethodName(String fieldName) 
{
if (fieldName == null || fieldName.isEmpty()) 
{
return fieldName;
}
return "set" + capitalizeFirstLetter(fieldName);
}

public void processFieldValues(List<FieldValuePair> d, HttpServletRequest request,Object instance,Service service,Class c) 
{
try
{
if(d.size()>0)
{
HttpSession session;
session=request.getSession();
ServletContext servletContext = getServletContext();

for(int i=0;i<d.size();i++)
{
FieldValuePair f=d.get(i);
Class fieldType=f.getFieldType();
Object o=null;
if(request.getAttribute(f.getValue())!=null &&  fieldType.isInstance(request.getAttribute(f.getValue())))
{
o=request.getAttribute(f.getValue());
String setterMethodName=generateSetterMethodName(f.getFieldName());
Method setRequestMethod = c.getDeclaredMethod(setterMethodName,fieldType);
setRequestMethod.setAccessible(true);
setRequestMethod.invoke(instance,o);
}
else if(session.getAttribute(f.getValue())!=null &&  fieldType.isInstance(session.getAttribute(f.getValue())))
{
o=session.getAttribute(f.getValue());
String setterMethodName=generateSetterMethodName(f.getFieldName());
Method setSessionMethod = c.getDeclaredMethod(setterMethodName,fieldType);
setSessionMethod.setAccessible(true);
setSessionMethod.invoke(instance,o);
}
else if(servletContext.getAttribute(f.getValue())!=null &&  fieldType.isInstance(servletContext.getAttribute(f.getValue())))
{
o=servletContext.getAttribute(f.getValue());
String setterMethodName=generateSetterMethodName(f.getFieldName());
Method setApplicationMethod = c.getDeclaredMethod(setterMethodName,fieldType);
setApplicationMethod.setAccessible(true);
setApplicationMethod.invoke(instance,o);
}
else
{
String setterMethodName=generateSetterMethodName(f.getFieldName());
Method setApplicationMethod = c.getDeclaredMethod(setterMethodName,fieldType);
setApplicationMethod.setAccessible(true);
setApplicationMethod.invoke(instance,o);
}
}
}
}catch(Exception e)
{
System.out.println("Exception in processFieldValues : "+e);
}
}
public List<Object> processMethodValues(HttpServletRequest request,Object intance,Service service,Method m)
{
Parameter[] parameters = m.getParameters();
List<Object> parameterValues = new ArrayList<>();
ApplicationScope applicationScope=null;
RequestScope requestScope=null;
ApplicationDirectory applicationDirectory=null;
SessionScope sessionScope=null;

for (Parameter parameter : parameters) 
{
System.out.println("Hello");
try
{
if (parameter.isAnnotationPresent(RequestParameter.class)) 
{
RequestParameter requestParameterAnnotation = parameter.getAnnotation(RequestParameter.class);
String paramName = requestParameterAnnotation.value();
String paramValue = request.getParameter(paramName);
Object convertedValue = convertToType(paramValue, parameter.getType());
parameterValues.add(convertedValue);
}else if (ApplicationScope.class.isAssignableFrom(parameter.getType())) 
{
if(applicationScope==null) 
{
applicationScope=new ApplicationScope();
ServletContext application = getServletContext();
Class appScope = ApplicationScope.class;
Field field = appScope.getDeclaredField("servletContext");
field.setAccessible(true);
field.set(applicationScope, application);
}
parameterValues.add(applicationScope);
}else if (RequestScope.class.isAssignableFrom(parameter.getType()))
{
if(sessionScope==null)
{
sessionScope=new SessionScope();
HttpSession session;
session=request.getSession();
Class sesScope = SessionScope.class;
Field field = sesScope.getDeclaredField("session");
field.setAccessible(true);
field.set(sessionScope, session);
}
parameterValues.add(sessionScope);
}else if (RequestScope.class.isAssignableFrom(parameter.getType())) 
 {
if(requestScope==null)
{
requestScope=new RequestScope();
Class reqScope = RequestScope.class;
Field field = reqScope.getDeclaredField("request");
field.setAccessible(true);
field.set(requestScope, request);
}
parameterValues.add(requestScope);
}else if (ApplicationDirectory.class.isAssignableFrom(parameter.getType()))
{
if(applicationDirectory==null)
{
ServletContext servletContext = getServletContext();
File file=new File(servletContext.getRealPath("/"));
applicationDirectory=new ApplicationDirectory(file);
}
parameterValues.add(applicationDirectory);
} 
else
{
// do nothing
}
}catch(Exception e)
{
System.out.println(e);
}
}
return parameterValues;
}

public List<Object> checkJsonAndPrepareIt(HttpServletRequest request,Object intance,Service service,Method m) throws ServiceException
{
Parameter[] parameters = m.getParameters();
List<Object> parameterValues = new ArrayList<>();
ApplicationScope applicationScope=null;
RequestScope requestScope=null;
ApplicationDirectory applicationDirectory=null;
SessionScope sessionScope=null;
for (Parameter parameter : parameters) 
{
try
{
if (ApplicationScope.class.isAssignableFrom(parameter.getType())) 
{
if(applicationScope==null) 
{
applicationScope=new ApplicationScope();
ServletContext application = getServletContext();
Class appScope = ApplicationScope.class;
Field field = appScope.getDeclaredField("servletContext");
field.setAccessible(true);
field.set(applicationScope, application);
}
parameterValues.add(applicationScope);
}else if (RequestScope.class.isAssignableFrom(parameter.getType()))
{
if(sessionScope==null)
{
sessionScope=new SessionScope();
HttpSession session;
session=request.getSession();
Class sesScope = SessionScope.class;
Field field = sesScope.getDeclaredField("session");
field.setAccessible(true);
field.set(sessionScope, session);
}
parameterValues.add(sessionScope);
}else if (RequestScope.class.isAssignableFrom(parameter.getType())) 
 {
if(requestScope==null)
{
requestScope=new RequestScope();
Class reqScope = RequestScope.class;
Field field = reqScope.getDeclaredField("request");
field.setAccessible(true);
field.set(requestScope, request);
}
parameterValues.add(requestScope);

}else if (ApplicationDirectory.class.isAssignableFrom(parameter.getType()))
{
if(applicationDirectory==null)
{
ServletContext servletContext = getServletContext();
File file=new File(servletContext.getRealPath("/"));
applicationDirectory=new ApplicationDirectory(file);
}
parameterValues.add(applicationDirectory);
} 
else 
{
// this means json in request
try
{
BufferedReader br=request.getReader();
StringBuffer b=new StringBuffer();
String d;
while(true)
{
d=br.readLine();
if(d==null) break;
b.append(d);
}
String rawData=b.toString();
Gson gson=new Gson();
Class parameterType = parameter.getType();
if (parameterType.isPrimitive() || parameterType==String.class) 
{
throw new ServiceException("Some another parameter");
}
Object parameterObject = gson.fromJson(rawData, parameter.getType());
parameterValues.add(parameterObject);
}catch(ServiceException s)
{
throw new ServiceException(s.getMessage());
}catch(Exception e)
{
throw new ServiceException("Some problem");
}
}
}catch(ServiceException ee)
{
throw new ServiceException(ee.getMessage());
}catch(Exception e)
{
System.out.println(e);
}
}
return parameterValues;
}




public List<Object> processMethodValuesForSecuredAccess(HttpServletRequest request,Object intance,Method m) 
{
Parameter[] parameters = m.getParameters();
List<Object> parameterValues = new ArrayList<>();
ApplicationScope applicationScope=null;
RequestScope requestScope=null;
ApplicationDirectory applicationDirectory=null;
SessionScope sessionScope=null;

for (Parameter parameter : parameters) 
{
System.out.println("(SECUREDACCESS)  Hello");
try
{
if (ApplicationScope.class.isAssignableFrom(parameter.getType())) 
{
if(applicationScope==null) 
{
applicationScope=new ApplicationScope();
ServletContext application = getServletContext();
Class appScope = ApplicationScope.class;
Field field = appScope.getDeclaredField("servletContext");
field.setAccessible(true);
field.set(applicationScope, application);
}
parameterValues.add(applicationScope);
}else if (RequestScope.class.isAssignableFrom(parameter.getType()))
{
if(sessionScope==null)
{
sessionScope=new SessionScope();
HttpSession session;
session=request.getSession();
Class sesScope = SessionScope.class;
Field field = sesScope.getDeclaredField("session");
field.setAccessible(true);
field.set(sessionScope, session);
}
parameterValues.add(sessionScope);
}else if (RequestScope.class.isAssignableFrom(parameter.getType())) 
 {
if(requestScope==null)
{
requestScope=new RequestScope();
Class reqScope = RequestScope.class;
Field field = reqScope.getDeclaredField("request");
field.setAccessible(true);
field.set(requestScope, request);
}
parameterValues.add(requestScope);

}else if (ApplicationDirectory.class.isAssignableFrom(parameter.getType()))
{
if(applicationDirectory==null)
{
ServletContext servletContext = getServletContext();
File file=new File(servletContext.getRealPath("/"));
applicationDirectory=new ApplicationDirectory(file);
}
parameterValues.add(applicationDirectory);
} 
else
{
// do nothing
}
}catch(Exception e)
{
System.out.println(e);
}
}
return parameterValues;
}
private void isInject(Class c,Object instance,HttpServletRequest request)
{
try
{
Field fields[];
fields=c.getDeclaredFields();
Field f;
String fieldName;
Class fieldType;
Object o=null;
for(int e=0;e<fields.length;e++)
{
f=fields[e];
fieldName=f.getName();
fieldType=f.getType();
if(fieldType==ApplicationDirectory.class &&  c.isAnnotationPresent(InjectApplicationDirectory.class))
{
ServletContext servletContext = getServletContext();
File file=new File(servletContext.getRealPath("/"));
String setterMethodName=generateSetterMethodName(fieldName);
ApplicationDirectory applicationDirectory;
applicationDirectory=new ApplicationDirectory(file);
try
{
Method setApplicationDirectoryMethod = c.getDeclaredMethod(setterMethodName,ApplicationDirectory.class);
setApplicationDirectoryMethod.setAccessible(true);
setApplicationDirectoryMethod.invoke(instance, applicationDirectory);
}catch(Exception ee)
{
System.out.println("(ISINJECT) Some exception");
System.out.println(ee);
}
} else if(fieldType==ApplicationScope.class &&  c.isAnnotationPresent(InjectApplicationScope.class))
{
String setterMethodName=generateSetterMethodName(fieldName);
ApplicationScope applicationScope;
applicationScope=new ApplicationScope();
ServletContext application = getServletContext();
Class appScope = ApplicationScope.class;
Field field = appScope.getDeclaredField("servletContext");
field.setAccessible(true);
field.set(applicationScope, application);
try
{
Method setApplicationScopeMethod = c.getDeclaredMethod(setterMethodName, ApplicationScope.class);
setApplicationScopeMethod.setAccessible(true);
setApplicationScopeMethod.invoke(instance, applicationScope);
}catch(Exception ee)
{
System.out.println(ee);
}
}else if(fieldType==SessionScope.class &&  c.isAnnotationPresent(InjectSessionScope.class))
{
String setterMethodName=generateSetterMethodName(fieldName);
SessionScope sessionScope;
sessionScope=new SessionScope();
HttpSession session;
session=request.getSession();
Class sesScope = SessionScope.class;
Field field = sesScope.getDeclaredField("session");
field.setAccessible(true);
field.set(sessionScope, session);
try
{
Method setSessionScopeMethod = c.getDeclaredMethod(setterMethodName, SessionScope.class);
setSessionScopeMethod.setAccessible(true);
setSessionScopeMethod.invoke(instance, sessionScope);
}catch(Exception ee)
{
System.out.println(ee);
}
}else if(fieldType==RequestScope.class &&  c.isAnnotationPresent(InjectRequestScope.class))
{
String setterMethodName=generateSetterMethodName(fieldName);
RequestScope requestScope;
requestScope=new RequestScope();
Class reqScope = RequestScope.class;
Field field = reqScope.getDeclaredField("request");
field.setAccessible(true);
field.set(requestScope, request);
try
{
Method setRequestScopeMethod = c.getDeclaredMethod(setterMethodName, RequestScope.class);
setRequestScopeMethod.setAccessible(true);
setRequestScopeMethod.invoke(instance, requestScope);
}catch(Exception ee)
{
System.out.println(ee);
}
}
else
{
// do nothing
}
}
}catch(Exception exception)
{
System.out.println("(ISINJECT) Exception in inject method : "+exception);
}
}


// Utility method to convert string value to the specified type
private Object convertToType(String value, Class targetType)
 {
if (targetType.equals(String.class)) 
{
return value;
}
else if (targetType.equals(long.class) || targetType.equals(Long.class)) 
{
return Long.parseLong(value);
} else if (targetType.equals(int.class) || targetType.equals(Integer.class)) 
{
return Integer.parseInt(value);
}else if (targetType.equals(short.class) || targetType.equals(Short.class)) 
{
return Short.parseShort(value);
}else if (targetType.equals(byte.class) || targetType.equals(Byte.class)) 
{
return Byte.parseByte(value);
}else if (targetType.equals(double.class) || targetType.equals(Double.class)) 
{
return Double.parseDouble(value);
}else if (targetType.equals(float.class) || targetType.equals(Float.class)) 
{
return Float.parseFloat(value);
}else if (targetType.equals(char.class) || targetType.equals(Character.class)) 
{
return value.charAt(0);
}else if (targetType.equals(boolean.class) || targetType.equals(Boolean.class)) 
{
return Boolean.parseBoolean(value);
}
 // Default case (no conversion)
return value;
}
}