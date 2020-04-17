package wtf.thepalbi.utils;

/**
 * Exception raised when some feature has not yet been implemented.
 */
public class FeatureNotImplementedException extends RuntimeException {
    private final String featureDescription;

    public FeatureNotImplementedException(String featureDescription) {
        this.featureDescription = featureDescription;
    }

    @Override
    public String getMessage() {
        return String.format("The feature '%s' has not been implemented yet.", this.featureDescription);
    }
}
