package com.manoj.finorder.payledgerservice.observability;

import com.manoj.finorder.payledgerservice.observability.CorrelationIdContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CorrelationIdFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String correlationId = CorrelationIdContext.getOrCreate(request.getHeader(CorrelationIdContext.HEADER_NAME));
        CorrelationIdContext.set(correlationId);
        response.setHeader(CorrelationIdContext.HEADER_NAME, correlationId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            CorrelationIdContext.clear();
        }
    }
}
