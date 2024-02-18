package tommy.StudentManagement;
import tommy.StudentManagement.*;
import java.sql.*;
public class DAOConnection
{
private DAOConnection(){}
static public Connection getConnection() throws DAOException
{
Connection connection=null;
try
{
Class.forName("com.mysql.cj.jdbc.Driver");
connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/student","std","std");
return connection;
}catch(Exception exception)
{
throw new DAOException(exception.getMessage());
}
}
}