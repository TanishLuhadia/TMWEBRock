package com.thinking.machines.webrock.scope;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
public class ApplicationScope
{
private ServletContext servletContext;
public void setAttribute(String s,Object o)
{
servletContext.setAttribute(s,o);
}
public Object getAttribute(String s)
{
return servletContext.getAttribute(s);
}
}