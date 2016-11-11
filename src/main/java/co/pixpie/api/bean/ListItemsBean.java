package co.pixpie.api.bean;

import java.util.List;

/**
 * Created by accord on 11/8/16.
 */
public class ListItemsBean {

    private List<String> images;
    private List<String> folders;

    public ListItemsBean() {
    }

    public ListItemsBean(List<String> images, List<String> folders) {
        this.images = images;
        this.folders = folders;
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
        if (!(o instanceof ListItemsBean)) return false;

        ListItemsBean that = (ListItemsBean) o;

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
        return "ListItemsBean{" +
                "images=" + images +
                ", folders=" + folders +
                '}';
    }
}
