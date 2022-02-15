/*
 * Copyright 2022 Austin Lehman. (cup_of_code@fastmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.lehman.gemini.lc19;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The Server class handles all of the high level server configuration
 * and running the server socket.
 *
 * The normal order of operations to get the server running:
 * loadProperties()
 * init()
 * run()
 */
public class Server {
    private final static Logger log = LoggerFactory.getLogger(Main.class);

    // KeyStore
    protected String keyStore = "";

    // KeyStore Password
    protected String keyStorePassword = "";

    // Hostname
    protected String host = "";

    // Default Gemini port
    protected int port = 1965;

    // hostDir is the host directory that files are
    // served from. If not set, this feature isn't active.
    protected String hostDir = null;

    // Max number of threads to use
    protected ThreadPoolExecutor threadPool;
    protected int minThreads = 10;
    protected int maxThreads = 100;

    // Available protocols
    protected String[] protocols = new String[] { "TLSv1.2" };

    // Available cipher suites.
    protected String[] cipherSuites = new String[] { "TLS_RSA_WITH_AES_128_GCM_SHA256" };

    // SSL server socket
    private SSLServerSocket serverSocket = null;

    /**
     * Default constructor
     */
    public Server() { }

    /**
     * Initializes the server. This must be executed prior to running
     * because it sets up the required thread pool and server socket.
     */
    public void init() {
        try {
            this.initThreadPool();
            this.serverSocket = this.createServerSocket(this.port);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Runs the server. Ensure init() has already been called prior
     * to calling run().
     */
    public void run() {
        log.info("Starting Lauch Complex 19 Gemini Server.");
        while (true) {
            try {
                SSLSocket soc = (SSLSocket) this.serverSocket.accept();
                log.info("New client connection from '" + soc.getInetAddress().getHostAddress() + "'.");

                ServerThread st = new ServerThread(this.getServerInfo(), soc);
                this.threadPool.execute(st);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * Loads properties with the provided property file name.
     * @param PropFile is a property file to load.
     * @throws IOException
     */
    public void loadProperties(String PropFile) throws IOException {
        log.info("Loading properties from file '" + PropFile + "'.");

        Properties props = new Properties();
        props.load(new FileInputStream(PropFile));

        this.setKeyStore(props.getProperty("keystore.file", ""));
        this.setKeyStorePassword(props.getProperty("keystore.password", ""));

        this.setHost(props.getProperty("hostname", "localhost"));
        this.setPort(Integer.parseInt(props.getProperty("port", "1965")));

        this.setMinThreads(Integer.parseInt(props.getProperty("minThreads", "10")));
        this.setMaxThreads(Integer.parseInt(props.getProperty("maxThreads", "100")));

        this.setHostDir(props.getProperty("hostDir", null));
    }

    /**
     * Gets a server info object with this server information.
     * @return A ServerInfo object.
     */
    private ServerInfo getServerInfo() {
        return new ServerInfo(this.host, this.port, this.hostDir);
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
        System.setProperty("javax.net.ssl.keyStore", this.keyStore);
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
        System.setProperty("javax.net.ssl.keyStorePassword", this.keyStorePassword);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHostDir() {
        return hostDir;
    }

    public void setHostDir(String hostDir) {
        this.hostDir = hostDir;
    }

    public int getMinThreads() {
        return minThreads;
    }

    public void setMinThreads(int minThreads) {
        this.minThreads = minThreads;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    /**
     * Creates the server socket with the provided port number.
     * @param port is an int with the port number to bind to.
     * @return A SSLServerSocket object.
     * @throws IOException
     */
    private SSLServerSocket createServerSocket(int port) throws IOException {
        log.info("Creating SSLServerSocket on port " + port + ".");
        SSLServerSocket soc = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(port);
        soc.setReuseAddress(true);
        soc.setEnabledProtocols(this.protocols);
        soc.setEnabledCipherSuites(this.cipherSuites);
        return soc;
    }

    /**
     * Initializes the thread pool.
     */
    private void initThreadPool() {
        log.info("Initializing server thread pool with min: " + this.minThreads + " and max: " + this.maxThreads + " threads.");
        this.threadPool = new ThreadPoolExecutor(this.minThreads, this.maxThreads, 10, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), new RejectedExecutionHandler()
        {
            @Override
            public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor){
                try {
                    executor.getQueue().put(runnable);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }
}
