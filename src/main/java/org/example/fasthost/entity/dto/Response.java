package org.example.fasthost.entity.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {

    private boolean success;
    private String message;
    private T data;

    public static <T> Response<T> success(String message) {
        return Response.<T>builder()
                .success(true)
                .message(message)
                .build();
    }

    public static <T> Response<T> success(String message, T data) {
        return Response.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> Response<T> error(String message) {
        return Response.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
