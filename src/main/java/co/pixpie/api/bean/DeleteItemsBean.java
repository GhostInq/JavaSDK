package co.pixpie.api.bean;

import co.pixpie.api.exception.PixpieEmptyDeleteBatchException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by accord on 1/15/16.
 */
public class DeleteItemsBean {

    private List<String> images = new ArrayList<>();
    private List<String> folders = new ArrayList<>();

    public DeleteItemsBean(String innerPath, List<String> images, List<String> folders) throws PixpieEmptyDeleteBatchException {
        if (images == null) {
            this.images = Collections.emptyList();
        } else {
            this.images = images;
            clearNulls(this.images);
        }
        if (folders == null) {
            this.folders = Collections.emptyList();
        } else {
            this.folders = folders;
            clearNulls(this.folders);
        }

        if (!this.images.isEmpty() || !this.folders.isEmpty()) {
            if (innerPath.length() > 0) {
                addInnerPath(innerPath, this.images);
                addInnerPath(innerPath, this.folders);
            }
        } else {
            throw new PixpieEmptyDeleteBatchException("Delete list of images and folders are empty or contain only nulls");
        }
    }

    void clearNulls(List<String> list) {
        while(list.remove(null));
    };

    void addInnerPath(String innerPath, List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            if (!s.startsWith("/") && !innerPath.endsWith("/")) {
                list.set(i, innerPath + "/" + s);
            } else {
                list.set(i, innerPath + s);
            }
        }
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getFolders() {
        return folders;
    }

    public void setFolders(List<String> folders) {
        this.folders = folders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeleteItemsBean)) return false;

        DeleteItemsBean that = (DeleteItemsBean) o;

        if (getImages() != null ? !getImages().equals(that.getImages()) : that.getImages() != null) return false;
        return !(getFolders() != null ? !getFolders().equals(that.getFolders()) : that.getFolders() != null);

    }

    @Override
    public int hashCode() {
        int result = getImages() != null ? getImages().hashCode() : 0;
        result = 31 * result + (getFolders() != null ? getFolders().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DeleteItemsBean{" +
                "images=" + images +
                ", folders=" + folders +
                '}';
    }
}
