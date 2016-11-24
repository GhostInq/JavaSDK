package co.pixpie.api;

import co.pixpie.api.bean.DeleteItemsBean;
import co.pixpie.api.bean.ListItemsBean;
import co.pixpie.api.bean.SdkAuthenticationBean;
import co.pixpie.api.exception.PixpieAuthenticationException;
import co.pixpie.api.exception.PixpieEmptyDeleteBatchException;
import co.pixpie.api.image.CropAlignType;
import co.pixpie.api.image.ImageFormat;
import co.pixpie.api.image.ImageLocationType;
import co.pixpie.api.image.ImageTransformation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.Validate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by accord on 2/10/16.
 */
public class PixpieAPIImpl implements PixpieAPI {

    private final Logger logger = LoggerFactory.getLogger(PixpieAPIImpl.class);
    private static final int JAVA_SERVER_SDK_TYPE_ID = 1;
    private static final String JAVA_SDK_VERSION = "1.0.1";

    ObjectMapper mapper = new ObjectMapper();

    final String reverseUrlId;
    final String secretKey;
    final String salt;
    final String address;

    final String authTokenUrl;
    final String uploadImageAsyncUrl;
    final String uploadImageUrl;
    final String deleteItemsUrl;
    final String listItemsUrl;

    SdkAuthenticationBean authBean = null;
    SSLConnectionSocketFactory sslCSF = null;
    /**
     *
     * @param reverseUrlId unique application in play market or app store
     * @param secretKey generated key for each application, provided by Pixpie
     * @param salt salt token for security, provided by Pixpie
     * @throws PixpieAuthenticationException if authentication failed
     */
    public PixpieAPIImpl(@NonNull String reverseUrlId, @NonNull String secretKey,
                         @NonNull String salt) throws PixpieAuthenticationException {
        this(reverseUrlId, secretKey, salt, "https", "api.pixpie.co", 9443);
    }

    /**
     *
     * @param reverseUrlId unique application in play market or app store
     * @param secretKey generated key for each application, provided by Pixpie
     * @param salt salt token for security, provided by Pixpie
     * @param scheme http or https, default - https
     * @param host host or IP of optimization servers, default - api.pixpie.co
     * @param port port of of optimization servers, default - 9443
     * @throws PixpieAuthenticationException if authentication failed
     */
    public PixpieAPIImpl(@NonNull String reverseUrlId, @NonNull String secretKey,
                         @NonNull String salt, @NonNull String scheme,
                         @NonNull String host, Integer port) throws PixpieAuthenticationException {
        this.reverseUrlId = reverseUrlId;
        this.secretKey = secretKey;
        this.salt = salt;
        this.address = scheme + "://" + host
                + (port != null && port > 0
                    ? (":" + port)
                    : "");

        this.authTokenUrl = address + "/"
                + PixpieConstants.authUri;

        this.uploadImageAsyncUrl = address + "/"
                + PixpieConstants.uploadImageAsync
                + "/" + reverseUrlId;

        this.uploadImageUrl = address + "/"
                + PixpieConstants.uploadImage
                + "/" + reverseUrlId;

        this.deleteItemsUrl = address + "/"
                + PixpieConstants.serverUriDeleteBatch
                + "/" + reverseUrlId;

        this.listItemsUrl = address + "/"
                + PixpieConstants.listItems
                + "/" + reverseUrlId;

        this.mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);

