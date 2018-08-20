using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;

namespace LongServicesApi
{
    [ServiceContract(Name = "RestServices")]
    public interface IRestServices
    {
        [OperationContract]
        [WebInvoke(Method = "POST", UriTemplate = Routing.UserAuth, BodyStyle = WebMessageBodyStyle.Bare, RequestFormat = WebMessageFormat.Json, ResponseFormat = WebMessageFormat.Json)]
        Stream UserAuth(string tbody);

        [OperationContract]
        [WebGet(UriTemplate = Routing.GetProduct, BodyStyle = WebMessageBodyStyle.Bare, RequestFormat = WebMessageFormat.Json, ResponseFormat = WebMessageFormat.Json)]
        Stream GetProduct(string querystr);

        [OperationContract]
        [WebInvoke(Method = "POST", UriTemplate = Routing.Outbound, BodyStyle = WebMessageBodyStyle.Bare, RequestFormat = WebMessageFormat.Json, ResponseFormat = WebMessageFormat.Json)]
        Stream Outbound(string tbody);
    }

    public static class Routing
    {
        public const string UserAuth = "/Auth";
        public const string GetProduct = "/Product/{querystr}";
        public const string Outbound = "/Outbound";
    }
}
