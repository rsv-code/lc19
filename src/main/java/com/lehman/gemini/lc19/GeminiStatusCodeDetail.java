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

public enum GeminiStatusCodeDetail {
    INPUT(10),
    SENSITIVE_INPUT(11),
    SUCCESS(20),
    REDIRECT_TEMPORARY(30),
    REDIRECT_PERMANENT(31),
    TEMPORARY_FAILURE(40),
    SERVER_UNAVAILABLE(41),
    CGI_ERROR(42),
    PROXY_ERROR(43),
    SLOW_DOWN(44),
    PERMANENT_FAILURE(50),
    NOT_FOUND(51),
    GONE(52),
    PROXY_REQUEST_REFUSED(53),
    BAD_REQUEST(59),
    CLIENT_CERTIFICATE_REQUIRED(60),
    CERTIFICATE_NOT_AUTHORISED(61),
    CERTIFICATE_NOT_VALID(62);

    private int value;

    private GeminiStatusCodeDetail(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
