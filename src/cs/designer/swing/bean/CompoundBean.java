package cs.designer.swing.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/28/12
 * Time: 10:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class CompoundBean extends LocationBean {
    private List<HouseBean> houses;
    private Set<LayoutHouseBean> layoutHouses = new HashSet<LayoutHouseBean>();

    public CompoundBean(String compoundCode, String compoundName,
                        LocationType locationType) {
        super(compoundCode, compoundName, locationType);
        houses = new ArrayList<HouseBean>();
    }

    public CompoundBean(String compoundCode, String compoundName) {
        super(compoundCode, compoundName);
        houses = new ArrayList<HouseBean>();
    }

    public void addHouseLayoitHouse(LayoutHouseBean layoutHouseBean) {
        layoutHouseBean.setCompoundCode(getCode());
        this.layoutHouses.add(layoutHouseBean);
    }

    public void addHouse(final HouseBean house) {
        houses.add(house);
    }

    public List<HouseBean> getHouses() {
        return houses;
    }

    public Set<LayoutHouseBean> getLayoutHouses() {
        return layoutHouses;
    }
}
