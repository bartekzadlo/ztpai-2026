package pl.edu.pk.gamelibrary.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " o id=" + id + " nie istnieje");
    }

    public ResourceNotFoundException(String resourceName, String identifier) {
        super(resourceName + " (" + identifier + ") nie istnieje");
    }
}
