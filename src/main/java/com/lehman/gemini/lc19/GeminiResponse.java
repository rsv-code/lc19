package com.lehman.gemini.lc19;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class GeminiResponse {
    /**
     * The response status code.
     */
    private GeminiStatusCodeDetail status = GeminiStatusCodeDetail.SUCCESS;

    /**
     * The response media type.
     */
    private String mediaType = GeminiMediaType.TEXT_GEMINI.getValue();

    /**
     * Language
     */
    private String language = "en";

    /**
     * Character set (.name())
     */
    private Charset charset = StandardCharsets.UTF_8;

    /**
     * The response data to send.
     */
    private String data = "";

    /**
     * Builds the Gemini response with the fields that have been set.
     * @return A String with the response.
     */
    public String build() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.status.getValue() + " ");
        if (this.status == GeminiStatusCodeDetail.SUCCESS) {
            if (!this.mediaType.equals(""))
                sb.append(this.mediaType + "; ");
            if (!this.language.equals("")) {
                sb.append("lang=" + this.language + "; ");
            }
            sb.append("charset=" + this.charset.name());
        }
        sb.append("\r\n");
        if (this.status == GeminiStatusCodeDetail.SUCCESS) {
            sb.append(this.data);
        }
        return sb.toString();
    }

    public GeminiStatusCodeDetail getStatus() {
        return status;
    }

    public void setStatus(GeminiStatusCodeDetail status) {
        this.status = status;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
