package org.keycloak.performance.iteration;

import java.util.AbstractList;
import java.util.List;

/**
 *
 * @author tkyjovsk
 * @param <XT> type of X-list items
 * @param <YT> type of Y-list items
 */
public abstract class FlattenedListOfLists<XT, YT> extends AbstractList<YT> {

    public abstract List<XT> getXList();

    @Override
    public int size() {
        return getXList().size() * getYListSize();
    }

    @Override
    public YT get(int index) {
        int x = index % getXList().size();
        int y = index / getXList().size();
        return getYList(getXList().get(x)).get(y);
    }

    public abstract List<YT> getYList(XT parentEntity);

    public abstract int getYListSize();

}
