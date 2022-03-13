using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading;
using System.Threading.Tasks;
using FlightMobileWeb.Models;
using Microsoft.AspNetCore.Mvc;
namespace FlightMobileWeb.Controllers
{
    [Route("[controller]")]
    [ApiController]
    public class ScreenshotController : ControllerBase
    {
        Model model;

        public ScreenshotController(Model m)
        {
            model = m;
        }

        // GET: /Screenshot
        [HttpGet]
        public IActionResult Get()
        {
            try
            {
                WebRequest request = WebRequest.Create(model.Url);
                HttpWebResponse response = (HttpWebResponse)request.GetResponse();
                using (Stream stream = response.GetResponseStream())
                {
                    //read the content
                    MemoryStream ms = new MemoryStream();
                    stream.CopyTo(ms);
                    byte[] img = ms.ToArray();
                    return File(img, "image/png");
                };
            }
            catch (Exception)
            {
                return new BadRequestResult();
            }
        }
    }
}