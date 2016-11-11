package co.pixpie.api;

import co.pixpie.api.bean.ListItemsBean;
import co.pixpie.api.exception.PixpieEmptyDeleteBatchException;
import co.pixpie.api.image.ImageTransformation;

import java.io.InputStream;
import java.util.List;

/**
 * Created by accord on 2/11/16.
 */
public interface PixpieAPI {

    String getImageUrl(String imagePath, ImageTransformation transformation);

    String getRemoteImageUrl(String imageUrl, ImageTransformation transformation);

    boolean uploadImage(byte[] image, String contentType, String encodedImageName, String innerPath, boolean async);

    boolean uploadImage(InputStream imageStream, String contentType, String encodedImageName, String innerPath, boolean async);

    boolean deleteItems(String innerPath, List<String> images, List<String> folders) throws PixpieEmptyDeleteBatchException;

    ListItemsBean listItems(String innerPath);

}