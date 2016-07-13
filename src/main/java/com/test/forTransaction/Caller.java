package com.test.forTransaction;

import javax.ejb.Remote;
import java.util.concurrent.Callable;

/**
 * Created by oksdud on 25.05.2016.
 */

public  interface Caller {
    public <V> V call(Callable<V> callable) throws Exception;
}
