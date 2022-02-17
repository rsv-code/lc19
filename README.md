# lc19
Launch Complex 19 - The Java Gemini Server

# Installation
If you run a Debian based version of Linux you're 
probably in luck. There's a .deb installer which makes 
things easy. 

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


# License

This application is licensed under the GNU Lesser General Public License. Please see the included LICENSE.txt file for details or find a copy on the GNU website [GNU Lesser General Public License](https://www.gnu.org/licenses/lgpl-3.0.en.html).