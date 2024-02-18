package tommy.StudentManagement;
import java.sql.*;
import java.util.*;
import java.math.*;
import tommy.StudentManagement.*;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.scope.*;
@Path("/student")
public class StudentService
{
@Path("/getAll")
public List<Student>getAll() throws Exception
{
List<Student> students;
students=new LinkedList<>();
try
{
Connection connection=DAOConnection.getConnection();
Statement statement;
statement=connection.createStatement();
ResultSet resultSet;
resultSet=statement.executeQuery("select student.id,student.name,student.gender from student ");
int id;
String name;
String gender;
Student student;
while(resultSet.next())
{
id=resultSet.getInt("id");
name=resultSet.getString("name").trim();
gender=resultSet.getString("gender");
student=new Student();
student.setID(id);
student.setName(name);
student.setGender(gender);
students.add(student);
}
resultSet.close();
statement.close();
connection.close();
}catch(Exception exception)
{
throw new DAOException(exception.getMessage());
}
return students;
}
@Post
@Path("/add")
public ActionResponse add(Student student) throws Exception
{
ActionResponse actionResponse;
actionResponse=new ActionResponse();
try
{
int id=student.getID();
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement;
preparedStatement=connection.prepareStatement("select id from student where id=?");
preparedStatement.setInt(1,id);
ResultSet resultSet=preparedStatement.executeQuery();
if(resultSet.next())
{
resultSet.close();
preparedStatement.close();
connection.close();
actionResponse.setSuccessful(false);
actionResponse.setException("ID : "+id+" already exists.");
actionResponse.setResult((Object)0);
return actionResponse;
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("insert into student(id,name,gender) values (?,?,?)");
preparedStatement.setInt(1,student.getID());
preparedStatement.setString(2,student.getName());
preparedStatement.setString(3,student.getGender());
preparedStatement.executeUpdate();
resultSet.close();
preparedStatement.close();
connection.close();
actionResponse.setSuccessful(true);
actionResponse.setException("");
actionResponse.setResult("Roll Number : "+id+" added.");
return actionResponse;
}catch(SQLException sqlException)
{
throw new Exception(sqlException.getMessage());
}
}
@Path("/update")
public void update(Student student) throws DAOException
{
try
{
int id=student.getID();
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement;
preparedStatement=connection.prepareStatement("select gender from student where id=?");
preparedStatement.setInt(1,id);
ResultSet resultSet=preparedStatement.executeQuery();
if(resultSet.next()==false)
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("Invalid student id : "+id);
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("update employee set name=?,gender=? where id=?");
preparedStatement.setString(1,student.getName());
preparedStatement.setString(2,student.getGender());
preparedStatement.setInt(3,student.getID());
preparedStatement.executeUpdate();
preparedStatement.close();
connection.close();
}catch(SQLException sqlException)
{
throw new DAOException(sqlException.getMessage());
}
}
@Path("/idExists")
public boolean idExists(@RequestParameter("id") int id) throws DAOException
{
boolean exists=false;
try
{
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement;
preparedStatement=connection.prepareStatement("select gender from employee where id=?");
preparedStatement.setInt(1,id);
ResultSet resultSet=preparedStatement.executeQuery();
exists=resultSet.next();
resultSet.close();
preparedStatement.close();
connection.close();
}catch(Exception exception)
{
throw new DAOException(exception.getMessage());
}
return exists;
}
@Path("/delete")
public ActionResponse delete(@RequestParameter("id") int id) throws DAOException
{
ActionResponse actionResponse;
actionResponse=new ActionResponse();
try
{
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement;
preparedStatement=connection.prepareStatement("select gender from student where id=?");
preparedStatement.setInt(1,id);
ResultSet resultSet=preparedStatement.executeQuery();
if(resultSet.next()==false)
{
resultSet.close();
preparedStatement.close();
connection.close();
actionResponse.setSuccessful(false);
actionResponse.setException("Invalid Student ID : "+id);
actionResponse.setResult((Object)0);
// later on add to log and remove this
return actionResponse;
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("delete  from student where id=?");
preparedStatement.setInt(1,id);
preparedStatement.executeUpdate();
preparedStatement.close();
connection.close();
actionResponse.setSuccessful(true);
actionResponse.setException("");
actionResponse.setResult("Roll Number : "+id+" deleted.");
return actionResponse;

}catch(Exception exception)
{
throw new DAOException(exception.getMessage());
}
}
@Path("/getByID")
public Student getByID(@RequestParameter("id")int id) throws DAOException
{
Student student=null;
try
{
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement;
preparedStatement=connection.prepareStatement("select id,name,gender from student where id=?");
preparedStatement.setInt(1,id);
ResultSet resultSet=preparedStatement.executeQuery();
if(resultSet.next()==false)
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("Invalid student Id : "+id);
}
int idd;
String name;
String gender;
idd=resultSet.getInt("id");
name=resultSet.getString("name").trim();
gender=resultSet.getString("gender");
student=new Student();
student.setID(idd);
student.setName(name);
student.setGender(gender);
resultSet.close();
preparedStatement.close();
connection.close();
}catch(Exception exception)
{
throw new DAOException(exception.getMessage());
}
return student;
}
}