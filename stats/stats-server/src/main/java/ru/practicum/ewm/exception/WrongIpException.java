package ru.practicum.ewm.exception;

public class WrongIpException extends RuntimeException {
    public WrongIpException(String message) {
        super(message);
    }
}
