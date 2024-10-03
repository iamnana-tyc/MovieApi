package com.iamnana.movieApi.exception;

public class FileExistException extends RuntimeException{

    public FileExistException(String message){
        super(message);
    }
}
