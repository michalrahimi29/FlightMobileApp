using Microsoft.Extensions.Configuration;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using static System.Net.Mime.MediaTypeNames;

namespace FlightMobileWeb.Models
{
    public class Model
    {
        TelnetClient telnetClient;
        public string Url { get; set; }

        public Model(IConfiguration config)
        {
            this.telnetClient = new TelnetClient();
            string ip = config.GetConnectionString("ip");
            int port = int.Parse(config.GetConnectionString("port"));
            telnetClient.connect(ip, port); 
            Url = config["urls"];
        }

        public void SetCommand(Command command)
        {
            try
            {
                //Ruder
                telnetClient.write("set /controls/flight/rudder " + command.Rudder + "\n");
                telnetClient.write("get /controls/flight/rudder \n");
                string rudder = telnetClient.read();
                if (command.Rudder != Double.Parse(rudder))
                {
                    throw new Exception();
                }
                //Elevator
                telnetClient.write("set /controls/flight/elevator " + command.Elevator + "\n");
                telnetClient.write("get /controls/flight/elevator \n");
                string elevator = telnetClient.read();
                if (command.Elevator != Double.Parse(elevator))
                {
                    throw new Exception();
                }
                //Throttle
                telnetClient.write("set /controls/engines/current-engine/throttle " + command.Throttle + "\n");
                telnetClient.write("get /controls/engines/current-engine/throttle \n");
                string throttle = telnetClient.read();
                if (command.Throttle != Double.Parse(throttle))
                {
                    throw new Exception();
                }
                //Aileron
                telnetClient.write("set /controls/flight/aileron " + command.Aileron + "\n");
                telnetClient.write("get /controls/flight/aileron \n");
                string aileron = telnetClient.read();
                if (command.Aileron != Double.Parse(aileron))
                {
                    throw new Exception();
                }
            }
            catch (Exception)
            {
                throw new Exception();
            }
        }
    }
}
