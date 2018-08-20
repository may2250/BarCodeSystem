using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.ServiceModel;
using System.ServiceModel.Activation;
using System.Text;
using System.Text.RegularExpressions;

namespace LongServicesApi
{
    [ServiceBehavior(InstanceContextMode = InstanceContextMode.Single, ConcurrencyMode = ConcurrencyMode.Single, IncludeExceptionDetailInFaults = true)]
    [AspNetCompatibilityRequirements(RequirementsMode = AspNetCompatibilityRequirementsMode.Allowed)]
    public class RestServices:IRestServices
    {
        private static MysqlEngine mysqlEngine = new MysqlEngine();
        public Stream UserAuth(string tbody)
        {
            if (mysqlEngine.myCon == null)
            {
                mysqlEngine.OpenMysql();
            }
            UserEntity user = Common.DeserializeJsonToObject<UserEntity>(tbody);
            TBODY response = new TBODY();
            response.msgcode = 0;
            response.errinfo = "";
            response.status = -1;
            mysqlEngine.Authed(user, ref response);

            return new MemoryStream(Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(response)));
        }

        public Stream GetProduct(string querystr)
        {
            if (mysqlEngine.myCon == null)
            {
                mysqlEngine.OpenMysql();
            }
            TBODY response = new TBODY();
            response.msgcode = 0;
            response.errinfo = "";
            response.status = -1;
            QueryCmd query = Common.DeserializeJsonToObject<QueryCmd>(querystr);
            string sql = "";
            string sql2 = "";
            sql = Common.CreateSqlStr(query, ref sql2);

            QueryResult qr = new QueryResult();
            qr.page = query.page;

            if (mysqlEngine.getResultset(sql, sql2, ref qr))
            {
                response.status = 0;
            }
            else
            {
                response.status = 1;
            }
            response.data = qr;
            return new MemoryStream(Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(response)));
        }

        public Stream Outbound(string data)
        {
            if (mysqlEngine.myCon == null)
            {
                mysqlEngine.OpenMysql();
            }
            TBODY boundbody = Common.DeserializeJsonToObject<TBODY>(data);
            TBODY response = new TBODY();
            response.msgcode = 0;
            response.errinfo = "";
            if (mysqlEngine.InsertOutRecord(boundbody.data.ToString(), ref response))
            {
                response.status = 0;
            }
            else
            {
                response.status = -1;
            }

            return new MemoryStream(Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(response)));
        }
    }
}
