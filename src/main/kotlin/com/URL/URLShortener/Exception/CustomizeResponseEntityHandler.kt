package com.URL.URLShortener.Exception

import com.URL.URLShortener.Bean.ErrorDetails
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class CustomizeResponseEntityHandler {

    @ExceptionHandler(ArguementNotValidException::class)
    fun notValidArguement(ex:Exception): ResponseEntity<ErrorDetails> {
        var error= ErrorDetails()
        error.errorDetails="Your Given Url is not Valid"
        error.message=ex.message.toString()
        return ResponseEntity<ErrorDetails>(error, HttpStatus.BAD_REQUEST)

    }

    @ExceptionHandler(UserNotValidException::class)
    fun notValidUser(ex:Exception): ResponseEntity<ErrorDetails> {
        var error= ErrorDetails()
        error.errorDetails="UserId is not Valid"
        error.message=ex.message.toString()
        return ResponseEntity<ErrorDetails>(error, HttpStatus.BAD_REQUEST)

    }


    @ExceptionHandler(UrlLengthException::class)
    fun urlLengthShort(ex:Exception): ResponseEntity<ErrorDetails> {
        var error= ErrorDetails()
        error.errorDetails="Your Given Url length is Not Acceptable"
        error.message=ex.message.toString()
        return ResponseEntity<ErrorDetails>(error, HttpStatus.NOT_ACCEPTABLE)

    }

    @ExceptionHandler(UrlNotFoundException::class)
    fun urlNotExist(ex:Exception): ResponseEntity<ErrorDetails> {
        var error= ErrorDetails()
        error.errorDetails="Given Url Not Present in the DataBase"
        error.message=ex.message.toString()
        return ResponseEntity<ErrorDetails>(error, HttpStatus.NOT_FOUND)

    }

    @ExceptionHandler(UrlTimeoutException::class)
    fun urlExpired(ex:Exception): ResponseEntity<ErrorDetails> {
        var error= ErrorDetails()
        error.errorDetails="Given Url Expired"
        error.message=ex.message.toString()
        return ResponseEntity<ErrorDetails>(error, HttpStatus.GATEWAY_TIMEOUT)

    }
}