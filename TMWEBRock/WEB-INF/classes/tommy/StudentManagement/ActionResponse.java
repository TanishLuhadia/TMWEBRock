package tommy.StudentManagement;
public class ActionResponse
{
private Boolean successful;
private String exception;
private Object result;
public ActionResponse()
{
this.successful=false;
this.exception="";
this.result=null;
}
public void setSuccessful(Boolean successful)
{
this.successful=successful;
}
public Boolean getSuccessful()
{
return this.successful;
}

public void setException(String exception)
{
this.exception=exception;
}
public String getException()
{
return this.exception;
}
public void setResult(Object result)
{
this.result=result;
}
public Object  getResult()
{
return this.result;
}
}