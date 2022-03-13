using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading;
using System.Threading.Tasks;

namespace FlightMobileWeb.Models
{
    public class TelnetClient
    {
        private TcpClient client;
        private NetworkStream clientStream;
        private static Mutex mut = new Mutex();

        public bool connect(string ip, int port)
        {
            IPEndPoint ep = new IPEndPoint(IPAddress.Parse(ip), port);
            client = new TcpClient();
            try
            {
                client.Connect(ep);
                clientStream = client.GetStream();
                write("data\n");
                return true;
            }
            catch (Exception)
            {
                return false;
            }
        }

        public void disconnect()
        {
            if (client != null)
            {
                client.Close();
            }
        }

        public string read()
        {
            string data = null;
            byte[] bytes = new byte[1024];
            try
            {
                mut.WaitOne();
                int i = clientStream.Read(bytes, 0, 1024);
                data += Encoding.ASCII.GetString(bytes, 0, bytes.Length);
                data = Regex.Replace(data, @"\t|\n|\r", "");
                return data;
            }
            catch (IOException)
            {
                throw new TimeoutException();
            }
            finally
            {
                mut.ReleaseMutex();
            }
        }

        public string write(string command)
        {
            byte[] msg = Encoding.UTF8.GetBytes(command);
            try
            {
                mut.WaitOne();
                clientStream.Write(msg, 0, msg.Length);
                string data = "ok";
                return data;
            }
            catch (IOException)
            {
                throw new TimeoutException();
            }
            finally
            {
                mut.ReleaseMutex();
            }
        }
    }
}
