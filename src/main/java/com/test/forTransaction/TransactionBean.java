package com.test.forTransaction;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.concurrent.Callable;

/**
 * Created by oksdud on 25.05.2016.
 */

@Stateless(name = "TransactionBean")
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class TransactionBean implements Caller {

    public <V> V call(Callable<V> callable) throws Exception {
        return callable.call();
    }
}
/* Example of usage
 transactionalCaller.call(new Callable() {
public Object call() throws Exception {
        preparePaymentData();
        return null;
        }
        });
*/
