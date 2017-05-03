package org.ifdc.db.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.agmip.common.Functions;
//import org.agmip.common.Functions;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author mike
 */
@Path("/file")
public class Upload {

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {

        String uploadedFileLocation = "uploaded/" + fileDetail.getFileName();
        System.out.println(fileDetail.getFileName());
        System.out.println(fileDetail.toString());
        System.out.println(fileDetail.getParameters().values().toString());

        // save it
        writeToFile(uploadedInputStream, uploadedFileLocation);

        String output = "File uploaded to : " + uploadedFileLocation;

        return Response.status(200).entity(output).build();

    }

    // save uploaded file to new location
    private void writeToFile(InputStream uploadedInputStream,
            String uploadedFileLocation) {

        Functions.revisePath(uploadedFileLocation);
        File dir = new File(uploadedFileLocation);
        try {
            
            OutputStream out = new FileOutputStream(dir);
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
            System.out.println("File was wrote to " + dir.getAbsolutePath());
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    

    // save uploaded file to new location
    private void writeToDropBox(InputStream uploadedInputStream,
            String uploadedFileLocation) {

        File dir = new File(uploadedFileLocation);
        if (!dir.getParentFile().exists()) {
            dir.mkdirs();
        }
        try {
            
            OutputStream out = new FileOutputStream(dir);
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
            System.out.println("File was wrote to " + dir.getAbsolutePath());
        } catch (IOException e) {

            e.printStackTrace();
        }

    }
}
