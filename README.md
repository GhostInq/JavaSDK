# Pixpie Java SDK #

https://www.pixpie.co

## What is it for? ##

Pixpie is a Platform as a Service for image optimization and manipulation.

Java SDK provides API methods implementation to access Pixpie REST API to use it for server-to-server integration. 

## How to start? ##

Check [Getting started](https://pixpie.atlassian.net/wiki/display/DOC/Getting+started) guide and [register](https://cloud.pixpie.co/registration) your account

## Add dependency ##

Check last released version on [Bintray](https://dl.bintray.com/accord/pixpie-java/co/pixpie/api/java/pixpie-java/).

Update your Maven/Gradle files:

``` gradle
<repositories>
    <repository>
        <id>bintray</id>
        <name>Bintray repo</name>
        <url>https://dl.bintray.com/accord/pixpie-java</url>
    </repository>
</repositories>

...

<dependencies>
  ... 
  <dependency>
    <groupId>co.pixpie.api.java</groupId>
    <artifactId>pixpie-java</artifactId>
    <version>{VERSION}</version>
  </dependency>
  ...
</dependencies>  

``` 

``` gradle
repositories {
    ...
    maven {
        url  "https://dl.bintray.com/accord/pixpie-java"
    }
    ...
}

dependencies {
    ...
    compile 'co.pixpie.api.java:pixpie-java:{VERSION}'
    ...
}
```

Another way is add source code as the module in project.

### Authentication ###

After [creation of new application](https://pixpie.atlassian.net/wiki/display/DOC/Create+application),
use Bundle ID (reverse url id) and Secret key in static authenticate method.

``` java
    
    PixpieAPI pixpieAPI = null;    
    try {
        pixpieAPI = new PixpieAPIImpl(
            "com.example.SomeApp", // Bundle ID (reverse url id), required
            "41bc32fde0d3ed6927b6f54sdc", // Secret key, required
            "yuuRiesahs3niet7thac" // fixed value for cloud usage, required
        );
    } catch (PixpieAuthenticationException e) {
        e.printStackTrace();
    }
    
``` 

### Upload ###

There are a few ways how to upload local images to Pixpie cloud.
- through [Web panel](https://pixpie.atlassian.net/wiki/display/DOC/Upload+image)
- using [REST API](https://pixpie.atlassian.net/wiki/display/DOC/Upload)
- using [SDKs](https://pixpie.atlassian.net/wiki/display/DOC/Client+and+server+SDKs)

``` java

    boolean uploadImage(@NonNull byte[] image or @NonNull InputStream imageStream                                
                               @NonNull String contentType,
                               @NonNull String encodedImageName, 
                               @NonNull String innerPath, 
                               boolean async)        
                               
    boolean uploadedAsync = pixpieAPI.uploadImage(IOUtils.toByteArray(new URL("http://i.imgur.com/ByDKcYg.jpg")),
                "jpeg", "dog.jpg", "/async/inner_folder", true);
    
    boolean uploadedSync = api.uploadImage(IOUtils.toByteArray(new URL("http://i.imgur.com/ByDKcYg.jpg")),
                "jpeg", "dog.jpg", "/sync/inner_folder", false);

```

- image/imageStream - image binary data
- contentType -  “image/png" or “image/jpeg”
- encodedImageName - image name in Pixpie
- innerPath - relative inner path where it will be saved in Pixpie cloud
- async - respond as soon as server received image's body, or when it was fully saved

### Get remote (third party) and local (uploaded) images ###

Approach how to manipulate and get optimized image's url that are available by the direct link or is uploaded.


``` java
    
    // for remote image
    String originalRemoteImageUrl = "http://i.imgur.com/ByDKcYg.jpg";            

    final String remoteImageUrl = pixpieAPI.getRemoteImageUrl(originalRemoteImageUrl, new ImageTransformation()
                .withWidth(600)
                .withCropAlignType(CropAlignType.TOP));
    
    // should produce 
    // http://pixpie-demo.azureedge.net/com.example.SomeApp/remote/def/w_600,c_top/http://i.imgur.com/ByDKcYg.jpg
    

    // for uploaded local image    
    String relativePathToImageInPixpieCloud = “/sync/inner_folder/dog.jpg” 

    final String localImageUrl = pixpieAPI.getImageUrl(relativePathToImageInPixpieCloud, new ImageTransformation()
                .withFormat(ImageFormat.WEBP)
                .withHeight(500)
                .withWidth(600)
                .withQuality(80));
    
    // should produce 
    // http://pixpie-demo.azureedge.net/com.example.SomeApp/local/webp/w_600,h_500,q_80/sync/inner_folder/dog.jpg
  
```

### Delete ###

Uploaded local images (folders) could be simply deleted from Pixpie cloud by calling the delete method.


``` java
    
    boolean deleteItems(String innerPath, List<String> images, List<String> folders) throws PixpieEmptyDeleteBatchException
    
    // Delete created/uploaded items
    List<String> foldersToDelete = new ArrayList<>();
    foldersToDelete.add("sync");
    foldersToDelete.add("async");

    try {
        b = pixpieAPI.deleteItems("/", null, foldersToDelete);
    } catch (PixpieEmptyDeleteBatchException e) {
        System.out.println("Delete failed " + e.getMessage());
    }
    
```    

- innerPath - relative path to folder in Pixpie cloud
- images - list of images
- folders - list of folders

### List items ###

Relative paths to the images/folders in some directory can be listed using listItems method.


``` java

    ListItemsBean listItems(@NonNull String innerPath)


    final ListItemsBean listItemsBean = pixpieAPI.listItems("/");

    final List<String> folders = listItemsBean.getFolders();
    final List<String> images = listItemsBean.getImages();

``` 

## License


    Copyright (C) 2015,2016 Pixpie

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
