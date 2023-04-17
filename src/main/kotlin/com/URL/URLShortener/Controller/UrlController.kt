package com.URL.URLShortener.Controller

import com.URL.URLShortener.Bean.UrlDto
import com.URL.URLShortener.Bean.UrlReport
import com.URL.URLShortener.Service.UrlServiceImpl
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI
import java.time.LocalDate

@RestController
class UrlController(var service: UrlServiceImpl) {

    /* Method to Generate Short Url */

    @PostMapping("/create")
    fun generate(@RequestBody urlDto: UrlDto): Mono<String> {
        return service.generate(urlDto);
    }

    /* Method to get Long Url Corresponding to short Url */

    @GetMapping("/miniurl.com/{shortUrl}")
            fun redirect(@PathVariable shortUrl:String,response:ServerHttpResponse):Mono<Void>
            {
                return service.redirect(shortUrl).
                flatMap{u ->
                       response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
                       response.getHeaders().setLocation(URI.create(u));
                        return@flatMap response.setComplete();
                          }
            }

    /* Method to get All Created Urls */

    @GetMapping("/getAll-CreatedUrls")
    fun getAll():Flux<UrlReport>
    {
        return service.getAll()
    }

    /* Method to Get All Created Urls on that day */

    @GetMapping("/getBy-CreationDate")
    fun getByCreationDate(@RequestParam(value="creationDate",required=false) creationDate: LocalDate?): Flux<UrlReport>
    {
        if(creationDate==null)
            return service.getAll()
        return service.getAllByCreationDate(creationDate)
    }

    /* Method to Get All Hitted Urls by Date */

    @GetMapping("/get-HitUrls")
    fun getHittedUrls(@RequestParam fetchDate:LocalDate?):Flux<UrlReport>
    {
        if(fetchDate==null)
            return service.getAll()
        return service.getByHits(fetchDate)
    }

}