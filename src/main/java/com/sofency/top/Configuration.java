package com.sofency.top;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * @author sofency
 * @date 2020/9/20 0:50
 * @package IntelliJ IDEA
 * @description
 */
@Data
@ToString
@AllArgsConstructor
public class Configuration {
    private String url;
    private String username;
    private String password;
}
