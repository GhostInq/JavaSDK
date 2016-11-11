package co.pixpie.api;

import co.pixpie.api.bean.SdkAuthenticationBean;
import co.pixpie.api.exception.PixpieAuthenticationException;
import co.pixpie.api.image.CropAlignType;
import co.pixpie.api.image.ImageFormat;
import co.pixpie.api.image.ImageTransformation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by accord on 11/9/16.
 */
@RunWith(JUnit4.class)
public class PixpieAPIImplTest {

    private String originalRemoteImageUrl = "https://pp.vk.me/c626730/v626730256/2f3d8/11jxm6YGqkw.jpg";
    private String originalLocalImageUrl = "uploaded_image.jpg";

    private PixpieAPIImpl pixpieAPI;

    @Before
    public void before() throws PixpieAuthenticationException {

        pixpieAPI = mock(PixpieAPIImpl.class);

        String cdnUrl = "http://pixpie-demo.azureedge.net/test.com.test";
        SdkAuthenticationBean authBean = new SdkAuthenticationBean();
        authBean.setCdnUrl(cdnUrl);

        when(pixpieAPI.getAuthTokenBean(false)).thenReturn(authBean);
        when(pixpieAPI.getRemoteImageUrl(any(), any())).thenCallRealMethod();
        when(pixpieAPI.getImageUrl(any(), any())).thenCallRealMethod();

    }

    @Test
    public void test_success_remote_width_height_quality() {

        ImageTransformation transformation = new ImageTransformation()
                .withWidth(600)
                .withHeight(500)
                .withQuality(80);

        final String remoteImageUrl = pixpieAPI.getRemoteImageUrl(originalRemoteImageUrl, transformation);

        assertEquals("http://pixpie-demo.azureedge.net/test.com.test/remote/def/w_600,h_500,q_80/" +
                "https://pp.vk.me/c626730/v626730256/2f3d8/11jxm6YGqkw.jpg", remoteImageUrl);
    }

    @Test
    public void test_success_remote_width_height_quality_cropAlignType() {

        ImageTransformation transformation = new ImageTransformation()
                .withWidth(600)
                .withHeight(500)
                .withQuality(80)
                .withFormat(ImageFormat.WEBP)
                .withCropAlignType(CropAlignType.BOTTOM_LEFT);

        final String remoteImageUrl = pixpieAPI.getRemoteImageUrl(originalRemoteImageUrl, transformation);

        assertEquals("http://pixpie-demo.azureedge.net/test.com.test/remote/webp/w_600,h_500,q_80,c_bottom_left/" +
                "https://pp.vk.me/c626730/v626730256/2f3d8/11jxm6YGqkw.jpg", remoteImageUrl);

    }

    @Test
    public void test_success_remote_width() {

        ImageTransformation transformation = new ImageTransformation()
                .withWidth(600);

        final String remoteImageUrl = pixpieAPI.getRemoteImageUrl(originalRemoteImageUrl, transformation);

        assertEquals("http://pixpie-demo.azureedge.net/test.com.test/remote/def/w_600/" +
                "https://pp.vk.me/c626730/v626730256/2f3d8/11jxm6YGqkw.jpg", remoteImageUrl);


    }

    @Test
    public void test_success_remote_width_imageFormat() {

        ImageTransformation transformation = new ImageTransformation()
                .withWidth(600)
                .withFormat(ImageFormat.DEFAULT);

        final String remoteImageUrl = pixpieAPI.getRemoteImageUrl(originalRemoteImageUrl, transformation);

        assertEquals("http://pixpie-demo.azureedge.net/test.com.test/remote/def/w_600/" +
                "https://pp.vk.me/c626730/v626730256/2f3d8/11jxm6YGqkw.jpg", remoteImageUrl);


    }

    @Test
    public void test_success_remote_width_webp() {
        ImageTransformation transformation = new ImageTransformation()
                .withHeight(500)
                .withFormat(ImageFormat.WEBP);

        final String remoteImageUrl = pixpieAPI.getRemoteImageUrl(originalRemoteImageUrl, transformation);

        assertEquals("http://pixpie-demo.azureedge.net/test.com.test/remote/webp/h_500/" +
                "https://pp.vk.me/c626730/v626730256/2f3d8/11jxm6YGqkw.jpg", remoteImageUrl);
    }

