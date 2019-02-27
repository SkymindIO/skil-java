package ai.skymind.models;

import ai.skymind.ApiException;

/*
 * An interface to serve as a callback for model stop function
 */
public interface CallbackInterface {
    void run() throws ApiException;
}
