package org.jmxtrans.agent.openfalcon;

import java.util.List;

/**
 * Created by lyf on 2017/8/15.
 */
public interface AggregateMethod<T> {
    public T execute(List<T> t);
}
