package com.tenpo.calculation_api.infrastructure.interceptor.Wrapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;


public class CachedBodyServletInputStream extends ServletInputStream {
    private final ByteArrayInputStream byteArrayInputStream;

    public CachedBodyServletInputStream(byte[] cachedBody) {
        this.byteArrayInputStream = new ByteArrayInputStream(cachedBody);
    }

    @Override
    public boolean isFinished() {
        return byteArrayInputStream.available() == 0;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        // No se necesita implementación adicional
    }

    @Override
    public int read() throws IOException {
        return byteArrayInputStream.read();
    }
}