package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import objects.User;

/**
 * ¶ÔÊý¾Ý¿â½øÐÐ²Ù×÷
 * @author one
 *
 */
public class DBoperation 
{
	static Connection connection = new DBconnection().con; 
	//static Statement stmt;		//Õâ¸öÒ»Ö±¶¼Ã»ÓÐ±»ÓÃµ½
	
	/**
	 * ×¢²á²Ù×÷
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
	 * µÇÂ¼²Ù×÷
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public static String Login(User user) throws SQLException
	{
		/*
		 * Ê×ÏÈ¼ì²éÓÃ»§ÊÇ·ñÊäÈëdeviceIDÒÔ¼°deviceIDÊÇ·ñ´æÔÚÓÚÊý¾Ý¿â
		 *  ÕâÀïµÄÂß¼­ÊÇÈç¹ûÉè±¸µÄID¶ÔµÄÉÏµÄ»°¾ÍÖ±½ÓµÇÂ½£¬Èç¹ûÐèÒªÌø¹ýIDµÇÂ½µÄ»°
		 	Ö±½Ó²»ÊäÈëdeviceID¾ÍºÃÁË
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
		 * ¼ì²éÓÃ»§ÃûÃÜÂëÊÇ·ñÕýÈ·
		 * ÈôÓÃ»§ÃûÃÜÂëÕýÈ·£¬¶ÔÓÃ»§¶ÔÓ¦µÄÉè±¸ºÅ½øÐÐ¸üÐÂ
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
	 * ¸üÐÂÓÃ»§Éè±¸ºÅ
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
	*´«»Ø×î½üµÄnumÌõÇëÇóÐÅÏ¢
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
	*¼ÇÂ¼Ò»ÌõÇëÇó
	*@parameter request
	*@throws SQLException
	**********************************/
	public static String InsertRequest(request)
	{
		String insertRequestString = "insert into request (starterID, starttime, deadline, category, description, tags, longitude, latitude)"+
										"values (?, ?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement insertRequestStatement = con.prepareStatement(insertRequestString);
		insertRequestStatement.setString(1, request.starterID);
		insertRequestStatement.setString(2, request.starttime);
		insertRequestStatement.setString(3, request.deadline);
		insertRequestStatement.setString(4, String.valueof(request.category));
		insertRequestStatement.setString(5, request.description);
		insertRequestStatement.setString(6, request.tags);
		insertRequestStatement.setString(7, String.valueof(request.longitude));
		insertRequestStatement.setString(8, String.valueof(request.starterID));

		insertRequestStatement.close();
	}
	/**********************************
	*¼ÇÂ¼Ò»Ìõ»ØÓ¦
	*@param response
	*@throws SQLException
	*********************************/
	public static String InsertResponse(response)
	{
		int i = 0;
		String InsertResponseString1 = "insert into response(requestID, reliability, item_num, answererID";
		String InsertResponseString2 = "values (";
		while (i < response.item_num)
		{
			InsertResponseString1 += "itemID" + String.valueof(i) + ", ";
			InsertResponseString2 += "?, ";
			i++;
		}
		InsertResponseString1 += ")";
		InsertResponseString2 += ")";
		PreparedStatement insertResponseStatement = con.prepareStatement(InsertResponseString1 + InsertResponseString2);
		i = 0;
		while (i < response.item_num)
		{
			insertResponseStatement.setString(i + 1, response);
		}
	}
}






