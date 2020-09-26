package io.cofix.hedging.utils;


import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.thymeleaf.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {

    // proxy address
    private static String PROXY_IP = "127.0.0.1";
    // The proxy port
    private static int PROXY_PORT = 0;

    // Utf-8 character encoding
    private static final String CHARSET_UTF_8 = "utf-8";

    // HTTP content types. In the form equivalent to a form, the data is submitted
    private static final String CONTENT_TYPE_FORM_URL = "application/x-www-form-urlencoded";

    // HTTP content types. In the form equivalent to a form, the data is submitted
    private static final String CONTENT_TYPE_JSON_URL = "application/json;charset=utf-8";

    // Connection manager
    private static PoolingHttpClientConnectionManager pool;

    // Request configuration
    private static RequestConfig requestConfig;

    static {

        try {
            //"Initialize HttpClientTest~~~ start";
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    builder.build());
            // The configuration supports both HTTP and HTPPS
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register(
                    "http", PlainConnectionSocketFactory.getSocketFactory()).register(
                    "https", sslsf).build();
            // Initializes the connection manager
            pool = new PoolingHttpClientConnectionManager(
                    socketFactoryRegistry);
            // Increase the maximum number of connections to 200, which the actual project would be better off reading from the configuration file
            pool.setMaxTotal(200);
            // Set the maximum route
            pool.setDefaultMaxPerRoute(2);
            // Initializes requestConfig according to the default timeout limit
            int socketTimeout = 10000;
            int connectTimeout = 10000;
            int connectionRequestTimeout = 10000;
            requestConfig = RequestConfig.custom().setConnectionRequestTimeout(
                    connectionRequestTimeout).setSocketTimeout(socketTimeout).setConnectTimeout(
                    connectTimeout).build();

            //Initialize HttpClientTest~~~ end;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        // Set the request timeout
        requestConfig = RequestConfig.custom().setSocketTimeout(50000).setConnectTimeout(50000)
                .setConnectionRequestTimeout(50000).build();
    }

    public static void updateProxy(String proxyIp, int proxyPort) {
        if (!StringUtils.isEmpty(proxyIp)) {
            PROXY_IP = proxyIp;
        }
        if (proxyPort > 0) {
            PROXY_PORT = proxyPort;
        }
    }

    public static int getProxyPort() {
        return PROXY_PORT;
    }

    public static String getProxyIp() {
        return PROXY_IP;
    }

    public static CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
                // Set up connection pool management
                .setConnectionManager(pool)
                // Set request configuration
                .setDefaultRequestConfig(requestConfig)
                // Set the number of retries
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
                .build();

        return httpClient;
    }

    /**
     * Send a Post request
     *
     * @param httpPost
     * @return
     */
    private static String sendHttpPost(HttpPost httpPost) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        // Response content
        String responseContent = null;
        try {
            // Create the default httpClient instance.
            httpClient = getHttpClient();
            // Configure request information
            httpPost.setConfig(requestConfig);
            // Perform the requested
            response = httpClient.execute(httpPost);
            // Get the response instance
            HttpEntity entity = response.getEntity();

            // Determine response state
            if (response.getStatusLine().getStatusCode() >= 300) {
                throw new Exception(
                        "HTTP Request is not success, Response code is " + response.getStatusLine().getStatusCode());
            }

            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                responseContent = EntityUtils.toString(entity, CHARSET_UTF_8);
                EntityUtils.consume(entity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Release resources
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }

    /**
     * Send a Get request
     *
     * @param httpGet
     * @return
     */
    private static String sendHttpGet(HttpGet httpGet) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        // Response content
        String responseContent = null;
        try {
            // Create the default httpClient instance.
            httpClient = getHttpClient();
            // Configure request information
            if (PROXY_PORT == 0) {
                httpGet.setConfig(requestConfig);
            } else {
                HttpHost proxy = new HttpHost(PROXY_IP, PROXY_PORT);
                RequestConfig proxyConfig = RequestConfig.custom().setProxy(proxy).build();
                httpGet.setConfig(proxyConfig);
            }
            // Perform the requested
            response = httpClient.execute(httpGet);
            // Get the response instance
            HttpEntity entity = response.getEntity();

            // Determine response state
            if (response.getStatusLine().getStatusCode() >= 300) {
                throw new Exception(
                        "HTTP Request is not success, Response code is " + response.getStatusLine().getStatusCode());
            }

            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                responseContent = EntityUtils.toString(entity, CHARSET_UTF_8);
                EntityUtils.consume(entity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Release resources
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }

    private static String sendHttpGet(HttpGet httpGet, String token, String headerName) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        // Response content
        String responseContent = null;
        try {
            // Create the default httpClient instance.
            httpClient = getHttpClient();
            // Configure request information
            httpGet.setConfig(requestConfig);
            httpGet.setHeader("Content-Type", CONTENT_TYPE_FORM_URL);
            httpGet.setHeader(headerName, token);
            // Perform the requested
            response = httpClient.execute(httpGet);
            // Get the response instance
            HttpEntity entity = response.getEntity();

            // Determine response state
            if (response.getStatusLine().getStatusCode() >= 300) {
                throw new Exception(
                        "HTTP Request is not success, Response code is " + response.getStatusLine().getStatusCode());
            }

            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                responseContent = EntityUtils.toString(entity, CHARSET_UTF_8);
                EntityUtils.consume(entity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Release resources
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }


    /**
     * Send a POST request
     *
     * @param httpUrl address
     */
    public static String sendHttpPost(String httpUrl) {
        // Create the httpPost
        HttpPost httpPost = new HttpPost(httpUrl);
        return sendHttpPost(httpPost);
    }

    /**
     * Send a GET request
     *
     * @param httpUrl
     */
    public static String sendHttpGet(String httpUrl, String token, String headerName) {
        // Create a GET request
        HttpGet httpGet = new HttpGet(httpUrl);
        return sendHttpGet(httpGet, token, headerName);
    }

    public static String sendHttpGet(String httpUrl) {
        // Create a GET request
        HttpGet httpGet = new HttpGet(httpUrl);
        return sendHttpGet(httpGet);
    }

    /**
     * Send a POST request (with file)
     *
     * @param httpUrl   address
     * @param maps      parameter
     * @param fileLists The attachment
     */
    public static String sendHttpPost(String httpUrl, Map<String, String> maps, List<File> fileLists) {
        HttpPost httpPost = new HttpPost(httpUrl);
        MultipartEntityBuilder meBuilder = MultipartEntityBuilder.create();
        if (maps != null) {
            for (String key : maps.keySet()) {
                meBuilder.addPart(key, new StringBody(maps.get(key), ContentType.TEXT_PLAIN));
            }
        }
        if (fileLists != null) {
            for (File file : fileLists) {
                FileBody fileBody = new FileBody(file);
                meBuilder.addPart("files", fileBody);
            }
        }
        HttpEntity reqEntity = meBuilder.build();
        httpPost.setEntity(reqEntity);
        return sendHttpPost(httpPost);
    }

    /**
     * Send a POST request
     *
     * @param httpUrl address
     * @param params  parameter(format:key1=value1&key2=value2)
     */
    public static String sendHttpPost(String httpUrl, String params) {
        HttpPost httpPost = new HttpPost(httpUrl);
        try {
            if (params != null && params.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(params, "UTF-8");
                stringEntity.setContentType(CONTENT_TYPE_FORM_URL);
                httpPost.setEntity(stringEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendHttpPost(httpPost);
    }

    /**
     * Send a POST request
     *
     * @param maps parameter
     */
    public static String sendHttpPost(String httpUrl, Map<String, String> maps) {
        String parem = convertStringParamter(maps);
        return sendHttpPost(httpUrl, parem);
    }

    /**
     * Send a POST request to send JSON data
     *
     * @param httpUrl    address
     * @param paramsJson parameter(json)
     */
    public static String sendHttpPostJson(String httpUrl, String paramsJson) {
        HttpPost httpPost = new HttpPost(httpUrl);
        try {
            if (paramsJson != null && paramsJson.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(paramsJson, "UTF-8");
                stringEntity.setContentType(CONTENT_TYPE_JSON_URL);
                httpPost.setEntity(stringEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendHttpPost(httpPost);
    }

    /**
     * Converts the key-value pairs of the Map collection to the form: key1=value1& Key2 =value2
     *
     * @param parameterMap The set of key-value pairs that need to be transformed
     * @return string
     */
    public static String convertStringParamter(Map parameterMap) {
        StringBuffer parameterBuffer = new StringBuffer();
        if (parameterMap != null) {
            Iterator iterator = parameterMap.keySet().iterator();
            String key = null;
            String value = null;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                if (parameterMap.get(key) != null) {
                    value = (String) parameterMap.get(key);
                } else {
                    value = "";
                }
                parameterBuffer.append(key).append("=").append(value);
                if (iterator.hasNext()) {
                    parameterBuffer.append("&");
                }
            }
        }
        return parameterBuffer.toString();
    }

}

