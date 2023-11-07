package com.haleon;

import java.net.URI;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Blob {

    // 容器名
    static final String CONTAINER = "blob";

    // blob连接字符串
    static final String CONNECT_STRING = "DefaultEndpointsProtocol=https;AccountName=cn03keyuatsa01;AccountKey=RWMFMaGd7I9lmxAPnhdbuajwy/u+AOuOz/QNdzk/C+kdYE3PYRmVvwAZFQ7fisSjNpoaEaT+IyDS+ASt2kU71A==;EndpointSuffix=core.chinacloudapi.cn";

    @FunctionName("blob")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = { HttpMethod.GET,
            }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("item");
        String blobPath = request.getBody().orElse(query);

        BlobContainerClient containerClient = buildClient();

        // 尝试获取blob对象，这里会产生一个Blob Client
        BlobClient blobClient = containerClient.getBlobClient(blobPath);
        boolean exists = blobClient.exists();

        if (!exists) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("资源不存在").build();
        } else {
            // 如果blob对象存在，获取二进制数据。
            // 可以直接将byte数组赋值给body()方法，看起来ResponseBuilder会自己决定对象的类型
            BinaryData binaryData = blobClient.downloadContent();
            return request.createResponseBuilder(HttpStatus.OK).body(binaryData.toBytes()).build();
        }
    }

    private BlobContainerClient buildClient() {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(CONNECT_STRING)
                .buildClient();
        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(CONTAINER);
        return blobContainerClient;
    }
}