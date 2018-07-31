using LongServicesApi;
using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel.Web;
using System.Text;

namespace BarCodeService
{
    class Program
    {
        static void Main(string[] args)
        {
            RestServices service = new RestServices();
            WebServiceHost _serviceHost = new WebServiceHost(service, new Uri("http://localhost:8000/BarcodeService"));
            _serviceHost.Open();
            Console.Write("服务端正在监听8000端口......");
            Console.ReadKey();
            _serviceHost.Close();
        }
    }
}
