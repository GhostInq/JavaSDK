package co.pixpie.api.image;

public enum ImageLocationType {

    LOCAL("local"), REMOTE("remote");

    private final String locationString;

    ImageLocationType(String locationString) {
        this.locationString = locationString;
    }

    public String getLocationString() {
        return locationString;
    }
}
