package red.tetracube.upspulsar.dto.exceptions;

public sealed class UPSPulsarException extends Exception permits
        UPSPulsarException.EntityExistsException,
        UPSPulsarException.EntityNotFoundException,
        UPSPulsarException.RepositoryException,
        UPSPulsarException.InternalException{

    public static final class RepositoryException extends UPSPulsarException {
        public RepositoryException(Throwable throwable) {
            super(throwable);
        }
    }

    public static final class EntityNotFoundException extends UPSPulsarException {
        public EntityNotFoundException(String message) {
            super(message);
        }
    }

    public static final class EntityExistsException extends UPSPulsarException {
        public EntityExistsException(String message) {
            super(message);
        }
    }

    public static final class InternalException extends UPSPulsarException {
        public InternalException(String message) {
            super(message);
        }
    }

    protected UPSPulsarException(Throwable throwable) {
        super(throwable);
    }

    protected UPSPulsarException(String message) {
        super(message);
    }

}
