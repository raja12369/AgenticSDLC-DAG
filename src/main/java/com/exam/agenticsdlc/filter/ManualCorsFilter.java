package com.exam.agenticsdlc.filter;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;


@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class ManualCorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Short-circuit CORS preflight (OPTIONS) requests with a 200 before they
        // ever reach a resource method - browsers expect this response to be fast
        // and to carry only the CORS headers, which the response filter below adds.
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            requestContext.abortWith(Response.ok().build());
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        String origin = requestContext.getHeaderString("Origin");
        String allowOrigin = (origin == null || origin.isBlank()) ? "*" : origin;

        responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", allowOrigin);
        responseContext.getHeaders().putSingle("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Headers", "Content-Type,Accept,Origin");
        responseContext.getHeaders().putSingle("Access-Control-Max-Age", "86400");
    }
}
