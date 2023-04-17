package com.URL.URLShortener.Repository

import com.URL.URLShortener.Bean.Url
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

 /* Repository to Store Url Object  */

@Repository
interface UrlRepository:ReactiveMongoRepository<Url,String>{

    fun findByUserIdAndLongUrl(userId: String?, longUrl: String?): Mono<Url>

    fun findByShortUrl(shortUrl: String): Mono<Url>
}