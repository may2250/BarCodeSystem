using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.ServiceModel;
using System.ServiceModel.Activation;
using System.Text;

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
            TBODY body = Common.DeserializeJsonToObject<TBODY>(tbody);
            UserEntity user = Common.DeserializeJsonToObject<UserEntity>(body.data.ToString());
            TBODY response = new TBODY();
            response.msgcode = body.msgcode;

            mysqlEngine.Authed(user, ref response);

            return new MemoryStream(Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(response)));
        }

        public Stream GetProduct(string querystr)
        {
            dynamic obj = new JObject { { "ProductId", 10 }, { "Name", "xxxdfe" }, { "CategoryName", "jsdifadfi" }, { "Price", 52 } };

            return new MemoryStream(Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(obj)));
        }
    }
}
