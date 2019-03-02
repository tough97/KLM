package cs.designer.io.net;

import cs.designer.swing.bean.PageBean;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/25/12
 * Time: 1:26 PM
 * To change this template use File | Settings | File Templates.
 */
public interface NetOperation {
    public static final String QUERY_RS_COUNT = "query_cnt";

    public boolean upload();

    public void delete();

    public void update();

    public void select();

    public void list(final PageBean pages);

    public void getPages(final PageBean pageBean);
}
