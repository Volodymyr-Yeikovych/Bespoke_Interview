package gg.v.yeikovych.interview_bespoke.exception;

public class NoTokenAvailableException extends RuntimeException{
    public NoTokenAvailableException() {
        super();
    }

    public NoTokenAvailableException(String message) {
        super(message);
    }
}
