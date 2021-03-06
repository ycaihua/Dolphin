package com.dianping.paas.core.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * chao.yu@dianping.com
 * Created by yuchao on 2015/11/16 15:42.
 */
public class HttpUtil {

    private static final RequestConfig CONFIG = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
    public static final HashMap<String, String> NULL_MAP = Maps.newHashMap();

    public static String post(String url) throws Exception {
        return post(url, NULL_MAP);
    }

    public static String post(String url, Map<String, String> params) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();

        HttpPost post = createPost(url, params);
        String body = invoke(client, post);

        closeClient(client);

        return body;
    }

    private static HttpPost createPost(String url, Map<String, String> params) {
        HttpPost post = new HttpPost(url);
        post.setConfig(CONFIG);
        if (params != null) {
            List<NameValuePair> nameValuePairList = Lists.newArrayList();
            Set<String> keySet = params.keySet();

            for (String key : keySet) {
                nameValuePairList.add(new BasicNameValuePair(key, params.get(key)));
            }

            try {
                post.setEntity(new UrlEncodedFormEntity(nameValuePairList, "UTF-8"));
            } catch (UnsupportedEncodingException ignored) {
            }
        }

        return post;
    }


    private static String invoke(HttpClient httpclient, HttpUriRequest httpUriRequest) throws Exception {
        HttpResponse response = httpclient.execute(httpUriRequest);

        return parseResponse(response);
    }


    public static String post(String url, String requestBody) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();

        HttpPost post = createPost(url, null);
        post.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
        String body = invoke(client, post);

        closeClient(client);

        return body;
    }

    public static String post(String url, File file) throws Exception {

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("file", new FileBody(file));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = createPost(url, null);
        post.setEntity(builder.build());
        String body = invoke(client, post);

        closeClient(client);

        return body;
    }


    public static String get(String url) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();

        HttpGet get = createGet(url);
        String body = invoke(client, get);

        closeClient(client);

        return body;
    }

    private static HttpGet createGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(CONFIG);
        return httpGet;
    }

    private static String parseResponse(HttpResponse response) {
        HttpEntity entity = response.getEntity();
        String body = null;
        try {
            body = EntityUtils.toString(entity);
        } catch (Exception ignored) {
        }

        return body;
    }

    private static void closeClient(CloseableHttpClient client) {
        try {
            client.close();
        } catch (IOException ignored) {

        }
    }
}
