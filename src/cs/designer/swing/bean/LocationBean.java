package cs.designer.swing.bean;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Iterator;

import cs.designer.swing.resources.ResourcesPath;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/26/12
 * Time: 5:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class LocationBean implements CoBean {
    public static LocationBean YUN_NAN_LOCATION;
    public static LocationBean OTHER_LOCATION;
    public static LocationBean PROVINCES_LOCATION;

    public enum LocationType {
        PROVINCE, CITY, COMPOUND
    }

    protected String locationName;
    protected String locationCode;
    private LocationBean parent;
    private List<LocationBean> jurisdicLocations = new ArrayList<LocationBean>();
    public LocationType locationType;

    static {
        PROVINCES_LOCATION =new LocationBean("","地区");

      SAXReader reader = new SAXReader();
    Document document = null;
    try {
        document = reader.read(ResourcesPath.getResourcesUrl("province.xml"));
    } catch (DocumentException e) {
        e.printStackTrace();
    }
    Element rootElm = document.getRootElement();
        List list = rootElm.elements();
        for (Object obj : list) {
            Element element = (Element) obj;
            final LocationBean provinceLocationBean=new LocationBean(element.attributeValue("id"),
                    element.elementText("name"),LocationBean.LocationType.PROVINCE);
            PROVINCES_LOCATION.addjurisdicLocation(provinceLocationBean);
            Iterator<Element> it = element.elementIterator("city");
            while (it.hasNext()) {
                Element childElement = it.next();
                provinceLocationBean.addjurisdicLocation( new LocationBean(childElement.attributeValue("id"),
                         childElement.elementText("name"), LocationBean.LocationType.CITY));
            }
        }
    }

    public LocationBean() {

    }

    public LocationBean(final String locationCode,
                        final String locationName,
                        final LocationType locationType) {
        this(locationCode, locationName);
        this.locationType = locationType;

    }

    public LocationBean(String locationCode, String locationName) {
        this.locationCode = locationCode;
        this.locationName = locationName;

    }

    public void addjurisdicLocation(LocationBean jurisdicLocation) {
        if (!jurisdicLocations.contains(jurisdicLocation)) {
            jurisdicLocations.add(jurisdicLocation);
            jurisdicLocation.setParent(this);
        }
    }


    public List<LocationBean> getJurisdicLocations() {
        return jurisdicLocations;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String createDescription() {
        return "";
    }

    public String getCode() {
        return locationCode;
    }

    public String getName() {
        return locationName;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public String toString() {
        return locationName;
    }

    public LocationBean getParent() {
        return parent;
    }

    public void setParent(LocationBean parent) {
        this.parent = parent;
    }
}
