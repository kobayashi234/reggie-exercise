package com.demo.reggie.configuration;

import com.demo.reggie.common.JacksonObjectMapper;
import com.demo.reggie.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class MyMvcConfiguration implements WebMvcConfigurer {
    /**
     * 注册拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**") //拦截所有请求
                //允许直接访问的请求
                .excludePathPatterns("/employee/login","/employee/logout","/backend/**","/front/**",
                        "/user/login","/user/sendMsg");
    }

    /**
     * 扩展mvc消息转换器
     * @param converters
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建转换器对象
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，使用自定义转换方式
        converter.setObjectMapper(new JacksonObjectMapper());
        converters.add(0, converter);
    }
}
