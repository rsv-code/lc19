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

/**
 * Application Main class handles starting the server.
 */
public class Main {
    /**
     * Main entry point of the application.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // Create a new server object.
        Server s = new Server();

        // Load properties from file.
        s.loadProperties("app.properties");

        // Init the server.
        s.init();

        // Run it.
        s.run();
    }
}
