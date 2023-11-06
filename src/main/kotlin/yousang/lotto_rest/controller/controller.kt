package yousang.lotto_rest.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import yousang.lotto_rest.dto.ApiResponse
import yousang.lotto_rest.service.AnnuityLottoService
import yousang.lotto_rest.service.LottoService

@RestController
@RequestMapping("/lotto")
class LottoController(private val lottoService: LottoService, private val annuityLottoService: AnnuityLottoService) {
    @GetMapping("/")
    suspend fun exampleHelloWorld(): ApiResponse {
        return ApiResponse(
            statusCode = HttpStatus.OK.value(),
            message = "Hello world!"
        )
    }

    @PutMapping("/lotto-number/")
    suspend fun putLottoNumber(@RequestParam mode: String): ApiResponse {
        val latestDrwNo = lottoService.getLatestDrwNo()
        return lottoService.fetchAndStoreLottoNumber(latestDrwNo + 1, mode)
    }

    @GetMapping("/lotto-number/")
    suspend fun getLottoNumber(@RequestParam number: Int): ApiResponse {
        return lottoService.getLottoNumber(number)
    }

    @GetMapping("/predict-lotto-number/")
    suspend fun getPredictLottoNumber(@RequestParam number: Int): ApiResponse {
        return lottoService.getPredictLottoNumber(number)
    }

    @PutMapping("/compare/predict-lotto-number/")
    suspend fun putCompareLottoNumber(): ApiResponse {
        return lottoService.compareLottoWinNumber()
    }
//
    @PutMapping("/annuity-lotto-number/")
    suspend fun putAnnuityLottoNumber(@RequestParam mode: String): ApiResponse {
        val latestDrwNo = annuityLottoService.getLatestAnnuityDrwNo()
        return annuityLottoService.fetchAndStoreAnnuityLottoNumber(latestDrwNo + 1, mode)
    }

    @GetMapping("/annuity-lotto-number/")
    suspend fun getAnnuityLottoNumber(@RequestParam number: Int): ApiResponse {
        return annuityLottoService.getAnnuityLottery(number)
    }

    @GetMapping("/predict-annuity-lotto-number/")
    suspend fun getPredictAnnuityLottoNumber(@RequestParam number: Int): ApiResponse {
        return annuityLottoService.getPredictLottoNumber(number)
    }

    @PutMapping("/compare/predict-annuity-lotto-number/")
    suspend fun putCompareAnnuityLottoNumber(): ApiResponse {
        return annuityLottoService.compareLottoWinNumber()
    }
}