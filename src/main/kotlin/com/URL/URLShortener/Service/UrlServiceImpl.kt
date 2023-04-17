package com.URL.URLShortener.Service
import com.URL.URLShortener.Bean.Url
import com.URL.URLShortener.Bean.UrlDto
import com.URL.URLShortener.Bean.UrlReport
import com.URL.URLShortener.Exception.*
import com.URL.URLShortener.Repository.UrlReportRepository
import com.URL.URLShortener.Repository.UrlRepository
import com.google.common.hash.Hashing
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalDateTime
import org.apache.commons.validator.routines.UrlValidator
@Service
class UrlServiceImpl (var repo: UrlRepository, var reportRepo: UrlReportRepository):UrlService {

    var domain:String="http://localhost:8080/miniurl.com/"

    /* Method to Create New Short Url */

    override fun generate(urlDto: UrlDto): Mono<String> {

        return Mono.just(urlDto).filter{urlDto->UrlValidator.getInstance().isValid(urlDto.longUrl) && !urlDto.longUrl!!.contains("miniurl")}
            .switchIfEmpty{Mono.error(ArguementNotValidException("Please Provide Valid Long Url"))}
            .filter {urlDto->!urlDto.userId.isNullOrBlank()}
            .switchIfEmpty{Mono.error(UserNotValidException("Please Provide a valid UserId"))}
            .filter { urlDto->urlDto.longUrl!!.length>50 }
            .switchIfEmpty {Mono.error(UrlLengthException("Long Url Must Contain at least 50 characters"))}
            .flatMap { urlDto->repo.findByUserIdAndLongUrl(urlDto.userId,urlDto.longUrl)
                .switchIfEmpty{createUrl(urlDto)}}
            .flatMap { url->reportRepo.findByShortUrl(url.shortUrl).switchIfEmpty{createReport(url)}
                .then(Mono.just("UserId = "+url.userId+"\n"+"ShortUrl = "+domain+url.shortUrl))}

    }

    /* Method to Redirect to long Url corresponding to Short Url */

    override fun redirect(shortUrl:String): Mono<String>{

        return Mono.just(shortUrl).filter { s->s.length==8 }
            .switchIfEmpty{Mono.error(UrlLengthException("Your Short Url is of 8 Characters"))}
            .flatMap {url->getEncodedUrl(shortUrl).switchIfEmpty{Mono.error(UrlNotFoundException("Url Does not exist"))}}
            .filter {url->url.expirationDate.isAfter(LocalDateTime.now())}
            .switchIfEmpty{deleteShortUrl(shortUrl).then(Mono.error(UrlTimeoutException("Url Expired")))}
            .flatMap{ url->reportRepo.findByShortUrlAndFetchDate(shortUrl,LocalDate.now())
                .flatMap{urlReport->updateHits(urlReport) }
                .switchIfEmpty {createNewUrlReport(url)}
                .then(Mono.just(url.longUrl!!))}

    }

    /* Method to get All created Urls Till Current Date */

    override fun getAll(): Flux<UrlReport> {

        return reportRepo.findAll()

    }

    /* Method to Get All created Urls By date */

    override fun getAllByCreationDate(creationDate: LocalDate): Flux<UrlReport>
    {
        return reportRepo.findByCreationDateAndFetchDate(creationDate,creationDate)
    }


    /* Method to Get All Urls Whose Hits are greater than 0 by date */

    override fun getByHits(fetchDate: LocalDate): Flux<UrlReport> {
        return reportRepo.findByFetchDate(fetchDate).filter { report->report.hits>0 }
    }

    /* Method to create Url Object */

    private fun createUrl(urlDto: UrlDto): Mono<Url> {
       return repo.save(Url(null,urlDto.userId,urlDto.longUrl,getEncodeUrl(urlDto.longUrl!!), LocalDateTime.now(), LocalDateTime.now().plusDays(1)))
    }

    /* Method to create UrlReport Object */

    private fun createReport(url: Url): Mono<UrlReport> {
        return reportRepo.save(
            UrlReport(null,url.shortUrl,
            LocalDate.now(),url.creationDate.toLocalDate(),0)
        )
    }

    /* Method to create New UrlReport Object */

    private fun createNewUrlReport(url: Url):Mono<UrlReport> {
        return reportRepo.save(
            UrlReport(null,url.shortUrl,
                LocalDate.now(),url.creationDate.toLocalDate(),1)
        )
    }

    /* Method to Generate Hashcode */

    private fun getEncodeUrl(longUrl: String): String {
       var encodeUrl:String=""
        var time:LocalDateTime= LocalDateTime.now()

        encodeUrl = Hashing.murmur3_32().hashString(longUrl.plus(time.toString()), StandardCharsets.UTF_8).toString();

        return encodeUrl;

    }

     /* Method to Update Hits Of Short Url */

    private fun updateHits(urlReport: UrlReport):Mono<UrlReport> {
        urlReport.hits=urlReport.hits+1
        return reportRepo.save(urlReport)


    }

    /* Method to Get Long Url form Database */

    private fun getEncodedUrl(shortUrl: String):Mono<Url> {
         return repo.findByShortUrl(shortUrl)
    }

    /* Method to Get Delete Url Object form Database When it Expired */

    private fun deleteShortUrl(shortUrl: String):Mono<Url> {
        return repo.findByShortUrl(shortUrl).flatMap {u->repo.delete(u).then(Mono.just(u))}
    }


}
