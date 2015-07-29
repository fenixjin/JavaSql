package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import objects.User;

/**
 * 对数据库进行操作
 * @author one
 *
 */
public class DBoperation 
{
	static Connection connection = new DBconnection().con; 
	//static Statement stmt;		//这个一直都没有被用到
	
	/**
	 * 注册操作
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public static String Register(User user) throws SQLException 
	{
		String presql = "select * from dbo.user_info where username = ?";
		PreparedStatement pstmt1 = connection.prepareStatement(presql);
		pstmt1.setString(1, user.username);
		ResultSet rs = pstmt1.executeQuery();
		
		if(rs.next() != false)
		{
			rs.close();
			pstmt1.close();
			return "User already exsists";
		}
		else
		{
			String sql_register = "insert into dbo.user_info (username, user_pwd, reliability, deviceID) values (?, ?, ?, ?)";
			PreparedStatement pstmt2 = connection.prepareStatement(sql_register);
			pstmt2.setString(1, user.username);
			pstmt2.setString(2, user.password);
			pstmt2.setString(3, "0.8");
			pstmt2.setString(4, user.deviceID);
			pstmt2.execute();
			
			rs.close();
			pstmt1.close();
			pstmt2.close();
			return "successful";
		}
	}
	
	/**
	 * 登录操作
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public static String Login(User user) throws SQLException
	{
		/*
		 * 首先检查用户是否输入deviceID以及deviceID是否存在于数据库
		 *  这里的逻辑是如果设备的ID对的上的话就直接登陆，如果需要跳过ID登陆的话
		 	直接不输入deviceID就好了
		 */
		if(user.deviceID != null)
		{
			String deviceString = "select * from dbo.user_info where deviceID = ?";
			PreparedStatement pstmt = connection.prepareStatement(deviceString);
			pstmt.setString(1, user.deviceID);
			ResultSet resultSet = pstmt.executeQuery();
			
			if(!resultSet.next())
			{
				resultSet.close();
				pstmt.close();
			}
			else 
			{
				resultSet.close();
				pstmt.close();
				return "successful";		
			}
		}

		/**
		 * 检查用户名密码是否正确
		 * 若用户名密码正确，对用户对应的设备号进行更新
		 */
		String presql = "select * from dbo.user_info where username = ?";
		PreparedStatement pstmt1 = connection.prepareStatement(presql);
		pstmt1.setString(1, user.username);
		ResultSet rs = pstmt1.executeQuery();
			
		if(!rs.next())
		{
			rs.close();
			pstmt1.close();
			return "user does not exsist";
		}
		else
		{
			if((user.password).equals(rs.getString("user_pwd")))
			{
				rs.close();
				pstmt1.close();
				UpdateUserDevice(user);
				return "right";
			}
			else 
			{
				return "wrong password";
			}
		}
	}
	
	/**
	 * 更新用户设备号
	 * @param user
	 * @throws SQLException
	 */
	public static void UpdateUserDevice(User user) throws SQLException
	{
		String updateString = "update dbo.user_info set deviceID = ? where username = ?";
		PreparedStatement updateStatement = connection.prepareStatement(updateString);
		updateStatement.setString(1, user.deviceID);
		updateStatement.setString(2, user.username);
		updateStatement.executeUpdate();
		updateStatement.close();
	}

	/********************************
	*传回最近的num条请求信息
	*@parameter times, num, category
	*@throws SQLexception
	*******************************/
	public static String[] RecentRequest(int times, int num, int category)
	{
		String
		String recentRequestString = "select * from (select row_number() over(order by starttime DESC) 'rn', * from request where category = ?) where rn between ? and ?";
		PreparedStatement requestStatement = con.prepareStatement(recentRequestString);
		requestStatement.setString(1, category);
		requestStatement.setString(2, times * num);
		requestStatement.setString(3, (times + 1) * num);
		ResultSet rs = requestStatement.executeQuery();

		while(rs.next)
		{

		}
		requestStatement.close();
	}

	/*********************************
	*记录一条请求
	*@parameter request
	*@throws SQLException
	**********************************/
	public static String InsertRequest(request)
	{
		String insertRequestString = "insert into request (starterID, starttime, deadline, category, description, tags, longitude, latitude)"+
										"values (?, ?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement insertStatement = con.prepareStatement(insertRequestString);
		insertStatement.setString(1, request.starterID);
		insertStatement.setString(2, request.starttime);
		insertStatement.setString(3, request.deadline);
		insertStatement.setString(4, Integer.tostring(request.category));
		insertStatement.setString(5, request.description);
		insertStatement.setString(6, request.tags);
		insertStatement.setString(7, String.valueof(request.longitude));
		insertStatement.setString(8, String.valueof(request.starterID));

		insertStatement.close();
	}
	/**********************************
	*记录一条回应
	*@param response
	*@throws SQLException
	*********************************/
	public static String InsertResponse(response)
	{
		String InsertResponseString = "insert into response("
	}
}
