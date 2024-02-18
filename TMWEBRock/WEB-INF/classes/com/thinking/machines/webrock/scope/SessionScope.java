package com.thinking.machines.webrock.scope;
import javax.servlet.*;
import javax.servlet.http.*;
public class SessionScope
{
private HttpSession session;
public void setAttribute(String s,Object o)
{
System.out.println("setAttribute of session Scope got called");
session.setAttribute(s,o);
}
public Object getAttribute(String s)
{
return session.getAttribute(s);
}
}