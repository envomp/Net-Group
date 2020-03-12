package ee.suveulikool.netgroup.demo.exception;

public class PersonExistsException extends Exception {
    public PersonExistsException(String message) {
        super(message);
    }
}