        initSSLFactory();
        initAuthentication();
    }

    /**
     * @param imagePath        relative path to image that was uploaded to Pixpie, like 'someFolder/image_name.jpg
     * @param transformation   transformation behaviour
     * @return generated url of image on Pixpie
     */
    public String getImageUrl(@NonNull String imagePath, @NonNull ImageTransformation transformation) {
        return buildUrl(ImageLocationType.LOCAL, imagePath, transformation);
    }

    /**
     * @param imageUrl         URL of image, that is located on third party (remote servers)
     * @param transformation   transformation behaviour
     * @return generated url of image on Pixpie
     */
    public String getRemoteImageUrl(@NonNull String imageUrl, @NonNull ImageTransformation transformation) {
        return buildUrl(ImageLocationType.REMOTE, imageUrl, transformation);
    }

    /**
     * @param image            byte array that contains image
     * @param contentType      image type image/jpeg, image/png etc.
     * @param encodedImageName final image name on CDN
     * @param innerPath        path to image on CDN
     * @return operation finished successfully
     */
    public boolean uploadImage(@NonNull byte[] image, @NonNull String contentType,
                               @NonNull String encodedImageName, @NonNull String innerPath, boolean async) {
        return uploadImage(new ByteArrayInputStream(image), contentType, encodedImageName, innerPath, async);
    }

    /**
     * @param imageStream      input stream that contains image
     * @param contentType      image type image/jpeg, image/png etc.
     * @param encodedImageName final image name on CDN
     * @param innerPath        path to image on CDN
     * @return operation finished successfully
     */
    public boolean uploadImage(@NonNull InputStream imageStream, @NonNull String contentType,
                               @NonNull String encodedImageName, @NonNull String innerPath, boolean async) {

        logger.debug("uploadImage contentType = {}, encodedImageName = {}, innerPath = {}, async = {}",
                contentType, encodedImageName, innerPath, async);

        try {

            HttpEntity entity = MultipartEntityBuilder
                    .create()
                    .addBinaryBody("image", imageStream, ContentType.create(contentType), encodedImageName)
                    .build();

            String url = getImageUploadRequestUrl(innerPath, encodedImageName, async);

            logger.debug("uploadImage url = {}", url);

            return doPostRequest(url, entity);
        } catch (Exception e) {
            logger.error("uploadImage failed", e);
        }

        return false;
    }

    /**
     *
     * @param innerPath is relative path to folder where to list child items
     * @return list of images and folders with relative paths
     */
    @Override
    public ListItemsBean listItems(@NonNull String innerPath) {

        logger.debug("listItems, innerPath = {}", innerPath);

        try {

            try (CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(getSslCSF()).build()) {
                HttpGet httpGet = new HttpGet(getListItemsUrl(innerPath));

                SdkAuthenticationBean authTokenBean = getAuthTokenBean(false);
                httpGet.setHeader(PixpieConstants.authTokenHeader, authTokenBean.getAuthToken());

                HttpResponse response = httpClient.execute(httpGet);
                return mapper.readValue(response.getEntity().getContent(), ListItemsBean.class);
            }
        } catch (Exception ex) {
            logger.error("listItems failed", ex);
        }

        return null;
    }

    /**
     *
     * @param innerPath is used to create relative path of images and folders, innerPath can be empty
     * @param images if innerPath is empty - specify absolute path to images from root
     * @param folders if innerPath is empty - specify absolute path to folders from root
     * @return
     */
    public boolean deleteItems(String innerPath, List<String> images, List<String> folders) throws PixpieEmptyDeleteBatchException {

        logger.debug("deleteItems innerPath = {}, images = {}, folders = {}", innerPath, images, folders);

        if (images != null || folders != null) {

            DeleteItemsBean itemsBean = new DeleteItemsBean(innerPath, images, folders);

            try {
                HttpEntity entity = new StringEntity(mapper.writeValueAsString(itemsBean), ContentType.APPLICATION_JSON);
                return doDeleteRequest(getDeleteItemsUrl(), entity);
            } catch (Exception e) {
                logger.error("deleteItems failed", e);
            }
        } else {
            logger.debug("images and folders lists are empty");
        }

        return false;
    }

    private String buildUrl(ImageLocationType locationType, String location, ImageTransformation transformation) {

        validateTransformation(transformation);

        Integer width = transformation.getWidth();
        Integer height = transformation.getHeight();
        Integer quality = transformation.getQuality();
        ImageFormat format = transformation.getFormat();

        if (transformation.isOriginal()) {
            width = height = quality = null;
        }

        try {
            return getUrl(locationType, location, format, width, height, quality, transformation.getCropAlignType());
        } catch (Exception e) {
            // shouldn't happen
            logger.error("getUrl failed", e);
        }
        return null;
    }

    private void validateTransformation(ImageTransformation transformation) {

        if (transformation.getWidth() != null) {
            Validate.isTrue(transformation.getWidth() > 0, "Width should be greater than 0");
        }
        if (transformation.getHeight() != null) {
            Validate.isTrue(transformation.getHeight() > 0, "Height should be greater than 0");
        }
        if (transformation.getQuality() != null) {
            Validate.inclusiveBetween(1L, 100L, transformation.getQuality(),
                    "Quality should be greater than 0 and less or equal 100");
        }
        if (transformation.getWidth() == null && transformation.getHeight() == null &&
                transformation.getCropAlignType() != null) {
            Validate.isTrue(false, "Transformation could not have crop value set, when at least height or width " +
                    "is not provided");
        }
    }

    private String getUrl(ImageLocationType locationType, String location, ImageFormat format,
                          Integer width, Integer height, Integer quality, CropAlignType сropAlignType) throws PixpieAuthenticationException {

        // {pathToCdn}/{requestType}/{imgType}/{params}/.../path-to-image
        return getAuthTokenBean(false).getCdnUrl() + "/" + locationType.getLocationString() +
                "/" + format.getFormatString() + "/" + getParamsString(width, height, quality, сropAlignType) +
                "/" + location;
    }

    private String getParamsString(Integer width, Integer height, Integer quality, CropAlignType cropAlignType) {
        StringBuilder sb = new StringBuilder();
        appendIntegerParam(sb, "w_", width);
        appendIntegerParam(sb, "h_", height);
        appendIntegerParam(sb, "q_", quality);
        appendCropAlignTypeParam(sb, "c_", cropAlignType);
        if (sb.length() == 0) {
            sb.append("w_0,h_0");
        }
        return sb.toString();
    }

    private void appendIntegerParam(StringBuilder sb, String prefix, Integer value) {
        if (value != null) {
            if (sb.length() != 0) {
                sb.append(',');
            }
            sb.append(prefix).append(value);
        }
    }

    private void appendCropAlignTypeParam(StringBuilder sb, String appendValue, CropAlignType cropAlignType) {
        if (cropAlignType != null) {
            sb.append(',').append(appendValue).append(cropAlignType.getUrlValue());
        }
    }

    boolean doPostRequest(@NonNull String url, @NonNull HttpEntity entity) {
        return doRequestWrapper(new HttpPost(url), entity);
    }

    boolean doDeleteRequest(@NonNull String url, @NonNull HttpEntity entity) {
        HttpEntityEnclosingRequestBase httpDelete = new HttpEntityEnclosingRequestBase() {
            @Override
            public String getMethod() {
                return "DELETE";
            }
        };

        try {
            httpDelete.setURI(new URI(url));
        } catch (URISyntaxException e) {
            logger.error("doDeleteRequest failed", e);
        }

        return doRequestWrapper(httpDelete, entity);
    }

    boolean doRequestWrapper(@NonNull HttpRequestBase http, @NonNull HttpEntity entity) {

        int responseStatus = doRequest(http, entity, true);

        if (responseStatus != HttpStatus.SC_INTERNAL_SERVER_ERROR) {

            logger.debug("Response status : {}", responseStatus);

            // token expired case
            if (responseStatus == HttpStatus.SC_FORBIDDEN) {

                authBean = null;

                responseStatus = doRequest(http, entity, true);
            }

            return responseStatus == HttpStatus.SC_OK;

        } else {
            logger.error("doRequest returned server error");
            return false;
        }
    }

    int doRequest(@NonNull HttpRequestBase http, HttpEntity entity, boolean authenticate) {

        try (CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(getSslCSF()).build()) {

            if (authenticate) {

                SdkAuthenticationBean authTokenBean = getAuthTokenBean(false);

                http.setHeader(PixpieConstants.authTokenHeader, authTokenBean.getAuthToken());

                logger.debug("doRequest, authTokenBean = {}", authTokenBean.toString());
            }

            if (entity != null) {
                ((HttpEntityEnclosingRequestBase) http).setEntity(entity);
            }

            HttpResponse response = httpClient.execute(http);
            int responseStatus = response.getStatusLine().getStatusCode();

            logger.debug("Response status : {}", responseStatus);

            return responseStatus;

        } catch (Exception ex) {
            logger.error("doRequest failed", ex);
            return HttpStatus.SC_INTERNAL_SERVER_ERROR;
        }
    }

    String generateAuthHash(long timestamp) {
        String origin = secretKey + salt + timestamp;
        return DigestUtils.sha256Hex(origin);
    }

    String getAuthTokenUrl() {
        return this.authTokenUrl;
    }

    String getImageUploadRequestUrl(@NonNull String innerPath, @NonNull String imageName, boolean async) {

        StringBuilder builder;

        if (async) {
            builder = new StringBuilder(uploadImageAsyncUrl);
        } else {
            builder = new StringBuilder(uploadImageUrl);
        }

        builder.append(innerPath);
        builder.append("/").append(imageName);

        return builder.toString();
    }

    String getDeleteItemsUrl() {
        return deleteItemsUrl;
    }

    String getListItemsUrl(String innerPath) {
        return listItemsUrl + "/" + innerPath;
    }

    SSLConnectionSocketFactory getSslCSF() {
        return sslCSF;
    }

    void initSSLFactory() throws PixpieAuthenticationException {

        try {
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, (chain, authType) -> true)
                    .build();

            this.sslCSF = new SSLConnectionSocketFactory(sslContext);
        } catch (Exception e) {
            logger.error("initSSLFactory failed", e);
            throw new PixpieAuthenticationException("SSL connection socket factory failed during the init");
        }
    }

    void initAuthentication() throws PixpieAuthenticationException {
        getAuthTokenBean(false);
    }

    /**
     *
     * @param update force request to update AuthToken (request new from server)
     * @return SdkAuthenticationBean contains information regarding CDN base URL and current AuthToken to talk with server
     * @throws PixpieAuthenticationException
     */
    SdkAuthenticationBean getAuthTokenBean(boolean update) throws PixpieAuthenticationException {

        logger.debug("getAuthTokenBean update = {}", update);

        if (authBean == null || update) {

            try {

                long timestamp = System.currentTimeMillis() / 1000; // get seconds

                final HttpEntity entity = MultipartEntityBuilder
                        .create()
                        .addTextBody("reverseUrlId", reverseUrlId)
                        .addTextBody("timestamp", String.valueOf(timestamp))
                        .addTextBody("hash", generateAuthHash(timestamp))
                        .addTextBody("serverSdkType", String.valueOf(JAVA_SERVER_SDK_TYPE_ID))
                        .addTextBody("sdkVersion", JAVA_SDK_VERSION)
                        .build();

                try (CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(getSslCSF()).build()) {
                    HttpPost httpPost = new HttpPost(getAuthTokenUrl());
                    httpPost.setEntity(entity);
                    HttpResponse response = httpClient.execute(httpPost);
                    authBean = mapper.readValue(response.getEntity().getContent(), SdkAuthenticationBean.class);
                }

            } catch (Exception ex) {
                logger.error("getAuthTokenBean failed", ex);
                throw new PixpieAuthenticationException("Authentication failed");
            }
        }

        return authBean;
    }
}
