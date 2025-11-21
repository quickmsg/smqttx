package io.github.quickmsg;

import reactor.core.publisher.Mono;

public class Test3 {
    private static final String key="key";
    public static void main(String[] args) {
        Mono.deferContextual(contextView -> {
            // 访问上下文中的数据
            String value = contextView.get(key);
            // 根据上下文中的数据创建和返回一个新的Mono
            return Mono.just("Value from context: " + value);
        }).subscribe();
    }
}
