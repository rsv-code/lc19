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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeminiRequest {
    protected String rawRequest = "";
    protected String scheme = "";
    protected String host = "";
    protected String port = "";
    protected String path = "";
    protected String query = "";

    public GeminiRequest() { }

    public GeminiRequest(String ReqStr) {
        this.rawRequest = ReqStr;
        this.parseRequest(this.rawRequest);
    }

    public void parseRequest(String RawRequest) {
        Pattern p = Pattern.compile("([A-z0-9\\+\\-\\.].+)://([a-z0-9\\-\\.]+)(:[0-9]+)?(/.*)?(\\?[A-z0-9_%]+)?");
        Matcher m = p.matcher(RawRequest);
        if (m.matches()) {
            this.scheme = m.group(1);
            this.host = m.group(2);
            if (m.group(3) != null) {
                this.port = m.group(3).substring(1);
            }
            if (m.group(4) != null) {
                this.path = m.group(4).trim();
            }
            if (m.group(5) != null) {
                this.query = m.group(5).substring(1).trim();
            }
        }
    }

    public String getRawRequest() {
        return rawRequest;
    }

    public void setRawRequest(String rawRequest) {
        this.rawRequest = rawRequest;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "GeminiRequest{" +
                "rawRequest='" + rawRequest + '\'' +
                ", scheme='" + scheme + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", path='" + path + '\'' +
                ", query='" + query + '\'' +
                '}';
    }
}
