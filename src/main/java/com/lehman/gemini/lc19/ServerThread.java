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
import java.nio.file.Files;

/**
 * ServerThread class implements the functionality of the server thread.
 */
public class ServerThread implements Runnable {
    private final static Logger log = LoggerFactory.getLogger(Main.class);

    protected ServerInfo serverInfo = null;
    protected SSLSocket soc = null;

    protected BufferedReader in = null;
    protected PrintWriter out = null;

    /**
     * Default constructor.
     * @param SInfo is a ServerInfo object with the server information.
     * @param Soc is the SSLSocket to use.
     */
    public ServerThread(ServerInfo SInfo, SSLSocket Soc) {
        this.serverInfo = SInfo;
        this.soc = Soc;
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
        GeminiStatusCodeDetail status = GeminiStatusCodeDetail.TEMPORARY_FAILURE;

        // Bad request
        if (!this.isGoodRequest(req)) {
            this.out.print(GeminiStatusCodeDetail.BAD_REQUEST.getValue() + " \r\n");
            log.info("Response: " + GeminiStatusCodeDetail.BAD_REQUEST.toString());
            return;
        }

        String data = null;
        try {
            data = this.getFileFromHostDir(req);
        } catch (IOException e) {
            log.error(e.getMessage());
            status = GeminiStatusCodeDetail.NOT_FOUND;
        }

        if (!data.equals("")) {
            status = GeminiStatusCodeDetail.SUCCESS;
            this.out.print(status.getValue() + " " + GeminiMediaType.TEXT_GEMINI.getValue() + "; lang=en; charset=utf-8\r\n");
            this.out.println(data);
        } else {
            this.out.print(status.getValue() + " \r\n");
        }
        log.info("Response: " + status.toString());
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
