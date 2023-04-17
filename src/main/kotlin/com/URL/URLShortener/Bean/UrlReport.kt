package com.URL.URLShortener.Bean

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
@Document(collection="UrlReportInfo")
data class UrlReport (@Id var id:String?,var shortUrl:String,@JsonFormat(pattern="yyyy-MM-dd") var fetchDate:LocalDate?,@JsonFormat(pattern="yyyy-MM-dd") var creationDate: LocalDate?,var hits:Long){
}