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

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Map;

/**
 * ServerThread class implements the functionality of the server thread.
 */
public class ServerThread implements Runnable {
    private final static Logger log = LoggerFactory.getLogger(Main.class);

    protected ServerInfo serverInfo = null;
    protected SSLSocket soc = null;

    protected BufferedReader in = null;
    protected PrintWriter out = null;

    // Reference to the capsule map.
    private Map<String, Class> capsules;

    /**
     * Default constructor.
     * @param SInfo is a ServerInfo object with the server information.
     * @param Soc is the SSLSocket to use.
     */
    public ServerThread(ServerInfo SInfo, SSLSocket Soc, Map<String, Class> Capsules) {
        this.serverInfo = SInfo;
        this.soc = Soc;
        this.capsules = Capsules;
    }

    /**
     * Runs the server thread.
     */
    @Override
    public void run() {
        log.info("ServerThread handling new request.");
        try {
            this.in = new BufferedReader(new InputStreamReader(this.soc.getInputStream()));
            this.out = new PrintWriter(this.soc.getOutputStream(), true);

            // Read the request message
            GeminiRequest req = this.readRequest();
            log.info("Received request: " + req.toString());

            // Create the response
            this.createResponse(req);

            this.out.flush();
            this.soc.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Reads in the request line and produces a
     * GeminiRequest object.
     * @return A GeminiRequest object.
     * @throws IOException
     */
    private GeminiRequest readRequest() throws IOException {
        String line = in.readLine();
        return new GeminiRequest(line.trim());
    }

    /**
     * Creates the response with the provided request.
     * @param req is a GeminiRequest object.
     */
    private void createResponse(GeminiRequest req) {
        // Bad request
        if (!this.isGoodRequest(req)) {
            this.out.print(GeminiStatusCodeDetail.BAD_REQUEST.getValue() + " \r\n");
            log.info("Response: " + GeminiStatusCodeDetail.BAD_REQUEST.toString());
            return;
        }

        // Attempt to find a capsule and execute it
        GeminiResponse resp = this.getCapsuleResponse(req);

        // Didn't find a response with the module, let's try a file.
        if (resp == null) {
            resp = this.getFileResponse(req);
        }

        out.print(resp.build());
        log.info("Response: " + resp.getStatus().toString());
    }

    /**
     * Attempts to find a matching module and if so it executes the
     * request and returns the response.
     * @param req is the GeminiRequest object.
     * @return A GeminiResponse object with the response if found and
     * null if not.
     */
    private GeminiResponse getCapsuleResponse(GeminiRequest req) {
        GeminiResponse resp = null;

        String reqPath = req.getPath();
        if (reqPath.length() > 1 && reqPath.endsWith("/"))
            reqPath = reqPath.substring(0, reqPath.length() - 1);

        Class c = this.capsules.get(reqPath);
        if (c != null) {
            try {
                Object obj = c.getDeclaredConstructor().newInstance();
                Method m = c.getMethod("handle", new Class[]{ GeminiRequest.class });
                resp = (GeminiResponse) m.invoke(obj, req);
            } catch (InstantiationException e) {
                log.error(e.getMessage());
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
            } catch (InvocationTargetException e) {
                log.error(e.getMessage());
            } catch (NoSuchMethodException e) {
                log.error(e.getMessage());
            }
        }

        return resp;
    }

    /**
     * Attempts to find a matching file and if so it returns the response.
     * @param req is the GeminiRequest object.
     * @return A GeminiResponse object with the response if found and
     * null if not.
     */
    private GeminiResponse getFileResponse(GeminiRequest req) {
        GeminiResponse resp = new GeminiResponse();

        String data = null;
        try {
            resp.setData(this.getFileFromHostDir(req));
        } catch (IOException e) {
            log.error(e.getMessage());
            resp.setStatus(GeminiStatusCodeDetail.NOT_FOUND);
        }

        return resp;
    }

    /**
     * Checks to see if the request is good and valid.
     * @param req is the GeminiRequest object.
     * @return A boolean with true for valid and false for not.
     */
    private boolean isGoodRequest(GeminiRequest req) {
        if (req.getPath().contains("..")) {
            log.info("Bad request: " + req.rawRequest);
            return false;
        } else if (
                this.serverInfo.getHost() != null
                && !this.serverInfo.getHost().equals("")
                && !req.getHost().equals(this.serverInfo.getHost())
        ) {
            log.info("Invalid host: " + req.rawRequest);
            return false;
        }
        return true;
    }

    /**
     * Attempts to fetch a file from the host directory with the
     * provided GeminiRequest object.
     * @param req is the GeminiRequest object.
     * @return A String with the file contents or empty String.
     * @throws IOException
     */
    private String getFileFromHostDir(GeminiRequest req) throws IOException {
        String ret = "";
        if (this.serverInfo.getHostDir() != null && !this.serverInfo.getHostDir().equals("")) {
            File f = this.findGeminiFile(req);
            if (f != null && f.exists()) {
                // Read file and return the contents.
                ret = Files.readString(f.toPath());
            }
        }
        return ret;
    }

    /**
     * Tries to find the requested file with the provided
     * GeminiRequest object.
     * @param req is the GeminiRequest object.
     * @return A File object if found and null if not.
     */
    private File findGeminiFile(GeminiRequest req) {
        File ret = null;

        String fileBase = this.serverInfo.getHostDir();
        if (req.getPath().length() > 0 && !req.getPath().equals("/")) {
            fileBase += req.getPath();
        } else {
            fileBase += "/index";
        }

        // Remove any trailing slashes
        while (fileBase.endsWith("/")) {
            fileBase = fileBase.substring(0, fileBase.length()-2);
        }

        File f = new File(fileBase + ".gmi");
        if (f.exists()) {
            return f;
        }

        f = new File(fileBase + ".gmni");
        if (f.exists()) {
            return f;
        }

        f = new File(fileBase + ".gemini");
        if (f.exists()) {
            return f;
        }

        return ret;
    }
}
