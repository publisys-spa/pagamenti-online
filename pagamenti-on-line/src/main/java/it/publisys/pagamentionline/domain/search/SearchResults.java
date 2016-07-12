package it.publisys.pagamentionline.domain.search;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mcolucci
 * @param <T>
 */
public class SearchResults<T> {

    private String type;
    private List<T> records;

    public SearchResults(String type, List<T> records) {
        if (records == null) {
            records = new ArrayList<>();
        }

        this.records = records;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public List<T> getRecords() {
        return records;
    }

    public int getSize() {
        return this.records.size();
    }

    public boolean isEmpty() {
        return this.records.isEmpty();
    }

}
