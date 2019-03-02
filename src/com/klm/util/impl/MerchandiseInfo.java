package com.klm.util.impl;

import com.klm.cons.impl.CSHouseException;
import com.klm.persist.Merchandise;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 12/17/11
 * Time: 11:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class MerchandiseInfo {

    private Map<Merchandise, Double> quantityCounter;

    public MerchandiseInfo() {
        reset();
    }

    public void reset() {
        quantityCounter = new HashMap<Merchandise, Double>();
    }

    public void addMerchandise(final Merchandise merchandise, final double quantity) throws CSHouseException {
        if (quantity < 0.0) {
            throw new CSHouseException(
                    new IllegalArgumentException("Quantity of the merchandise can not be negative " + quantity));
        }
        for (final Merchandise existingMerchandise : quantityCounter.keySet()) {
            if (existingMerchandise.getId().equals(merchandise.getId())) {
                final double currentQuantity = quantityCounter.get(existingMerchandise).doubleValue() + quantity;
                quantityCounter.put(existingMerchandise, new Double(currentQuantity));
                return;
            }
        }
        quantityCounter.put(merchandise, new Double(quantity));
    }

    public Set<Merchandise> getMerchandiseSet() {
        return quantityCounter.keySet();
    }

    public Map<Merchandise, Double> getQuantityCounter() {
        return quantityCounter;
    }

    public void addMerchandiseCounter(final MerchandiseInfo anotherInfo) {
        for (final Merchandise currentMerchandise : quantityCounter.keySet()) {
            for (final Merchandise merchandise : anotherInfo.getMerchandiseSet()) {
                if (currentMerchandise.getId() == merchandise.getId()) {
                    final double currentQuantity = quantityCounter.get(merchandise).doubleValue() + anotherInfo.getQuantityCounter().get(merchandise).doubleValue();
                    quantityCounter.put(merchandise, new Double(currentQuantity));
                    anotherInfo.getQuantityCounter().remove(merchandise);
                    break;
                }
            }
        }

        if(!anotherInfo.getMerchandiseSet().isEmpty()){
            for(final Merchandise merchandise : anotherInfo.getMerchandiseSet()){
                quantityCounter.put(merchandise, anotherInfo.getQuantityCounter().get(merchandise));
            }
        }
    }

}
