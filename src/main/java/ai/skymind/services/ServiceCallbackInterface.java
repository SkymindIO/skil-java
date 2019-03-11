package ai.skymind.services;

import ai.skymind.ApiException;

/**
 * An interface to service as a callback for model start function
 */
public interface ServiceCallbackInterface {
    void run(Service service) throws ApiException, InterruptedException;
}
