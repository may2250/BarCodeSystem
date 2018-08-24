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
        string M_str_sqlcon = "";
        public bool OpenMysql()
        {
            M_str_sqlcon = "server=127.0.0.1;user id=root;password=prevail;database=db_storege;Charset=utf8;"; //根据自己的设置
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
                        data.sn = reader.GetString(7).Replace("\r\n", "");
                        data.optdate = reader.GetString(8);
                        data.desc = "";
                        data.softversion = "";
                        data.destination = "";
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

        /// <summary>
        /// 验证数据
        /// </summary>
        /// <param name="cm"></param>
        /// <param name="tbl">数据库表</param>
        public bool getOutValidate(string sqlstr)
        {
            bool flag = true;
            MySqlDataReader reader = null;
            try
            {
                if (myCon.State == System.Data.ConnectionState.Closed || myCon.State == System.Data.ConnectionState.Broken)
                {
                    OpenMysql();
                }
                lock (lockobj)
                {
                    MySqlCommand mySqlCommand = getSqlCommand(sqlstr, myCon);
                    reader = mySqlCommand.ExecuteReader();
                }

                while (reader.Read())
                {
                    if (reader.HasRows)
                    {
                        flag = false;
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
                {
                    reader.Close();
                }
            }
            return flag;
        }

        public bool InsertOutRecord(string outdata, ref TBODY resp)
        {
            string sqlstr = "";
            bool flag = false;
            bool dup = false;
            ComMessage_t data = new ComMessage_t();
            string[] resultString = outdata.Split(new string[] { "|" }, StringSplitOptions.None);
            switch (resultString[0])
            {
                case "sn":
                    sqlstr = "select id from outbound where sn = '" + resultString[1] + "'";
                    if (!getOutValidate(sqlstr))
                    {
                        resp.errinfo = "重复的设备，该设备已出库...";
                        dup = true;
                        //return false;
                    }
                    sqlstr = "select * from packing where sn = '" + resultString[1] + "'";
                    break;
                case "mac":
                    sqlstr = "select id from outbound where mac = '" + resultString[1] + "'";
                    if (!getOutValidate(sqlstr))
                    {
                        resp.errinfo = "重复的设备，该设备已出库...";
                        dup = true;
                        //return false;
                    }
                    sqlstr = "select * from packing where mac = '" + resultString[1] + "'";
                    break;

            }

            //从包装部数据表查询出库数据
            MySqlCommand mySqlCommand = null;
            MySqlDataReader reader = null;
            
            try
            {
                lock (lockobj)
                {
                    mySqlCommand = getSqlCommand(sqlstr, myCon);
                    reader = mySqlCommand.ExecuteReader();
                    while (reader.Read())
                    {
                        if (reader.HasRows)
                        {
                            data.username = reader.GetString(1);
                            data.boxid = reader.GetString(2);
                            data.orderid = reader.GetString(3);
                            data.mac = reader.GetString(4);
                            data.wifimac = reader.GetString(5);
                            data.gpsn = reader.GetString(6);
                            data.sn = reader.GetString(7);
                            data.optdate = reader.GetString(8);
                            data.softversion = reader.GetString(9);

                            if (data.softversion != resultString[2])
                            {
                                flag = false;
                                resp.errinfo = "软件版本不一致，请核对软件版本[" + data.softversion + "]";
                                break;
                            }
                            flag = true;
                        }
                    }
                }

            }
            catch (Exception)
            {
                flag = false;
                resp.errinfo = "从包装数据表提取数据失败。";
            }
            finally
            {
                if (reader != null)
                    reader.Close();
            }
            if (data == null)
            {
                resp.errinfo = "从包装数据表无改设备。";
                return false;
            }
            if (!dup && flag)
            {
                //将数据插入到出库表单
                sqlstr = string.Format("INSERT INTO outbound(username,boxid,orderid,mac,wifimac,gpsn,sn,optdate,destination,softversion) VALUES('{0}','{1}','{2}','{3}','{4}','{5}','{6}','{7}','{8}','{9}')", data.username, data.boxid.ToUpper(), data.orderid, data.mac.ToUpper(), data.wifimac == null ? "" : data.wifimac.ToUpper(), data.gpsn == null ? "" : data.gpsn, data.sn.ToUpper(), DateTime.Now.ToString("yyyy-MM-dd"), resultString[3], data.softversion);
                if (!Execute(sqlstr))
                {
                    resp.errinfo = "记录数据失败。";
                    return false;
                }
            }
            resp.data = data;
            return (flag && !dup);
        }

        public bool getStatistic(string outdata, ref TBODY resp)
        {
            List<int> statstic = new List<int>();
            using (MySqlConnection con = new MySqlConnection(M_str_sqlcon))
            {
                con.Open();
                string[] resultString = outdata.Split(new string[] { "|" }, StringSplitOptions.None);
                string sqlstr = "select count(*) from outbound where optdate >= '" + resultString[0] + "' and optdate <= '" + resultString[1] + "' and mac like '3071B2%'";
                using (MySqlCommand cmd = new MySqlCommand(sqlstr, con))
                {
                    //MySqlTransaction trans = con.BeginTransaction();
                    try
                    {
                        //cmd.Connection = trans.Connection;
                        //cmd.Transaction = trans;
                        cmd.CommandText = sqlstr;
                        statstic.Add(Convert.ToInt32(cmd.ExecuteScalar()));

                        sqlstr = "select count(*) from outbound where optdate >= '" + resultString[0] + "' and optdate <= '" + resultString[1] + "' and mac like '000A5A%'";
                        cmd.CommandText = sqlstr;
                        statstic.Add(Convert.ToInt32(cmd.ExecuteScalar()));

                        sqlstr = "select count(*) from outbound where optdate >= '" + resultString[0] + "' and optdate <= '" + resultString[1] + "'";
                        cmd.CommandText = sqlstr;
                        statstic.Add(Convert.ToInt32(cmd.ExecuteScalar()) - statstic[0] - statstic[1]);
                    }
                    catch
                    {
                        resp.errinfo = "统计出现未知错误.";
                        resp.data = statstic;
                        return false;
                    }
                    finally
                    {
                        
                    }
                }

            }
            resp.data = statstic;
            return true;
        }

    }
}
