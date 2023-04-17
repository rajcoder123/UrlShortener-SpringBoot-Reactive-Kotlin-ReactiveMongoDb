package com.URL.URLShortener.Repository

import com.URL.URLShortener.Bean.UrlReport
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

   /* Repository to Store UrlReport Object  */

@Repository
interface UrlReportRepository:ReactiveMongoRepository<UrlReport,String> {

    fun findByShortUrl(shortUrl:String):Mono<UrlReport>

    fun findByShortUrlAndFetchDate(shortUrl:String,fetchDate:LocalDate):Mono<UrlReport>

    fun findByCreationDateAndFetchDate(creationDate:LocalDate?,fetchDate:LocalDate?): Flux<UrlReport>

    fun findByFetchDate(fetchDate: LocalDate?): Flux<UrlReport>
}