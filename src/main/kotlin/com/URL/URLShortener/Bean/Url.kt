package com.URL.URLShortener.Bean

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
@Document(collection="UrlInfo")
data class Url(@Id var id:String?,var userId:String?,var longUrl:String?,var shortUrl:String,var creationDate:LocalDateTime,var expirationDate:LocalDateTime ) {
}