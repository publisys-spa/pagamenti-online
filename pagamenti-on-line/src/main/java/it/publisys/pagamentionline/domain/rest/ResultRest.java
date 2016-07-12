package it.publisys.pagamentionline.domain.rest;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author mcolucci
 * @version 1.0
 * @since <pre>16/09/15</pre>
 */
public class ResultRest<T> {

    private boolean error;
    private String message;
    private T data;

    public ResultRest(T data) {
        this(false, null, data);
    }

    public ResultRest(boolean error, String message, T data) {
        this.error = error;
        this.message = message;
        this.data = data;
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
