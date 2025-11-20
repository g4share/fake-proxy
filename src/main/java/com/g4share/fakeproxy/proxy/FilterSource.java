package com.g4share.fakeproxy.proxy;

import com.g4share.fakeproxy.model.Filter;
import com.g4share.fakeproxy.model.UpdatedData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;

@RequiredArgsConstructor
public class FilterSource extends HttpFiltersSourceAdapter {

    private static final int BUGGER_SIZE = 10 * 1024 * 1024;

    private final Filter filter;
    private final UpdatedData updatedData;

    @Override
    public int getMaximumRequestBufferSizeInBytes() {
        return BUGGER_SIZE;
    }

    @Override
    public int getMaximumResponseBufferSizeInBytes() {
        return BUGGER_SIZE;
    }

    @Override
    public HttpFilters filterRequest(final @NonNull HttpRequest originalRequest,
                                     final @NonNull ChannelHandlerContext ctx) {
        return new LoggedHttpFilter(originalRequest, ctx, filter == null ? new Filter() : filter, updatedData);
    }
}
