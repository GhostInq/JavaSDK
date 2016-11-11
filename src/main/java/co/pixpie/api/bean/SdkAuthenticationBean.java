package co.pixpie.api.bean;

/**
 * Created by accord on 2/10/16.
 */
public class SdkAuthenticationBean {

    private String authToken;
    private String cdnUrl;

    public SdkAuthenticationBean() {
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getCdnUrl() {
        return cdnUrl;
    }

    public void setCdnUrl(String cdnUrl) {
        this.cdnUrl = cdnUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SdkAuthenticationBean)) return false;

        SdkAuthenticationBean that = (SdkAuthenticationBean) o;

        if (getAuthToken() != null ? !getAuthToken().equals(that.getAuthToken()) : that.getAuthToken() != null)
            return false;
        return !(getCdnUrl() != null ? !getCdnUrl().equals(that.getCdnUrl()) : that.getCdnUrl() != null);

    }

    @Override
    public int hashCode() {
        int result = getAuthToken() != null ? getAuthToken().hashCode() : 0;
        result = 31 * result + (getCdnUrl() != null ? getCdnUrl().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SdkAuthenticationBean{" +
                "authToken='" + authToken + '\'' +
                ", cdnUrl='" + cdnUrl + '\'' +
                '}';
    }
}
