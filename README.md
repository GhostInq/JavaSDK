# Java SDK

https://www.pixpie.co

## What is it for? ##

Pixpie is a Platform as a Service for image optimization and manipulation.

Java SDK provides API methods implementation to access Pixpie REST API to use it for server-to-server integration. 

## How to start? ##

Check [Getting started](https://pixpie.atlassian.net/wiki/display/DOC/Getting+started) guide and [register](https://cloud.pixpie.co/registration) your account

## Add dependency ##

Check last released version on [Bintray](https://dl.bintray.com/accord/pixpie-java/co/pixpie/api/java/pixpie-java/).

Update your Maven/Gradle files:

``` maven
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

``` 

### Get remote (third party) images ###

Approaches how to manipulate and optimize images that are available by the direct link.

### Upload ###

There are a few ways how to upload local images to Pixpie cloud.
- through [Web panel](https://pixpie.atlassian.net/wiki/display/DOC/Upload+image)
- using [REST API](https://pixpie.atlassian.net/wiki/display/DOC/Upload)
- using [SDKs](https://pixpie.atlassian.net/wiki/display/DOC/Client+and+server+SDKs)

``` java

```

- image - binary data 
- contentType -  “image/png" or “image/jpeg”
- encodedImageName - image name in Pixpie
- innerPath - relative inner path where it will be saved in Pixpie cloud
- async - true / false

### Get local (uploaded) images ###

Behaviour of methods that provide possibility to show or get the urls of uploaded images is very similar to remote (third party images).

To get the link:

``` java

  String relativePathToImageInPixpieCloud = “/path/to/image/some-image-1.jpg”
  
  
```

### Delete ###

Uploaded local images could be simply deleted from Pixpie cloud by calling the delete method.


``` java


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
