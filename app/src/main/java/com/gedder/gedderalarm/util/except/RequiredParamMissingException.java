/*
 * USER: mslm
 * DATE: March 20, 2017
 */

package com.gedder.gedderalarm.util.except;


/**
 * Thrown when a certain required parameter is null or some other "missing"-like value.
 */
public class RequiredParamMissingException extends RuntimeException {
    public RequiredParamMissingException() {
        super();
    }

    public RequiredParamMissingException(String message) {
        super(message);
    }
}