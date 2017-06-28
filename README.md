## VolleyPractice

I wrote a series of articles to introduce this practice demo.

* [Volley 源码解析及对 Volley 的扩展（一）](http://lijiankun24.com/Volley源码解析及对Volley的扩展1/)
* [Volley 源码解析及对 Volley 的扩展（二）](http://lijiankun24.com/Volley源码解析及对Volley的扩展2/)
* [Volley 源码解析及对 Volley 的扩展（三）](http://lijiankun24.com/Volley源码解析及对Volley的扩展3/)

This series of articles introduce how to count api's duration and result's status code of the api; analysis source code of volley; custom GsonRequest by Gson and custom OkHttpStack by OkHttp.

Scan below QR code to download the apk.
<div align=center>
    <img src="app_screenshot/VolleyQR.png" width="270" height="258"/>
</div>

### count api's duration and result's status code of the api

Use the class CustomStringRequest, and override the method -- `onResponseTimeAndCode(long networkTimeMs, int statusCode)`, send a request.

you would receive api's duration and result's status code of the api in the method `onResponseTimeAndCode(long networkTimeMs, int statusCode)`
``` Java
    final String url = "http://gank.io/api/data/Android/10/1";
    StringRequest request = new CustomStringRequest(Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Volley", "onResponse " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Volley", "onErrorResponse " + error);
            }
        }) {
            @Override
            protected void onResponseTimeAndCode(long networkTimeMs, int statusCode) {
              Log.i("Volley", "api's url " + url);
              Log.i("Volley", "api's networkTimeMs " + networkTimeMs);
              Log.i("Volley", "api's statusCode " + statusCode);
            }
        };
    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
    queue.add(request);
```

the source code of CustomStringRequest:
``` Java
public class CustomStringRequest extends StringRequest {

    private long mNetworkTimeMs = 0L;

    public CustomStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        if (response != null) {
            mNetworkTimeMs = response.networkTimeMs;
        }
        return super.parseNetworkResponse(response);
    }

    @Override
    protected void deliverResponse(String response) {
        super.deliverResponse(response);
        if (mNetworkTimeMs > 0) {
            this.onResponseTimeAndCode(mNetworkTimeMs, 1);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
        NetworkResponse response = error.networkResponse;
        if (response != null) {
            this.onResponseTimeAndCode(response.networkTimeMs, response.statusCode);
        } else {
            // Http 协议中 417 表示 Expectation Failed
            this.onResponseTimeAndCode(error.getNetworkTimeMs(), 417);
        }
    }

    /**
     * 在子类中重写此方法，即可得到网络请求耗时和网络请求结果
     *
     * @param networkTimeMs 网络请求耗时，单位：毫秒
     * @param statusCode    网络请求结果，成功则为1；失败则是具体的Http状态码，如404，500等(容易定位到请求失败的原因)
     */
    protected void onResponseTimeAndCode(long networkTimeMs, int statusCode) {
    }
}
```

Other request, such as: JsonObjectRequest、JsonArrayRequest and ImageRequest, is similar, need to custom subclasses of these request.

### Custom GsonRequest
How to use GsonRequest? It is easy.
``` Java
GsonRequest<AndroidModel> request = new GsonRequest<AndroidModel>(mUrl,
    new Response.Listener<AndroidModel>() {
        @Override
        public void onResponse(AndroidModel response) {
            L.i("onResponse " + response.error);
        }
    },
    new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            L.i("onErrorResponse " + error);
        }
    }, AndroidModel.class);
RequestQueue queue = Volley.newRequestQueue(MainActivity.this.getApplicationContext());
queue.add(request);
```

The source code of GsonRequest:
``` Java

public class GsonRequest<T> extends Request<T> {

    private Gson mGson = new Gson();

    private Response.Listener<T> mListener = null;

    private Class<T> mClass = null;

    public GsonRequest(String url, Response.Listener<T> listener, Response.ErrorListener errorListener, Class<T> aClass) {
        this(Method.GET, url, listener, errorListener, aClass);
    }

    public GsonRequest(int method, String url, Response.Listener<T> listener, Response.ErrorListener errorListener, Class<T> aClass) {
        super(method, url, errorListener);
        mListener = listener;
        mClass = aClass;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(mGson.fromJson(res, mClass), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

}
```

### Custom OkHttpStack by OkHttp
How to use OkHttpStack?
``` Java
    RequestQueue queue = Volley.newRequestQueue(MainActivity.this,
                new OkHttpStack(new OkHttpClient()));
    StringRequest request = new StringRequest(mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                L.i("onResponse " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.i("onErrorResponse " + error);
            }
        });

    queue.add(request);
```

The source code of OkHttpStack:
``` Java
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
```
