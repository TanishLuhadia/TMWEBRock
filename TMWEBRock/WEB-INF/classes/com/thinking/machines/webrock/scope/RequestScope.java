package com.thinking.machines.webrock.scope;
import javax.servlet.*;
import javax.servlet.http.*;
public class RequestScope
{
private HttpServletRequest request;
public void setAttribute(String s,Object o)
{
request.setAttribute(s,o);
}
public Object getAttribute(String s)
{
return request.getAttribute(s);
}
}