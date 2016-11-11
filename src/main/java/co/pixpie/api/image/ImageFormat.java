package co.pixpie.api.image;

public enum ImageFormat {

    DEFAULT("def"), WEBP("webp");

    private final String formatString;

    ImageFormat(String formatString) {
        this.formatString = formatString;
    }

    public String getFormatString() {
        return formatString;
    }
}
