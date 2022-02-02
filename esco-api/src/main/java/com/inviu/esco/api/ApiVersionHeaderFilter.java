package com.inviu.esco.api;

import com.lightbend.lagom.javadsl.api.transport.HeaderFilter;
import com.lightbend.lagom.javadsl.api.transport.RequestHeader;
import com.lightbend.lagom.javadsl.api.transport.ResponseHeader;

public class ApiVersionHeaderFilter implements HeaderFilter {
    private final String apiVersion;

    ApiVersionHeaderFilter(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    @Override
    public RequestHeader transformClientRequest(RequestHeader request) {
        return request.withHeader("api-version", apiVersion);
    }

    @Override
    public RequestHeader transformServerRequest(RequestHeader request) {
        return request;
    }

    @Override
    public ResponseHeader transformServerResponse(ResponseHeader response, RequestHeader request) {
        return response;
    }

    @Override
    public ResponseHeader transformClientResponse(ResponseHeader response, RequestHeader request) {
        return response;
    }
}
