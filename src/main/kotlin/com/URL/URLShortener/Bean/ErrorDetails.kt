package com.URL.URLShortener.Bean

data class ErrorDetails(var errorDetails: String,var message:String?) {
    constructor():this("","")
}