package com.tenpo.calculation_api.infrastructure.interceptor;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class ResponseWrapper extends HttpServletResponseWrapper {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintWriter printWriter = new PrintWriter(outputStream);
    private ServletOutputStream servletOutputStream;

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
        servletOutputStream = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) throws IOException {
                outputStream.write(b);
            }
        };
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return printWriter;
    }

    public String getCapturedResponse() throws IOException {
        printWriter.flush();
        return new String(outputStream.toByteArray(), getCharacterEncoding());
    }
}