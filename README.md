# lc19
Launch Complex 19 - The Java Gemini Server

Copyright 2022 Austin Lehman <austin@rosevillecode.com>

# Installation
If you run a Debian based version of Linux you're 
probably in luck. There's a .deb installer which makes 
things easy. Otherwise you can download the binary 
zipped file or build the source here. 

Downloads:
- Debian Package: [lc19.deb](https://github.com/rsv-code/lc19/releases/download/1.0.4/lc19.deb)
- Binary Zip: [lc19-bin.zip](https://github.com/rsv-code/lc19/releases/download/1.0.4/lc19-bin.zip)

# Building from Source

After running the maven command you should have the 
generated .jar file in the target directory. 

```
mvn clean package
```

# Java KeyStore 

SSL is a Gemini requirement so a Java KeyStore must be 
setup. You can name the KeyStore file anything you like, 
just make sure it's referenced correctly from app.
properties. 

```
keytool -genkey -keyalg RSA -keypass password -storepass password -keystore serverkeystore.jks
```
Where fisrt and last name are the host name. This is 
critical and things won't work if you don't get this 
correct. If you're just testing it out running locally 
you can set it to 'localhost'.

# Configuration

Configuration is done in the app.properties file. The 
following properties are available.

- keystore.file - The keystore filename to use.
- keystore.password - The keystore password to 
  ues along with the provided keystore file.
- port - The port to serve Gemini content on. Default is 
  '1965'.
- hostname - A string with the hostname this 
  server uses. This is used to limit requests to only 
  the name provided here. Default is 'localhost'.
- hostDir - The directory to host Gemini files from. 
  Default is 'public'.
- minThreads - The minimum number of threads to use in 
  the thread pool. Default is 10.
- maxThreads - The maximum number of threads to use in 
  the thread pool. Default is 100.

# Dynamic Capsules - Embedded Server

The lc19 code can be used as a library in your project 
to create your own server and extend its functionality. 
You can use annotations to create dynamic content using 
capsules.

First include the Maven dependency.
```
<dependency>
  <groupId>io.github.rsv-code</groupId>
  <artifactId>lc19</artifactId>
  <version>1.0.3</version>
</dependency>
```

Next write the server startup code in your application.
```
// Create a new server object.
Server s = new Server();

// Load properties from file.
s.loadProperties("app.properties");

// Init the server.
s.init();

// Run it.
s.run();
```

Finally creat a new class and use the Capsule annotation 
along with the path. When a request is made to 
gemini://localhost/hello in this case, handle will be 
called and 
the string response will be sent back.

```
@Capsule(path = "/hello")
public class hello implements CapsuleInt {
    public GeminiResponse handle(GeminiRequest req) {
        GeminiResponse resp = new GeminiResponse();
        resp.setData("Hello from the hello capsule!");
        return resp;
    }
}
```

# License

This application is licensed under the GNU Lesser General Public License. Please see the included LICENSE.txt file for details or find a copy on the GNU website [GNU Lesser General Public License](https://www.gnu.org/licenses/lgpl-3.0.en.html).
