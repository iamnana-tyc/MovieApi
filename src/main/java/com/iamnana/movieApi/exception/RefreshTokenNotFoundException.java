package com.iamnana.movieApi.exception;

public class RefreshTokenNotFoundException extends RuntimeException{
    public RefreshTokenNotFoundException(String message){
        super(message);
    }
}
