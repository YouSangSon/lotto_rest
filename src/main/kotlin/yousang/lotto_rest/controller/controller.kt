package yousang.lotto_rest.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import yousang.lotto_rest.dto.ApiResponse
import yousang.lotto_rest.service.LotteryService

@RestController
@RequestMapping("/lotto")
class LotteryController(private val lotteryService: LotteryService) {
    @GetMapping("/")
    fun exampleHelloWorld(): ApiResponse {
        println("hello world")
        return ApiResponse(
            statusCode = HttpStatus.OK.value(),
            message = "Hello world!"
        )
    }

    @PutMapping("/api/lotto-number/")
    fun putLottoNumber(@RequestParam mode: String): ApiResponse {
        val latestDrwNo = lotteryService.getLatestDrwNo()
        return lotteryService.fetchAndStoreLottoNumber(latestDrwNo + 1, mode)
    }

    @GetMapping("/api/lotto-number/")
    fun getLottoNumber(@RequestParam number: Number): ApiResponse {
        return lotteryService.getLottoNumber(number.toInt())
    }

    @GetMapping("/api/predict-lotto-number/")
    fun getPredictLottoNumber(@RequestParam number: Number): ApiResponse {
        return lotteryService.getPredictLottoNumber(number.toInt())
    }

    @GetMapping("/api/annuity-lotto-number/")
    fun getAnnuityLottoNumber(@RequestParam number: Number): ApiResponse {
        return lotteryService.getAnnuityLottery(number.toInt())
    }
}