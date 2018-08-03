using MySql.Data.MySqlClient;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Timers;

namespace LongServicesApi
{
    class MysqlEngine
    {
        public MySqlConnection myCon = null;
        private static object lockobj = new object();
        System.Timers.Timer dbtimer = new System.Timers.Timer();
        public bool OpenMysql()
        {
            string M_str_sqlcon = "server=127.0.0.1;user id=root;password=prevail;database=db_storege;Charset=utf8;"; //根据自己的设置
            myCon = new MySqlConnection(M_str_sqlcon);
            try
            {
                myCon.Open();
                //到时间的时候执行事件  
                dbtimer.Elapsed += new ElapsedEventHandler(RestartMysql);
                dbtimer.Interval = 30000;
                dbtimer.AutoReset = true;//执行一次 false，一直执行true  
                //是否执行System.Timers.Timer.Elapsed事件  
                dbtimer.Enabled = true;  
            }
            catch (MySqlException ex)
            {
                return false;
            }
            return true;
        }

        public void RestartMysql(object source, System.Timers.ElapsedEventArgs e)
        {
            if (myCon.State == System.Data.ConnectionState.Closed || myCon.State == System.Data.ConnectionState.Broken)
            {
                OpenMysql();
            }
            else if (myCon.State != System.Data.ConnectionState.Executing)
            {
                myCon.Close();
                myCon.Open();                
            }
        }

        /// <summary>
        /// 建立执行命令语句对象
        /// </summary>
        /// <param name="sql"></param>
        /// <param name="mysql"></param>
        /// <returns></returns>
        public MySqlCommand getSqlCommand(string sql, MySqlConnection mysql)
        {
            MySqlCommand mySqlCommand = new MySqlCommand(sql, mysql);
            return mySqlCommand;
        }

        /// <summary>
        /// 操做数据
        /// </summary>
        /// <param name="sql"></param>
        public bool Execute(string sql)
        {
            try
            {
                if (myCon.State == System.Data.ConnectionState.Closed || myCon.State == System.Data.ConnectionState.Broken)
                {
                    OpenMysql();
                }
                lock (lockobj)
                {
                    MySqlCommand mySqlCommand = getSqlCommand(sql, myCon);
                    mySqlCommand.ExecuteNonQuery();
                }
            }
            catch (Exception ex)
            {
                return false;
            }
            return true;
        }


        public void Authed(UserEntity user,ref TBODY response)
        {            
            string sqlstr = "select * from users where username = '" + user.username + "'";
            MySqlDataReader reader = null;
            MySqlCommand mySqlCommand = getSqlCommand(sqlstr, myCon);
            try
            {
                reader = mySqlCommand.ExecuteReader();
                while (reader.Read())
                {
                    if (reader.HasRows)
                    {
                        UserEntity auth = new UserEntity();
                        auth.username = reader.GetString(1);
                        auth.password = reader.GetString(2);
                        auth.flag = reader.GetInt32(3);
                        if (auth.username == user.username && auth.password == user.password)
                        {
                            response.status = 0;
                        }
                        else
                        {
                            response.status = 1;
                            response.errinfo = "用户名或密码不正确！";
                        }
                        response.data = auth;
                        return;
                    }
                    response.errinfo = "无用户记录！";
                    response.status = 1;
                }
            }
            catch (Exception)
            {
                response.errinfo = "未知错误，查询失败！";
                response.status = 1;
            }
            finally
            {
                if (reader != null)
                    reader.Close();
            }            
        }

        public bool getResultset(string sql, string sqlstr2, ref QueryResult response)
        {
            bool flag = true;
            if (myCon.State == System.Data.ConnectionState.Closed || myCon.State == System.Data.ConnectionState.Broken)
            {
                OpenMysql();
            }
            response.result = new ArrayList();
            MySqlDataReader reader = null;
            try
            {
                lock (lockobj)
                {
                    MySqlCommand mySqlCommand = getSqlCommand(sqlstr2, myCon);
                    response.resultcnt = Convert.ToInt32(mySqlCommand.ExecuteScalar());
                    mySqlCommand = getSqlCommand(sql, myCon);
                    reader = mySqlCommand.ExecuteReader();
                }

                while (reader.Read())
                {
                    if (reader.HasRows)
                    {
                        ComMessage_t data = new ComMessage_t();
                        data.username = reader.GetString(1);
                        data.boxid = reader.GetString(2);
                        data.orderid = reader.GetString(3);
                        data.mac = reader.GetString(4);
                        data.wifimac = reader.GetString(5);
                        data.gpsn = reader.GetString(6);
                        data.sn = reader.GetString(7);
                        data.optdate = reader.GetString(8);
                        response.result.Add(data);
                        flag = true;
                    }
                }
            }
            catch (Exception ex)
            {
                flag = false;
            }
            finally
            {
                if (reader != null)
                    reader.Close();
            }
            return flag;
        }

    }
}
