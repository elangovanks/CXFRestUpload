package com.sample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.activation.DataHandler;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;

public class UploadServiceImpl {
	
	@GET
	@Path("/health")
	public String getHealth() {
		return "I am good!";
	}

   @POST
   @Path("/uploadFile")
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   public Response uploadFile(List<Attachment> attachments, @Context HttpServletRequest request) {
      for (Attachment attachment : attachments) {
         DataHandler handler = attachment.getDataHandler();
         try {
            InputStream stream = handler.getInputStream();
            MultivaluedMap<String, String> map = attachment.getHeaders();
            System.out.println("fileName Here" + getFileName(map));
            OutputStream out = new FileOutputStream(new File("C:/uploads/" + getFileName(map)));

            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = stream.read(bytes)) != -1) {
               out.write(bytes, 0, read);
            }
            stream.close();
            out.flush();
            out.close();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      return Response.ok("file uploaded").build();
   }

   private String getFileName(MultivaluedMap<String, String> header) {
      String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
      for (String filename : contentDisposition) {
         if ((filename.trim().startsWith("filename"))) {
            String[] name = filename.split("=");
            String exactFileName = name[1].trim().replaceAll("\"", "");
            return exactFileName;
         }
      }
      return "unknown";
   }
}