    @Test
    public void test_success_remote_height_width_default_format_quality() {
        ImageTransformation transformation = new ImageTransformation()
                .withHeight(500)
                .withWidth(600)
                .withFormat(ImageFormat.DEFAULT)
                .withQuality(100);

        final String remoteImageUrl = pixpieAPI.getRemoteImageUrl(originalRemoteImageUrl, transformation);

        assertEquals("http://pixpie-demo.azureedge.net/test.com.test/remote/def/w_600,h_500,q_100/" +
                "https://pp.vk.me/c626730/v626730256/2f3d8/11jxm6YGqkw.jpg", remoteImageUrl);
    }

    @Test
    public void test_success_remote_width_default_format_quality() {
        ImageTransformation transformation = new ImageTransformation()
                .withWidth(600)
                .withFormat(ImageFormat.DEFAULT)
                .withQuality(100);

        final String remoteImageUrl = pixpieAPI.getRemoteImageUrl(originalRemoteImageUrl, transformation);

        assertEquals("http://pixpie-demo.azureedge.net/test.com.test/remote/def/w_600,q_100/" +
                "https://pp.vk.me/c626730/v626730256/2f3d8/11jxm6YGqkw.jpg", remoteImageUrl);
    }

    @Test
    public void test_success_remote_height_default_format_quality() {
        ImageTransformation transformation = new ImageTransformation()
                .withHeight(500)
                .withFormat(ImageFormat.DEFAULT)
                .withQuality(100);

        final String remoteImageUrl = pixpieAPI.getRemoteImageUrl(originalRemoteImageUrl, transformation);

        assertEquals("http://pixpie-demo.azureedge.net/test.com.test/remote/def/h_500,q_100/" +
                "https://pp.vk.me/c626730/v626730256/2f3d8/11jxm6YGqkw.jpg", remoteImageUrl);
    }

    @Test
    public void test_success_local_width_height() {
        ImageTransformation transformation = new ImageTransformation()
                .withHeight(500)
                .withWidth(600);

        final String localImageUrl = pixpieAPI.getImageUrl(originalLocalImageUrl, transformation);

        assertEquals("http://pixpie-demo.azureedge.net/test.com.test/local/def/w_600,h_500/uploaded_image.jpg",
                localImageUrl);
    }

    @Test
    public void test_success_local_width() {
        ImageTransformation transformation = new ImageTransformation()
                .withWidth(600);

        final String localImageUrl = pixpieAPI.getImageUrl(originalLocalImageUrl, transformation);

        assertEquals("http://pixpie-demo.azureedge.net/test.com.test/local/def/w_600/uploaded_image.jpg",
                localImageUrl);
    }

    @Test
    public void test_success_local_height() {
        ImageTransformation transformation = new ImageTransformation()
                .withHeight(500);

        final String localImageUrl = pixpieAPI.getImageUrl(originalLocalImageUrl, transformation);

        assertEquals("http://pixpie-demo.azureedge.net/test.com.test/local/def/h_500/uploaded_image.jpg",
                localImageUrl);
    }

    @Test
    public void test_success_local_width_height_default_format_quality() {
        ImageTransformation transformation = new ImageTransformation()
                .withFormat(ImageFormat.DEFAULT)
                .withHeight(500)
                .withWidth(600)
                .withQuality(80);

        final String localImageUrl = pixpieAPI.getImageUrl(originalLocalImageUrl, transformation);

        assertEquals("http://pixpie-demo.azureedge.net/test.com.test/local/def/w_600,h_500,q_80/uploaded_image.jpg",
                localImageUrl);
    }

    @Test
    public void test_success_local_format_quality() {
        ImageTransformation transformation = new ImageTransformation()
                .withFormat(ImageFormat.DEFAULT);

        final String localImageUrl = pixpieAPI.getImageUrl(originalLocalImageUrl, transformation);

        assertEquals("http://pixpie-demo.azureedge.net/test.com.test/local/def/w_0,h_0/uploaded_image.jpg",
                localImageUrl);
    }

    @Test
    public void test_success_remote_width_crop_top() {
        ImageTransformation transformation = new ImageTransformation()
                .withWidth(600)
                .withCropAlignType(CropAlignType.TOP);

        final String remoteImageUrl = pixpieAPI.getRemoteImageUrl(originalRemoteImageUrl, transformation);

        assertEquals("http://pixpie-demo.azureedge.net/test.com.test/remote/def/w_600,c_top/" +
                "https://pp.vk.me/c626730/v626730256/2f3d8/11jxm6YGqkw.jpg", remoteImageUrl);
    }



}
