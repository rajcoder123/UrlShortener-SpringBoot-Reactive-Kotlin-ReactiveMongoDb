package com.URL.URLShortener

import com.URL.URLShortener.Bean.Url
import com.URL.URLShortener.Bean.UrlReport
import com.URL.URLShortener.Exception.UrlLengthException
import com.URL.URLShortener.Exception.UrlNotFoundException
import com.URL.URLShortener.Exception.UrlTimeoutException
import com.URL.URLShortener.Repository.UrlReportRepository
import com.URL.URLShortener.Repository.UrlRepository
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
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class UrlServiceGetTest:UrlShortenerApplicationTests(){

    @Mock
    lateinit var reportRepo: UrlReportRepository

    @Mock
    lateinit var repo: UrlRepository


    @InjectMocks
    lateinit var service: UrlServiceImpl

    @Test
    fun lengthTest()
    {
        var shortUrl:String="88bfd"

        var response:Mono<String> = service.redirect(shortUrl)

        StepVerifier.create(response).verifyError(UrlLengthException::class.java)
    }

    @Test
    fun urlNotInDbTest()
    {
        var shortUrl:String="88bfd2hu"
        Mockito.`when`(repo.findByShortUrl(shortUrl)).thenReturn(Mono.empty())
        var response:Mono<String> =service.redirect(shortUrl)
        StepVerifier.create(response).verifyError(UrlNotFoundException::class.java)
    }


    @Test
    fun urlExpiredTest()
    {
        var longUrl:String="https://www.google.com/search?q=www.gogle&oq=www.gogle&aqs=chrome..69i57j0i10i131i433i512j0i10i512l3j0i131i433i650j69i60j69i65.5683j0j7&sourceid=chrome&ie=UTF-8"
        var shortUrl:String="843jdks9"

        var url: Url =Url("1", "Ankit", longUrl, shortUrl, LocalDateTime.now(),
            LocalDateTime.now().minusSeconds(10))

        Mockito.`when`(repo.findByShortUrl(shortUrl)).thenReturn(Mono.just(url))
        Mockito.`when`(repo.delete(url)).thenReturn(Mono.empty())

        var response:Mono<String> = service.redirect(shortUrl)
        StepVerifier.create(response).verifyError(UrlTimeoutException::class.java)
    }

    @Test
    fun urlReportAndRedirectTest()
    {
        var longUrl:String="https://www.google.com/search?q=www.gogle&oq=www.gogle&aqs=chrome..69i57j0i10i131i433i512j0i10i512l3j0i131i433i650j69i60j69i65.5683j0j7&sourceid=chrome&ie=UTF-8"
        var shortUrl:String="843jdks9"

        var url: Url =Url("1", "Ankit", longUrl, shortUrl, LocalDateTime.now(),
            LocalDateTime.now().plusDays(1))

        var urlReport:UrlReport=UrlReport("2",shortUrl, LocalDate.now(),url.creationDate.toLocalDate(),0)
        var newUrlReport:UrlReport=UrlReport("2",shortUrl, LocalDate.now(),url.creationDate.toLocalDate(),1)
        Mockito.`when`(repo.findByShortUrl(shortUrl)).thenReturn(Mono.just(url))
        Mockito.`when`(reportRepo.findByShortUrlAndFetchDate(shortUrl, LocalDate.now())).thenReturn(Mono.just(urlReport))
        Mockito.`when`(reportRepo.save(Mockito.any(UrlReport::class.java))).thenReturn(Mono.just(newUrlReport))

        var response:Mono<String> =service.redirect(shortUrl)

        StepVerifier.create(response).expectNext(longUrl).verifyComplete()

    }

    @Test
    fun getByCreationDateTest()
    {
        var urlReport1:UrlReport= UrlReport("1","dfkdu433", LocalDate.now(),LocalDate.now(),1)
        var urlReport2:UrlReport= UrlReport("2","ljkdu433", LocalDate.now(),LocalDate.now(),2)
        var urlReport: Flux<UrlReport> = Flux.just(urlReport1,urlReport2)
        Mockito.`when`(reportRepo.findByCreationDateAndFetchDate(LocalDate.now(), LocalDate.now())).thenReturn(urlReport)
        var result:Flux<UrlReport> = service.getAllByCreationDate(LocalDate.now())
        StepVerifier.create(result).expectNext(urlReport1).expectNext(urlReport2).verifyComplete()

    }

    @Test
    fun getAllTest() {
        var urlReport1: UrlReport = UrlReport("1", "dfkdu433", LocalDate.now(), LocalDate.now(), 1)
        var urlReport2: UrlReport = UrlReport("2", "ljkdu433", LocalDate.now(), LocalDate.now(), 2)
        var urlReport: Flux<UrlReport> = Flux.just(urlReport1, urlReport2)
        Mockito.`when`(reportRepo.findAll()).thenReturn(urlReport)
        var result: Flux<UrlReport> = service.getAll()
        StepVerifier.create(result).expectNext(urlReport1).expectNext(urlReport2).verifyComplete()

    }

    @Test
    fun getByHitTest()
    {
        var urlReport1:UrlReport= UrlReport("1","dfkdu433", LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),4)
        var urlReport2:UrlReport= UrlReport("2","ljkdu433", LocalDate.now(),LocalDate.now(),2)
        var urlReport: Flux<UrlReport> = Flux.just(urlReport1,urlReport2)
        Mockito.`when`(reportRepo.findByFetchDate(LocalDate.now().minusDays(1))).thenReturn(Flux.just(urlReport1))
        var result:Flux<UrlReport> = service.getByHits(LocalDate.now().minusDays(1))
        StepVerifier.create(result).expectNext(urlReport1).verifyComplete()

    }




    }