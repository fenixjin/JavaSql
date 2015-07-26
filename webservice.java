import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManeger;
import java.sql.PreparedStatement

public class sqlConnection
{
	public static void main(String args[])
	{
		String url = "jdbc:sqlserver://########;databaseName=!)@#&!@(*; user = sa; password = ******";
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		try
		{
			// Establish the connection.    
            System.out.println("begin.");    
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");    
            con = DriverManager.getConnection(url);    
            System.out.println("end.");    
    
            /* Create and execute an SQL statement that returns some data.    
            String SQL = "SELECT TOP 10 * FROM aud_t_basis";    
            stmt = con.createStatement();    
            rs = stmt.executeQuery(SQL);    
    
            // Iterate through the data in the result set and display it.    
            while (rs.next()) 
            {    
                System.out.println(rs.getString(4) + " " + rs.getString(6));  
            }	*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (rs != null)    
                try 
            	{    
                    rs.close();    
                } 
                catch (Exception e) 
                {}    
            if (stmt != null)    
                try 
            	{    
                    stmt.close();    
                } 
                catch (Exception e) 
                {}    
            if (con != null)    
                try 
            	{    
                    con.close();    
                } 
                catch (Exception e) 
                {}    
		}
	}
}

public class DBoperation
{
	public int UserRegister(String username, String user_pwd, String deviceID)
	{
		//首先检查是否用户名是否已经存在
		String presql = "select username from user_info where username = ?";
		PreparedStatement pstmt1 = con.preparedStatement(presql);
		pstmt1.setstring(1, username);
		ResultSet rs = pstmt1.executeQuery();
		if(rs.next() != false)
		{
			rs.close();
			pstmt1.close();
			return -1;
		}
		else
		{
			String sql_register = "insert into user(username, password, reliability, devicecode,)"+ 
									"values ( ? , ?, ?, ?)";
			PreparedStatement pstmt2 = con.preparedStatement(sql_register);
			pstmt2.setstring(1, username);
			pstmt2.setstring(2, user_pwd);
			pstmt2.setstring(3, 0.8);
			pstmt2.setstring(4, deviceID);
			pstmt2.executeNonQuery();
		}
		rs.close();
		pstmt1.close();
		pstmt2.close();
		return 1;
	}

	public int UserLogin(String username, String user_pwd)
	{
		//首先还是查找用户名
		String presql = "select username, password from user_info where username = ?";
		PreparedStatement pstmt1 = con.preparedStatement(presql);
		pstmt1.setstring(1, username);
		Resultset rs = pstmt1.executeQuery();
		if(!rs.next())
		{
			rs.close();
			pstmt1.close();
			return -1;
		}
		else
		{
			if(userpwd.equals(rs.getString(2)))
			{
				rs.close();
				pstmt1.close();
				return 1;
			}
		}
	}

	/*
		返回最近的num条请求信息
	*/
	public string[] RecentRequest(int times, int num, int category)
	{
		String presql = "select * from (select row_number() over(order by starttime DESC) 'rn', * from request where category = ?) where rn between ? and ?";
		PreparedStatement pstmt1 = con.preparedStatement(presql);
		pstmt1.setstring(1, category);
		pstmt1.setstring(2, times * m);
		pstmt1.setstring(3, (times + 1) * m);
	} 
	public bool SendRequest(int starterID,
					   String starttime,
					   String deadline,
					   int category
					   String description,
					   String tags,
					   double longitude,
					   double latitude,
					   )
	{
		String presql = "insert into request (starterID, starttime, deadline, category, description, tags, longitude, latitude)"+
							"values (?, ?, ?, ?, ?, ?, ?, ?);"
		PreparedStatement pstmt1 = con.preparedStatement(presql);
		pstmt1.setstring(1, starterID);
		pstmt1.setstring(2, starttime);
		pstmt1.setstring(3, deadline);
		pstmt1.setstring(4, String.toString(category));
		pstmt1.setstring(5, description);
		pstmt1.setstring(6, tags);
		pstmt1.setstring(7, longitude);
		pstmt1.setstring(8, latitude);

		pstmt1.executeNonQuery();

		pstmt1.close();
		return true;
	}

	public bool SendResponse(int requestID,
								short item_num,
								float reliability,
								int answererID,
								int scorearray[],
								)
	{
		int i = 0;
		String sql1 = "insert into response (requestID, reliability, item_num, answerID,"
		String sql2 = "values(?, ?, ?, ?,";
		
		while (i < item_num)
		{
			sql1 += "itemID" + String.valueOf(i) + ",";
			sql2 += "?, ";
			i++;
		}

		String presql = sql1 + ")" + sql2 + ")";
		PreparedStatement pstmt1 = con.preparedStatement(presql);
		
		i = 0;
		while (i < item_num)
		{
			pstmt1.setstring(i+1, scorearray[i]);
		}

		pstmt1.executeNonQuery();

		pstmt1.close();
	}
}