package com.URL.URLShortener.Service

import com.URL.URLShortener.Bean.UrlDto
import com.URL.URLShortener.Bean.UrlReport
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

interface UrlService {

    fun generate(urlDto: UrlDto): Mono<String>

    fun redirect(shortUrl:String): Mono<String>

    fun getAll(): Flux<UrlReport>

    fun getAllByCreationDate(creationDate: LocalDate): Flux<UrlReport>

    fun getByHits(fetchDate: LocalDate): Flux<UrlReport>



}