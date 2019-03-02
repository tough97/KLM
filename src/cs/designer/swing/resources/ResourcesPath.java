package cs.designer.swing.resources;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 1/3/12
 * Time: 9:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourcesPath {
    public static URL getResourcesUrl(String fileName) {
        URL image = ResourcesPath.class.getResource(fileName);
        return image;
    }
}
