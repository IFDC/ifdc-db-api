package org.ifdc.api;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author mike
 */
@Path("read")
public class Read {
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String readAll() {
        return "ALL";
    }
    
    @GET
    @Path("xlsx")
    @Produces(MediaType.TEXT_PLAIN)
    public String readXlsx() {
        StringBuilder ret = new StringBuilder();
        File dir = new File("uploaded");
        for (File f : dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("xlsx");
            }
        })) {
            
            ret.append(f.getName()).append("<br />");
            
        }
        return ret.toString();
    }
}
