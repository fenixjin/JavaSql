package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import objects.User;

/**
 * �����ݿ���в���
 * @author one
 *
 */
public class DBoperation 
{
	static Connection connection = new DBconnection().con; 
	//static Statement stmt;		//���һֱ��û�б��õ�
	
	/**
	 * ע�����
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
	 * ��¼����
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public static String Login(User user) throws SQLException
	{
		/*
		 * ���ȼ���û��Ƿ�����deviceID�Լ�deviceID�Ƿ���������ݿ�
		 *  ������߼�������豸��ID�Ե��ϵĻ���ֱ�ӵ�½�������Ҫ����ID��½�Ļ�
		 	ֱ�Ӳ�����deviceID�ͺ���
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
		 * ����û��������Ƿ���ȷ
		 * ���û���������ȷ�����û���Ӧ���豸�Ž��и���
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
	 * �����û��豸��
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
	*���������num��������Ϣ
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
	*��¼һ������
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
	*��¼һ����Ӧ
	*@param response
	*@throws SQLException
	*********************************/
	public static String InsertResponse(response)
	{
		String InsertResponseString = "insert into response("
	}
}
