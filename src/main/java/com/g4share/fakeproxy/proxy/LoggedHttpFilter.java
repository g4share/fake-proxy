package com.g4share.fakeproxy.proxy;

import com.g4share.fakeproxy.model.Filter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.Nullable;
import org.littleshoot.proxy.HttpFiltersAdapter;

import java.util.Map;

import static com.g4share.fakeproxy.helper.Utils.extractHost;

@Log4j2
public class LoggedHttpFilter extends HttpFiltersAdapter {
    private final Filter filter;
    private final String host;
    private final boolean isHostFiltered;

    public LoggedHttpFilter(final HttpRequest originalRequest,
                            final ChannelHandlerContext ctx,
                            final Filter filter) {
        super(originalRequest, ctx);
        this.filter = filter;

        host = extractHost(originalRequest);
        isHostFiltered = filter.isFiltered(host);
    }

    @Override
    public @Nullable HttpResponse clientToProxyRequest(final @Nullable HttpObject httpObject) {
        if (httpObject instanceof HttpRequest req && isHostFiltered) {
            String fullUrl =  req.method() == HttpMethod.CONNECT
                    ? req.uri()
                    : host + req.uri();
            log.info("\n>>> {} {}", req.method(), fullUrl);
            printHeaders("    ", req.headers());
        }

        if (httpObject instanceof FullHttpRequest fullReq && isHostFiltered) {
            printBody(fullReq.content(), fullReq.headers());
        }

        return null;
    }

    @Override
    public  HttpObject serverToProxyResponse(final HttpObject httpObject) {
        if (httpObject instanceof HttpResponse resp) {
            log.info("\n<<< Response Status: {}", resp.status());
            printHeaders("    ", resp.headers());
        }

        if (httpObject instanceof FullHttpResponse fullResp) {
            printBody(fullResp.content(), fullResp.headers());
        }

        return httpObject;
    }

    private void printHeaders(final String alignment, final HttpHeaders headers) {
        Map<String, String> allowedHeaders = filter.filteredHeaders(headers);
        for (Map.Entry<String, String> header : allowedHeaders.entrySet()) {
            log.info("{}{}: {}", alignment, header.getKey(), header.getValue());
        }
    }

    private void printBody(final ByteBuf content, final HttpHeaders headers) {
        if (content == null) {
            return;
        }

        String enc = headers.get(HttpHeaderNames.CONTENT_ENCODING, "");
        String ctype = headers.get(HttpHeaderNames.CONTENT_TYPE, "");

        boolean maybeBinary = enc.contains("gzip") || enc.contains("deflate")
                || (!ctype.isEmpty() && !ctype.contains("json") && !ctype.contains("xml") && !ctype.contains("text"));

        int len = content.readableBytes();
        if (len == 0) {
            return;
        }

        if (maybeBinary || len > 256 * 1024) {
            log.info( "{} bytes (binary or large; not printed)", len);
            return;
        }

        ByteBuf copy = Unpooled.copiedBuffer(content);
        try {
            String body = copy.toString(CharsetUtil.UTF_8);
            log.info("\n{}", body);
        } finally {
            copy.release();
        }
    }
}
