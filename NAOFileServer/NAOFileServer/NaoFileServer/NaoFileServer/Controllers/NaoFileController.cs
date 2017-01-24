using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web;
using System.Web.Http;

namespace NaoFileServer.Controllers
{
    public class NaoFileController : ApiController
    {
#if DEBUG
        private string filePath = @"\\psf\Home\Documents\3.Klasse\SYP\NAOFileServer\NAOFileServer\Actions";
        //private string filePath = @"\NAOTestFiles";
#else
            private string filePath = @"C:\Daten\NAO";
#endif
        // GET api/values
        [Route("api/files/names/{count:int}")]
        public HttpResponseMessage GetFileNamesByIndex(int count)
        {
            HttpResponseMessage message = null;

            string[] files = Directory.GetFiles(filePath);

            if (files != null)
            {
                if (files.Length >= count)
                {
                    message = this.Request.CreateResponse(HttpStatusCode.OK, files[count]);
                }
                else
                {
                    message = this.Request.CreateResponse(HttpStatusCode.BadRequest);
                }
            }

            return message;
        }

        [Route("api/files/names")]
        public string[] GetAllFileNames()
        {
            string[] files = Directory.GetFiles(filePath);

            return files;
        }

        // GET api/values/5
        [Route("api/files/{fileName}")]
        public HttpResponseMessage GetByFileName(string fileName)
        {
            HttpResponseMessage result = null;

            if (!String.IsNullOrEmpty(fileName))
            {
                string[] strs = fileName.Split('.');

                if (strs.Length > 0)
                {
                    string requestedFilePath = filePath + @"\" + strs[0] + ".xar";
                    if (File.Exists(requestedFilePath))
                    {
                        // sende Dateidaten an Client:
                        byte[] bytes = File.ReadAllBytes(requestedFilePath);


                        result = Request.CreateResponse(HttpStatusCode.OK);
                        result.Content = new ByteArrayContent(bytes);
                        result.Content.Headers.Add("X-DownloadedFile", requestedFilePath);
                    }
                    else
                    {
                        string errMsg = "File not found.";
                        result = new HttpResponseMessage(HttpStatusCode.NotFound)
                        {
                            Content = new StringContent(errMsg),
                            ReasonPhrase = errMsg
                        };
                        throw new HttpResponseException(result);
                    }
                }
                else
                {
                    string errMsg = "Unsupported name of file.";
                    result = new HttpResponseMessage(HttpStatusCode.BadRequest)
                    {
                        Content = new StringContent(errMsg),
                        ReasonPhrase = errMsg,
                    };
                    throw new HttpResponseException(result);
                }
            }
            else
            {
                string errMsg = "Filename must not be null.";
                result = new HttpResponseMessage(HttpStatusCode.BadRequest)
                {
                    Content = new StringContent(errMsg),
                    ReasonPhrase = errMsg,
                };
                throw new HttpResponseException(result);
            }

            return result;
        }

        [Route("api/files/{index:int}")]

        public HttpResponseMessage GetContentByIndex(int index){

            HttpResponseMessage message = null;

            string[] files = Directory.GetFiles(filePath);

            if (files != null)
            {
                if (files.Length >= index)
                {
                    if (File.Exists(files[index]))
                    {
                        byte[] bytes = File.ReadAllBytes(files[index]);

                        message = Request.CreateResponse(HttpStatusCode.OK);
                        message.Content = new ByteArrayContent(bytes);
                        message.Content.Headers.Add("File", files[index]);
                    }
                    else
                    {
                        string errorMessage = "File not found";
                        message = new HttpResponseMessage(HttpStatusCode.NotFound)
                        {
                            Content = new StringContent(errorMessage),
                            ReasonPhrase = errorMessage
                        };
                        throw new HttpResponseException(message);
                    }
                }
                else
                {
                    string errorMessage = "Unsupported name of file";
                    message = new HttpResponseMessage(HttpStatusCode.BadRequest)
                    {
                        Content = new StringContent(errorMessage),
                        ReasonPhrase = errorMessage
                    };
                    throw new HttpResponseException(message);
                }
            }
            else
            {
                string errorMessage = "Filename must not be null";
                message = new HttpResponseMessage(HttpStatusCode.BadRequest)
                {
                    Content = new StringContent(errorMessage),
                    ReasonPhrase = errorMessage
                };
                throw new HttpResponseException(message);
            }
            
            return message;
        }

        //[Route("api/file/{filename}")]

        //public HttpResponseMessage PostSomeData(string newFilename)
        //{
        //    HttpResponseMessage message = null;
        //    var request = (HttpWebRequest)WebRequest.Create("http://localhost:62151/");

        //    if (newFilename != null)
        //    {
        //        request.Method = "POST";
        //        request.ContentType = "";
        //        request.ContentLength = 0;
        //    }
        //    else
        //    {
        //        message = new HttpResponseMessage(HttpStatusCode.BadRequest);
        //    }

        //    return message;
        //}
    }
}