package com.URL.URLShortener

import com.URL.URLShortener.Bean.Url
import com.URL.URLShortener.Bean.UrlDto
import com.URL.URLShortener.Bean.UrlReport
import com.URL.URLShortener.Exception.ArguementNotValidException
import com.URL.URLShortener.Exception.UrlLengthException
import com.URL.URLShortener.Repository.UrlReportRepository
import com.URL.URLShortener.Repository.UrlRepository
import com.URL.URLShortener.Service.UrlServiceImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class UrlServicePostTest: UrlShortenerApplicationTests() {
    @Mock
    lateinit var reportRepo: UrlReportRepository

    @Mock
    lateinit var repo:UrlRepository


    @InjectMocks
    lateinit var service:UrlServiceImpl

    @Test
    fun objedtNotInDbTest()
    {
        var urlDto:UrlDto= UrlDto("https://cloud.mongodb.com/v2/624dc2dad5e231382bcf9505#/metrics/replicaSet/63e1e41afccd251e48b2a98a/explorer/UrlInfo/UrlReport/find",
            "Ankit_Rajput")

        var result = "UserId = "+urlDto.userId+"\n"+"ShortUrl = "+"http://localhost:8080/miniurl.com/925d2811";

        var shortUrl = "925d2811"

        var url:Url= Url("1",urlDto.userId,urlDto.longUrl,shortUrl, LocalDateTime.now(), LocalDateTime.now().plusDays(1))

        var urlReport:UrlReport= UrlReport("1",shortUrl, LocalDate.now(),url.creationDate.toLocalDate(),0)

         Mockito.`when`(repo.findByUserIdAndLongUrl(urlDto.userId,urlDto.longUrl)).thenReturn(Mono.empty())
        Mockito.`when`(repo.save(Mockito.any(Url::class.java))).thenReturn(Mono.just(url))
        Mockito.`when`(reportRepo.findByShortUrl(shortUrl)).thenReturn(Mono.empty())
        Mockito.`when`(reportRepo.save(Mockito.any(urlReport::class.java))).thenReturn(Mono.just(urlReport))

     var response:Mono<String> = service.generate(urlDto)
        StepVerifier
            .create(response).expectNext(result).verifyComplete()

    }

    @Test
    fun objectInDbTest()
    {
        var urlDto:UrlDto= UrlDto("https://cloud.mongodb.com/v2/624dc2dad5e231382bcf9505#/metrics/replicaSet/63e1e41afccd251e48b2a98a/explorer/UrlInfo/UrlReport/find",
            "Ankit_Rajput")

        var result = "UserId = "+urlDto.userId+"\n"+"ShortUrl = "+"http://localhost:8080/miniurl.com/925d2811";

        var shortUrl = "925d2811"

        var url:Url= Url("1",urlDto.userId,urlDto.longUrl,shortUrl, LocalDateTime.now(), LocalDateTime.now().plusDays(1))

        var urlReport:UrlReport= UrlReport("1",shortUrl, LocalDate.now(),url.creationDate.toLocalDate(),0)

        Mockito.lenient().`when`(repo.findByUserIdAndLongUrl(urlDto.userId,urlDto.longUrl)).thenReturn(Mono.just(url))
        Mockito.lenient().`when`(repo.save(Mockito.any(Url::class.java))).thenReturn(Mono.just(url))
        Mockito.lenient().`when`(reportRepo.findByShortUrl(shortUrl)).thenReturn(Mono.just(urlReport))
        Mockito.lenient().`when`(reportRepo.save(Mockito.any(urlReport::class.java))).thenReturn(Mono.just(urlReport))

        var response:Mono<String> = service.generate(urlDto)
        StepVerifier
            .create(response).expectNext(result).verifyComplete()

    }

    @Test
    fun blankTest()
    {
        var urlDto:UrlDto= UrlDto("",
            "Ankit_Rajput")

        var response:Mono<String> = service.generate(urlDto)
        StepVerifier
            .create(response).verifyError(ArguementNotValidException::class.java)


    }

    @Test
    fun domainTest()
    {
        var urlDto:UrlDto= UrlDto("localhost:8080/miniurl.com/797f8952",
            "Ankit_Rajput")

        var response:Mono<String> = service.generate(urlDto)
        StepVerifier
            .create(response).verifyError(ArguementNotValidException::class.java)


    }

    @Test
    fun urlIsShort()
    {
        var urlDto:UrlDto= UrlDto("https://www.google.com/",
            "Ankit_Rajput")

        var response:Mono<String> = service.generate(urlDto)
        StepVerifier
            .create(response).verifyError(UrlLengthException::class.java)
    }


}