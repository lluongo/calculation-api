package com.tenpo.calculation_api.infrastructure.tool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

public class HttpServletResponseCopier extends HttpServletResponseWrapper {
    private final ByteArrayOutputStream copy;
    private final ServletOutputStream outputStream;
    private final PrintWriter writer;
    private boolean usingWriter;

    public HttpServletResponseCopier(HttpServletResponse response) throws IOException {
        super(response);
        copy = new ByteArrayOutputStream();
        outputStream = new CopierServletOutputStream(copy, response.getOutputStream());
        writer = new PrintWriter(copy);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        usingWriter = false;
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        usingWriter = true;
        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (usingWriter) {
            writer.flush();
        } else {
            outputStream.flush();
        }
        super.flushBuffer();
    }

    public byte[] getCopy() {
        return copy.toByteArray();
    }

    private static class CopierServletOutputStream extends ServletOutputStream {
        private final ByteArrayOutputStream copy;
        private final ServletOutputStream outputStream;

        public CopierServletOutputStream(ByteArrayOutputStream copy, ServletOutputStream outputStream) {
            this.copy = copy;
            this.outputStream = outputStream;
        }

        @Override
        public void write(int b) throws IOException {
            copy.write(b);
            outputStream.write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
        }
    }
}

