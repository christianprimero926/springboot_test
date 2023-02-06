package org.cospina.test.springboot.app.exceptions;

public class InsufficientMoneyException extends RuntimeException{
    public InsufficientMoneyException(String message) {
        super(message);
    }
}
