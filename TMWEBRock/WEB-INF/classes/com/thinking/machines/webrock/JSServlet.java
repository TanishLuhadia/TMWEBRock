package com.thinking.machines.webrock;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
public class JSServlet extends HttpServlet 
{
public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException 
{
processRequest(request, response);
}
public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException 
{
processRequest(request, response);
}

private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException 
{
String fileName = request.getParameter("name");
String filePath = getServletContext().getRealPath("/WEB-INF/js/" + fileName+".js");
File file = new File(filePath);
if (file.exists() && file.isFile()) 
{
try (BufferedReader reader = new BufferedReader(new FileReader(file));
PrintWriter out = response.getWriter()) 
{
String line;
while ((line = reader.readLine()) != null) 
{
out.println(line);
}
} catch (IOException e) 
{
response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
e.printStackTrace(); // Log the exception
}
} 
else 
{
response.setStatus(HttpServletResponse.SC_NOT_FOUND);
response.getWriter().println("File not found");
}
}
}