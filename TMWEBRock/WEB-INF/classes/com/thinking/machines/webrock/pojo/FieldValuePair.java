package com.thinking.machines.webrock.pojo;
import java.lang.reflect.*;
public  class FieldValuePair 
{
private Class fieldType;
private String value;
private String fieldName;
public FieldValuePair(Class fieldType, String value,String f) 
{
this.fieldType = fieldType;
this.value = value;
this.fieldName=f;
}
public Class getFieldType()
{
return this.fieldType;
}
public String getValue()
{
return this.value;
}
public String getFieldName()
{
return this.fieldName;
}
}
