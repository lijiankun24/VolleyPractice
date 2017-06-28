package com.lijiankun24.volleypractice.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpStack;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * OkHttpStack.java
 * <p>
 * Created by lijiankun on 17/6/7.
 */

public class OkHttpStack implements HttpStack {

    private final OkHttpClient mOkHttpClient;

    public OkHttpStack(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            throw new IllegalArgumentException("OkHttpClient can't be null");
        }
        mOkHttpClient = okHttpClient;
    }

    @Override
    public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders)
            throws IOException, AuthFailureError {
        // 设置超时时间
        int timeoutMs = request.getTimeoutMs();
        OkHttpClient okHttpClient = mOkHttpClient
                .newBuilder()
                .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .writeTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .build();

        // 设置请求头
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.putAll(request.getHeaders());
        headers.putAll(additionalHeaders);

        // 设置请求 url
        okhttp3.Request.Builder builder = new okhttp3.Request
                .Builder()
                .url(request.getUrl());
        for (String key : headers.keySet()) {
            builder.header(key, headers.get(key));
        }
        // 设置请求方式和请求体
        setConnectionParametersForRequest(builder, request);

        // 通过 OkHttp3 进行网络请求得到 Response 类型的响应对象 okResponse
        okhttp3.Request okRequest = builder.build();
        Call call = okHttpClient.newCall(okRequest);
        Response okResponse = call.execute();

        // 根据 okResponse 对象生成 BasicStatusLine 对象
        BasicStatusLine responseStatus = new BasicStatusLine(
                parseProtocol(okResponse.protocol()),
                okResponse.code(),
                okResponse.message()
        );
        // 生成 BasicHttpResponse 对象 response，通过 {@link #entityFromOkHttpResponse(Response)}
        // 方法生成 HttpEntity 对象，并设置给 response 对象
        BasicHttpResponse response = new BasicHttpResponse(responseStatus);
        response.setEntity(entityFromOkHttpResponse(okResponse));

        // 向响应对象 response 中设置响应头
        Headers responseHeaders = okResponse.headers();
        int size = responseHeaders.size();
        String name;
        String value;
        for (int i = 0; i < size; i++) {
            name = responseHeaders.name(i);
            value = responseHeaders.value(i);
            if (value != null) {
                response.addHeader(new BasicHeader(name, value));
            }
        }
        return response;
    }

    /**
     * 设置请求方式和请求体
     *
     * @param builder OkHttp3 通过 Build 设置请求方式和请求体
     * @param request 请求对象 request
     * @throws AuthFailureError
     */
    private void setConnectionParametersForRequest(okhttp3.Request.Builder builder, Request<?> request)
            throws AuthFailureError {
        switch (request.getMethod()) {
            case Request.Method.DEPRECATED_GET_OR_POST:
                byte[] postBody = request.getPostBody();
                if (postBody != null) {
                    builder.post(RequestBody.create(MediaType.parse(request.getPostBodyContentType()), postBody));
                }
                break;
            case Request.Method.GET:
                builder.get();
                break;
            case Request.Method.DELETE:
                builder.delete();
                break;
            case Request.Method.POST:
                builder.post(createRequestBody(request));
                break;
            case Request.Method.PUT:
                builder.put(createRequestBody(request));
                break;
            case Request.Method.HEAD:
                builder.head();
                break;
            case Request.Method.OPTIONS:
                builder.method("OPTIONS", null);
                break;
            case Request.Method.TRACE:
                builder.method("TRACE", null);
                break;
            case Request.Method.PATCH:
                builder.patch(createRequestBody(request));
                break;
            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    /**
     * 根据请求对象 request 创建请求体
     *
     * @param r 请求对象
     * @return OKHttp 所需要的请求体 RequestBody 对象
     * @throws AuthFailureError
     */
    private static RequestBody createRequestBody(Request<?> r) throws AuthFailureError {
        final byte[] body = r.getBody();
        if (body == null) return null;

        return RequestBody.create(MediaType.parse(r.getBodyContentType()), body);
    }

    /**
     * 根据不同的 Protocol 对象，生成对应的不同的 ProtocolVersion 对象
     *
     * @param p
     * @return
     */
    private static ProtocolVersion parseProtocol(final Protocol p) {
        switch (p) {
            case HTTP_1_0:
                return new ProtocolVersion("HTTP", 1, 0);
            case HTTP_1_1:
                return new ProtocolVersion("HTTP", 1, 1);
            case SPDY_3:
                return new ProtocolVersion("SPDY", 3, 1);
            case HTTP_2:
                return new ProtocolVersion("HTTP", 2, 0);
        }

        throw new IllegalAccessError("Unkwown protocol");
    }

    /**
     * 根据 OKHttp 响应中的 Response 对象，生成 HttpEntity 对象
     *
     * @param r OkHttp 的响应对象
     * @return HttpEntity 对象
     * @throws IOException
     */
    private static HttpEntity entityFromOkHttpResponse(Response r) throws IOException {
        BasicHttpEntity entity = new BasicHttpEntity();
        ResponseBody body = r.body();

        entity.setContent(body.byteStream());
        entity.setContentLength(body.contentLength());
        entity.setContentEncoding(r.header("Content-Encoding"));

        if (body.contentType() != null) {
            entity.setContentType(body.contentType().type());
        }
        return entity;
    }
}
