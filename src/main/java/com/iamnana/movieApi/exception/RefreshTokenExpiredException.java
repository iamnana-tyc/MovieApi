package com.iamnana.movieApi.exception;

public class RefreshTokenExpiredException extends RuntimeException{
    public RefreshTokenExpiredException(String message){
        super(message);
    }
}
