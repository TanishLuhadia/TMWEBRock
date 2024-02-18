package com.thinking.machines.webrock.scope;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
public class ApplicationDirectory
{
private File directory;
public ApplicationDirectory(File file)
{
this.directory=file;
}
public File getDirectory()
{
return this.directory;
}
} 
