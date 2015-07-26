using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Xml.Linq;
using System.Data.SqlClient;
using System.Text.RegularExpressions;
using System.Collections;
using System.Collections.Generic;

namespace sqlmanagement
{

	/******************************************************
		userregister
		用户注册的时候的验证函数
		先找用户名是否有重复，如果没有的话就创建出来
		根据成功与否返回布尔值
	******************************************************/
	public bool UserRegister(string username, string password, string devicecode)
	{
		try 
		{
			string sql_checking_name = "select username from user where username = " + username;
			SqlCommand cmd_checking_name = new SqlCommand(sql_checking_name, sqlCon);
			SqlDataReader reader_checking_name = cmd.ExecuteReader;
			//如果可以找到用户名的话，说明用户名重复
			if(reader_checking_name.HasRows == true)
			{
				return false;
			}
			else
			{
				string sql_register = "insert into user(username, password, reliability, devicecode,) 
										values ('" + username + "'," + password + "'," + "0.80" + "'," + devicecode "')";
				SqlCommand cmd_register = new SqlCommand(sql_register, sqlCon);
				cmd_register.ExecuteNonQuery();
				cmd_register.Dispose();
				cmd_checking_name.Dispose();
			}
		}
		catch(Exception)
		{
			return false;
		}
		return list;
	}
	/******************************************************
		userlogin
		用户登录时候的验证函数
		先找用户名，再验证密码
		根据成功与否返回布尔值
	******************************************************/
	public bool UserLogIn(string username, string password)
	{
		try
		{
			string sql = "select password from user where password ='" + password + "'";
			SqlCommand cmd = new SqlCommand(sql, sqlCon);
			SqlDataReader reader = cmd.ExecuteReader();
			if (reader.HasRows) == false;
			{
				//查找不到用户名
				return false;
			}
			if(reader[0] == password)
			{
				//验证成功，可以登录
				return true;
			}
			else
			{
				//密码不对
				return false;
			}
			cmd.Dispose();
		}
		catch(Exception)
		{
			return false;
		}
		return false;
	}

	/************************************************
		SendRequest
		将用户的推荐请求存入数据库的函数
		开始时间和结束时间都是以字符串的形式传入的
		因为sql似乎是可以接受字符串作为datetime类型的
		输入的
	************************************************/
	public bool SendRequest(int starterID,
							int requestID,
							string starttime,
							string deadline,
							double longitude,
							double latitude,
							)
	{
		try
		{
			string sql = "insert into request (starterID, starttime, deadline, longitude, latitude) 
							values ('" + starterID + "'," + starttime +"'," + deadline + 
							"'," + longitude + "'," + latitude + ")";
			SqlCommand cmd = new SqlCommand(sql, sqlCon);
			cmd.ExecuteNonQuery();
			cmd.Dispose();

			return true;
		}
		catch(Exception)
		{
			return false;
		}
	}

	/**********************************************
		SendResponse
		收到回应之后将它存入数据库
		这里的评分是分成以下几个部分的：
		item_num：推荐的项目数量
		scorearray:项目的ID以及评分,ID一起放在前面，分数一起放在后面
	**********************************************/
	public bool SendResponse(int requestID,
								short item_num,
								float reliability,
								int answererID,
								int scorearray[],
								)
	{
		try
		{
			int i = 0;
			string sql1 = "insert into response (requestID, reliability, item_num, answerID,"
			string sql2 = "values('" + requestID + "'," + reliability + "'," + item_num + "'," + answerID + "',";

			while(i < item_num)
			{
				sql1 += "itemID" + i +"',";
				sql2 += scorearray[i] + "',";
				i++;
			}
			while(i < 2*item_num)
			{
				sql1 += "item" + (i-item_num) + "_score,";
				sql2 += scorearray[i] + "',";
			}
			sql = sql1 + ")" + sql2 + ")";
			SqlCommand cmd = new SqlCommand(sql, sqlCon);
			cmd.ExecuteNonQuery();
			cmd.Dispose();

			//增加用户推荐次数
			string sql_update_rec_count = "update user set response_count = response_count + 1 
											where userID = " + answererID;
			SqlCommand cmd_update_rec_count = new SqlCommand(sql_update_rec_count, sqlCon);
			cmd_update_rec_count.ExecuteNonQuery();
			cmd_update_rec_count.Dispose();

			return ture;
		}
		catch(Exception)
		{
			return false;
		}
	}

	/******************************************
		这里的Item是一个结构
		这个函数的功能是计算出最终的结果
		做法是遍历所有的回应，查看项目评分，
		如果不存在这个项目的话就创建这个项目
		如果存在这个项目的话就把这个项目的平分加入总分
		最后通过一系列计算得到最终结果。
	******************************************/
	public Item[] calculate_result(int requestID)
	{
		try
		{
			string sql = "select * from response where requestID = " + requestID;
			SqlCommand cmd = new SqlCommand(sql, sqlCon);
			SqlDataReader reader = cmd.ExecuteReader();
			while(reader.Read())
			{
				int i = reader.GetSqlInt32(3); 					//找出这个元组中有多少个打分
                while (i != 0)
                {
                    int j = 0;
                    int m = reader.GetSqlInt32(j + 5);				//找到打分元组中的第j个项目

                    //根据itemID以及requestID来找出相应的项目得分元组
                    string sql_for_item = "select itemID, requestID from item_stat where itemID = "
                    + m + "AND requestID = " + requestID;
                    SqlCommand cmd_for_item_stat = new SqlCommand(sql_for_item, sqlCon);
                    SqlDataReader reader_for_item_stat = cmd.ExecuteReader();
                    //如果查找不到项目，就添加这个项目
                    if(!reader_for_item_stat.HasRows)
                    {
                    	/*string sql_adding_score = "update item_stat set total = t, recommender_count = r, t = t +" + reader.GetSqlInt32(j + 14) + "r = r + 1" + "where itemID = " + m + "AND requestID = " + requestID;
                    	SqlCommand cmd_for_adding_score = new SqlCommand(cmd_for_adding_score, sqlCon);
                    	cmd_for_adding_score.ExecuteNonQuery();*/     
                    	string sql_inserting_item_stat = "insert into item_stat(itemID, requestID) values(" + itemID + "'," + requestID + ")";
                    	SqlCommand cmd_for_inserting_item_stat = new SqlCommand(sql_inserting_item_stat, sqlCon);
                    	cmd_for_inserting_item_stat.ExecuteNonQuery();
                    }
                    //把当前分数加入项目的总分中(加入的时候乘以可信度)
                    string sql_adding_score = "update item_stat set total = t, recommender_count = r, t = t +" 
                    							+ reader.GetSqlInt32(j + 15) * reader.GetSqlInt32(2)+ "r = r + 1" + "where itemID = " + m 
                    							+ "AND requestID = " + requestID;
                    SqlCommand cmd_for_adding_score = new SqlCommand(sql_for_adding_score, sqlCon);
                    cmd_for_adding_score.ExecuteNonQuery();
                    cmd_for_adding_score.Dispose();
                    cmd_for_item_stat.Dispose();
                    i--;
                }
			}
			//总分统计完毕之后就开始计算

            //计算平均数
            string sql_for_average = "update item_stat set average = a, total = t, recommender_count = r, a = t / r";
            SqlCommand cmd_for_avg = new SqlCommand(sql_for_average, sqlCon);
            cmd_for_avg.ExecuteNonQuery();
            cmd_for_avg.Dispose();

            //归一化计算
            string sql_for_minmax = "select MAX(average), MIN(average) from item_stat";
            SqlCommand cmd_for_minmax = new SqlCommand(sql_for_average, sqlCon);
            SqlDataReader reader_for_minmax = cmd_for_minmax.ExecuteReader;
            int max = reader_for_minmax.GetSqlInt32(0);
            int min = reader_for_minmax.GetSqlInt32(1);
            int diff = max - min;
            cmd_for_minmax.Dispose();

            string sql_for_normalization = "update item_stat set average = (average - " + min + ") / " + diff;
            SqlCommand cmd_for_normalization = new SqlCommand(sql_for_normalization, sqlCon);
            cmd_for_normalization.ExecuteNonQuery;
            cmd_for_normalization.Dispose;

            //计算最后得分
            string sql_for_recommand_count = "select COUNT(*) from response where requestID = " + requestID;
            SqlCommand cmd_for_recommand_count = new SqlCommand(sql_for_recommand_count, sqlCon);
            SqlDataReader reader_for_recommand_count = cmd_for_recommand_count.ExecuteReader();
            int count = reader_for_recommand_count.GetSqlInt32(0);
            cmd_for_recommand_count.Dispose();

            string sql_for_popularity = "update item_stat set final = average + a * recommender_count / " + count + 
            							"where requestID = " + requestID;
            SqlCommand cmd_for_popularity = new SqlCommand(sql_for_popularity, sqlCon);
            cmd_for_popularity.ExecuteNonQuery();
            cmd_for_popularity.Dispose();

            //计算可信度
            string sql = "select * from response where requestID = " + requestID;
            SqlCommand cmd = new SqlCommand(sql_for_recommand_count, sqlCon);
            SqlDataReader reader = cmd.ExecuteReader();

		}
	}

}