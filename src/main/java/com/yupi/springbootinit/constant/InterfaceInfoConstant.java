package com.yupi.springbootinit.constant;

import java.util.Arrays;
import java.util.List;

public interface InterfaceInfoConstant {
    /**
     * 最大接口名称长度
     */
    int MAX_NAME_LENGTH = 50;

    /**
     * URL正则
     */
    String URL_REGEX = "^((https?|ftp)://)?(www\\.)?([a-zA-Z0-9]+)\\.[a-zA-Z0-9]{2,}(\\.[a-zA-Z0-9]{2,})?(/[a-zA-Z0-9]+)*(/?\\?.*)?$";

    /**
     * 允许请求类型
     */
    List<String> ALLOW_METHODS = Arrays.asList("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS", "TRACE", "PATCH");

    /**
     * 必需请求/响应头
     */
    String REQUIRE_HEADER = "Content-Type";
}
