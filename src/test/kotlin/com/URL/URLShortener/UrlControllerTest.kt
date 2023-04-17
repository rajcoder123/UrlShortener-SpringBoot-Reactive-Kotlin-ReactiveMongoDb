package com.URL.URLShortener

import com.URL.URLShortener.Bean.UrlDto
import com.URL.URLShortener.Bean.UrlReport
import com.URL.URLShortener.Controller.UrlController
import com.URL.URLShortener.Service.UrlServiceImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class UrlControllerTest:UrlShortenerApplicationTests(){

    @Mock
    lateinit var service:UrlServiceImpl

    @InjectMocks
    lateinit var controller:UrlController

   // lateinit var response:ServerHttpResponse


    @Test
    fun generate()
    {   var urlDto:UrlDto= UrlDto("https://www.google.com/search?q=www.gogle&oq=www.gogle&aqs=chrome..69i57j0i10i131i433i512j0i10i512l3j0i131i433i650j69i60j69i65.5683j0j7&sourceid=chrome&ie=UTF-8",
        "Ankit_Rajput")
        var result = "UserId = "+urlDto.userId+"\n"+"ShortUrl = "+"http://localhost:8080/miniurl.com/925d2811"

        Mockito.`when`(service.generate(urlDto)).thenReturn(Mono.just(result))

        var response:Mono<String> = controller.generate(urlDto)
        StepVerifier.create(response).expectNext(result).verifyComplete()

    }

   /* @Test
    fun redirect()
    {
        val longUrl:String="https://www.google.com/search?q=www.gogle&oq=www.gogle&aqs=chrome..69i57j0i10i131i433i512j0i10i512l3j0i131i433i650j69i60j69i65.5683j0j7&sourceid=chrome&ie=UTF-8"
        val shortUrl:String="925d2811"
        Mockito.`when`(service.redirect(shortUrl)).thenReturn(Mono.just(longUrl))

        val result:Mono<Void> = controller.redirect(shortUrl,response)
        StepVerifier.create(result).expectComplete()

    }*/

    @Test
    fun getByCreationDateTest()
    {
        var urlReport1: UrlReport = UrlReport("1","dfkdu433", LocalDate.now(), LocalDate.now(),1)
        var urlReport2: UrlReport = UrlReport("2","ljkdu433", LocalDate.now(), LocalDate.now(),2)
        var urlReport: Flux<UrlReport> = Flux.just(urlReport1,urlReport2)
        Mockito.`when`(service.getAllByCreationDate(LocalDate.now())).thenReturn(urlReport)
        var result: Flux<UrlReport> = controller.getByCreationDate(LocalDate.now())
        StepVerifier.create(result).expectNext(urlReport1).expectNext(urlReport2).verifyComplete()

    }

    @Test
    fun getAllTest() {
        var urlReport1: UrlReport = UrlReport("1", "dfkdu433", LocalDate.now(), LocalDate.now(), 1)
        var urlReport2: UrlReport = UrlReport("2", "ljkdu433", LocalDate.now(), LocalDate.now(), 2)
        var urlReport: Flux<UrlReport> = Flux.just(urlReport1, urlReport2)
        Mockito.`when`(service.getAll()).thenReturn(urlReport)
        var result: Flux<UrlReport> = controller.getAll()
        StepVerifier.create(result).expectNext(urlReport1).expectNext(urlReport2).verifyComplete()

    }

    @Test
    fun getByHitTest()
    {
        var urlReport1: UrlReport = UrlReport("1","dfkdu433", LocalDate.now().minusDays(1),
            LocalDate.now().minusDays(2),4)
        var urlReport2: UrlReport = UrlReport("2","ljkdu433", LocalDate.now(), LocalDate.now(),2)
        Mockito.`when`(service.getByHits(LocalDate.now().minusDays(1))).thenReturn(Flux.just(urlReport1))
        var result: Flux<UrlReport> = controller.getHittedUrls(LocalDate.now().minusDays(1))
        StepVerifier.create(result).expectNext(urlReport1).verifyComplete()

    }

}