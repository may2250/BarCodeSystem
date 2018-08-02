using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Linq;
using System.Text;
using Newtonsoft.Json;
using System.IO;
using System.Collections;
using System.Text.RegularExpressions;

namespace LongServicesApi
{
    class Common
    {
        /// <summary>
        /// 解析JSON字符串生成对象实体
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="json">json字符串(eg.{"ID":"112","Name":"石子儿"})</param>
        /// <returns>对象实体</returns>
        public static T DeserializeJsonToObject<T>(string json) where T : class
        {
            JsonSerializer serializer = new JsonSerializer();
            StringReader sr = new StringReader(json);
            object o = serializer.Deserialize(new JsonTextReader(sr), typeof(T));
            T t = o as T;
            return t;
        }

        /// <summary>
        /// 解析JSON数组生成对象实体集合
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="json">json数组字符串(eg.[{"ID":"112","Name":"石子儿"}])</param>
        /// <returns>对象实体集合</returns>
        public static List<T> DeserializeJsonToList<T>(string json) where T : class
        {
            JsonSerializer serializer = new JsonSerializer();
            StringReader sr = new StringReader(json);
            object o = serializer.Deserialize(new JsonTextReader(sr), typeof(List<T>));
            List<T> list = o as List<T>;
            return list;
        }

        /// <summary>
        /// 组装数据库查询命令
        /// </summary>
        /// <typeparam name="query">查询数据</typeparam>
        /// <param name="sql2">组装的查询命令</param>
        /// <returns>组装的查询命令</returns>
        public static string CreateSqlStr(QueryCmd query, ref string sql2)
        {
            string sql = "";
            Regex r = new Regex(@"^([0-9a-fA-F]{2})(([0-9a-fA-F]{2}){5})$");
            if (r.IsMatch(query.data))
            {
                //查询字段为MAC地址
                switch (query.querycmd)
                {
                    case 0://车间数据
                        sql = "select * from btlable where onumac = '" + query.data.ToUpper() + "'  order by boxid asc limit " + (query.page - 1) * query.number + "," + query.number;
                        sql2 = "select count(*) from btlable where onumac = '" + query.data.ToUpper() + "' order by OptDate desc";
                        break;
                    case 1://包装数据
                        sql = "select * from packing where mac = '" + query.data.ToUpper() + "'  order by boxid asc limit " + (query.page - 1) * query.number + "," + query.number;
                        sql2 = "select count(*) from packing where mac = '" + query.data.ToUpper() + "' order by OptDate desc";
                        break;
                    case 2://仓库数据
                        sql = "select * from outbound where mac = '" + query.data.ToUpper() + "'  order by boxid asc limit " + (query.page - 1) * query.number + "," + query.number;
                        sql2 = "select count(*) from outbound where mac = '" + query.data.ToUpper() + "' order by OptDate desc";
                        break;

                }

            }
            else if (query.data.IndexOf("SCDD") != -1)
            {
                //查询字段为orderid
                switch (query.querycmd)
                {
                    case 0://车间数据

                        break;
                    case 1://包装数据
                        sql = "select * from packing where orderid = '" + query.data.ToUpper() + "'  order by boxid asc limit " + (query.page - 1) * query.number + "," + query.number;
                        sql2 = "select count(*) from packing where orderid = '" + query.data.ToUpper() + "' order by OptDate desc";
                        break;
                    case 2://仓库数据
                        sql = "select * from outbound where orderid = '" + query.data.ToUpper() + "'  order by boxid asc limit " + (query.page - 1) * query.number + "," + query.number;
                        sql2 = "select count(*) from outbound where orderid = '" + query.data.ToUpper() + "' order by OptDate desc";
                        break;

                }
            }
            else
            {
                //查询字段默认为SN
                switch (query.querycmd)
                {
                    case 0://车间数据
                        sql = "select * from btlable where printsn = '" + query.data.ToUpper() + "'  order by boxid asc limit " + (query.page - 1) * query.number + "," + query.number;
                        sql2 = "select count(*) from btlable where printsn = '" + query.data.ToUpper() + "' order by OptDate desc";
                        break;
                    case 1://包装数据
                        sql = "select * from packing where sn = '" + query.data.ToUpper() + "'  order by boxid asc limit " + (query.page - 1) * query.number + "," + query.number;
                        sql2 = "select count(*) from packing where sn = '" + query.data.ToUpper() + "' order by OptDate desc";
                        break;
                    case 2://仓库数据
                        sql = "select * from outbound where mac = '" + query.data.ToUpper() + "'  order by boxid asc limit " + (query.page - 1) * query.number + "," + query.number;
                        sql2 = "select count(*) from outbound where sn = '" + query.data.ToUpper() + "' order by OptDate desc";
                        break;

                }
            }

            return sql;
        }

    }

    [DataContract]
    public class TBODY
    {
        [DataMember]
        public int status { get; set; }
        [DataMember]
        public string errinfo { get; set; }
        [DataMember]
        public int msgcode { get; set; }
        [DataMember]
        public object data { get; set; }
    }

    [DataContract]
    public class UserEntity
    {
        [DataMember]
        public string username { get; set; }
        [DataMember]
        public string password { get; set; }
        [DataMember]
        public int flag { get; set; }
    }

    [DataContract]
    public class QueryCmd
    {
        [DataMember]
        public int querycmd { get; set; }
        [DataMember]
        public int page { get; set; }
        [DataMember]
        public int number { get; set; }
        [DataMember]
        public string data { get; set; }
    }

    [DataContract]
    public class QueryResult
    {
        [DataMember]
        public int resultcnt { get; set; }
        [DataMember]
        public int page { get; set; }
        [DataMember]
        public ArrayList result { get; set; }
    }

    [DataContract]
    public class ComMessage_t
    {
        [DataMember]
        public string desc { get; set; }
        [DataMember]
        public string username { get; set; }
        [DataMember]
        public string orderid { get; set; }
        [DataMember]
        public string boxid { get; set; }
        [DataMember]
        public string mac { get; set; }
        [DataMember]
        public string wifimac { get; set; }
        [DataMember]
        public string gpsn { get; set; }
        [DataMember]
        public string sn { get; set; }
        [DataMember]
        public string destination { get; set; }
        [DataMember]
        public string optdate { get; set; }
        [DataMember]
        public string softversion { get; set; }
    }
}
