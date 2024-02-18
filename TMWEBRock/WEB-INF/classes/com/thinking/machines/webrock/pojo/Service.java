package com.thinking.machines.webrock.pojo;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;
public class Service 
{
private Class serviceClass;
private String path;
private Method service;
private String forwardTo;
private String mappingType;
private Boolean onStartup;
private int priority;
private Boolean isInjectApplicationDirectory;
private Boolean isInjectSessionScope;
private Boolean isInjectApplicationScope;
private Boolean isInjectRequestScope;
private Boolean isInjectRequestParameter;
private Boolean isSecuredAccessOnMethod;
private Boolean isSecuredAccessOnClass;
private List<FieldValuePair> fieldList;
public Service()
{
this.serviceClass=null;
this.path="";
this.service=null;
this.mappingType="";
this.forwardTo=null;
this.onStartup=false;
this.priority=0;
this.isInjectRequestParameter=false;
this.fieldList = new ArrayList<>();
}
public void addToArrayList(Class field,String value,String f)
{
this.fieldList.add(new FieldValuePair(field,value,f));
}
public List<FieldValuePair> getArrayList()
{
return this.fieldList;
}
public void setServiceClass(Class s)
{
this.serviceClass=s;
}
public Class getServiceClass()
{
return this.serviceClass;
}
public void setPath(String path)
{
this.path=path;
}
public String getPath()
{
return this.path;
}
public void setMethod(Method m)
{
this.service=m;
}
public Method getMethod()
{
return this.service;
}
public void setMappingType(String mappingType)
{
this.mappingType=mappingType;
}
public String getMappingType()
{
return this.mappingType;
}
public void setForwardTo(String forwardTo)
{
this.forwardTo=forwardTo;
}
public String getForwardTo()
{
return this.forwardTo;
}
public void setOnStartup(Boolean onStartup)
{
this.onStartup=onStartup;
}
public Boolean getOnStartup()
{
return this.onStartup;
}
public void setPriority(int priority)
{
this.priority=priority;
}
public int getPriority()
{
return this.priority;
}
public void setIsInjectApplicationDirectory(Boolean isInjectApplicationDirectory)
{
this.isInjectApplicationDirectory=isInjectApplicationDirectory;
}
public Boolean getIsInjectApplicationDirectory()
{
return this.isInjectApplicationDirectory;
}
public void setIsInjectApplicationScope(Boolean isInjectApplicationScope)
{
this.isInjectApplicationScope=isInjectApplicationScope;
}
public Boolean getIsInjectApplicationScope()
{
return this.isInjectApplicationScope;
}
public void setIsInjectSessionScope(Boolean isInjectSessionScope)
{
this.isInjectSessionScope=isInjectSessionScope;
}
public Boolean getIsInjectSessionScope()
{
return this.isInjectSessionScope;
}

public void setIsInjectRequestScope(Boolean isInjectRequestScope)
{
this.isInjectRequestScope=isInjectRequestScope;
}
public Boolean getIsInjectRequestScope()
{
return this.isInjectRequestScope;
}

public void setIsInjectRequestParameter(Boolean isInjectRequestParameter)
{
this.isInjectRequestParameter=isInjectRequestParameter;
}
public Boolean getIsInjectRequestParameter()
{
return this.isInjectRequestParameter;
}
public void setIsSecuredAccessOnClass(Boolean isSecuredAccessOnClass)
{
this.isSecuredAccessOnClass=isSecuredAccessOnClass;
}
public Boolean getIsSecuredAccessOnClass()
{
return this.isSecuredAccessOnClass;
}

public void setIsSecuredAccessOnMethod(Boolean isSecuredAccessOnMethod)
{
this.isSecuredAccessOnMethod=isSecuredAccessOnMethod;
}
public Boolean getIsSecuredAccessOnMethod()
{
return this.isSecuredAccessOnMethod;
}
}