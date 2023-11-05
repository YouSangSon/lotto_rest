package yousang.lotto_rest.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.jsoup.Jsoup
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import yousang.lotto_rest.dto.*
import yousang.lotto_rest.entity.LottoResult
import yousang.lotto_rest.repository.LottoPredictResultRepository
import yousang.lotto_rest.repository.LottoResultRepository
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


@Service
class LotteryService(
    private val webClientBuilder: WebClient.Builder,
    private val lottoResultRepository: LottoResultRepository,
    private val lottoPredictResultRepository: LottoPredictResultRepository
) {
    private val baseURL = "http://www.dhlottery.co.kr"

    fun fetchAndStoreLottoNumber(drwNo: Int, mode: String): ApiResponse {

        if (mode == "last") {
            var response: String = webClientBuilder.baseUrl(baseURL)
                .build()
                .get()
                .uri("/common.do?method=getLottoNumber&drwNo=${drwNo}")
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
                ?: throw RuntimeException("Failed to fetch data")

            val lottoResult: LottoResult = ObjectMapper().readValue(response, LottoResult::class.java)

            if (lottoResult.drwNo == drwNo) {
                lottoResultRepository.save(lottoResult)
                return ApiResponse(statusCode = HttpStatus.OK.value(), message = "Success", data = lottoResult.drwNo)
            } else {
                return ApiResponse(
                    statusCode = HttpStatus.NOT_MODIFIED.value(),
                    message = "No new numbers to fetch and store"
                )
            }
        } else if (mode == "all") {
            val currentDateTime = Date().toInstant().atZone(ZoneId.of("Asia/Seoul"))

            var firstDrwNo = 1
            while (true) {
                var response: String = webClientBuilder.baseUrl(baseURL)
                    .build()
                    .get()
                    .uri("/common.do?method=getLottoNumber&drwNo=${firstDrwNo}")
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .block()
                    ?: throw RuntimeException("Failed to fetch data")

                val lottoResult: LottoResult = ObjectMapper().readValue(response, LottoResult::class.java)

                if (lottoResult.drwNoDate < Date.from(currentDateTime.toInstant())) {
                    lottoResultRepository.save(lottoResult)
                    firstDrwNo++
                } else if (lottoResult.drwNoDate > Date.from(currentDateTime.toInstant())) {
                    return ApiResponse(
                        statusCode = HttpStatus.OK.value(),
                        message = "Success",
                        data = lottoResult.drwNoDate
                    )
                    break
                }
            }
        } else {
            return ApiResponse(statusCode = HttpStatus.BAD_REQUEST.value(), message = "Invalid mode", data = mode)
        }
    }

    fun getLatestDrwNo(): Int {
        val latestLottoResult = lottoResultRepository.findTopByOrderByDrwNoDesc()
        return latestLottoResult?.drwNo ?: 0  // returns 0 if no result is found
    }

    fun getLottoNumber(drwNo: Int): ApiResponse {
        val lottoResult = lottoResultRepository.findByDrwNo(drwNo)
            ?: return ApiResponse(statusCode = HttpStatus.NOT_FOUND.value(), message = "No result found")
        val lottoNumberResult = LottoNumberResult(
            drwNo = lottoResult.drwNo,
            drwNoDate = lottoResult.drwNoDate.toString(),
            drwtNo1 = lottoResult.drwtNo1,
            drwtNo2 = lottoResult.drwtNo2,
            drwtNo3 = lottoResult.drwtNo3,
            drwtNo4 = lottoResult.drwtNo4,
            drwtNo5 = lottoResult.drwtNo5,
            drwtNo6 = lottoResult.drwtNo6,
            bnusNo = lottoResult.bnusNo,
            firstAccumamnt = lottoResult.firstAccumamnt,
            firstPrzwnerCo = lottoResult.firstPrzwnerCo,
            firstWinamnt = lottoResult.firstWinamnt,
            totSellamnt = lottoResult.totSellamnt
        )
        return ApiResponse(statusCode = HttpStatus.OK.value(), message = "Success", data = lottoNumberResult)
    }

    fun getPredictLottoNumber(predictDrwNo: Int): ApiResponse {
        if (predictDrwNo == 0) {
            val predictLottoNumberResults = lottoPredictResultRepository.findAllBy()
            return ApiResponse(
                statusCode = HttpStatus.OK.value(),
                message = "Success",
                data = predictLottoNumberResults
            )
        } else {
            val lottoPredictResult = lottoPredictResultRepository.findByPredictDrwNo(predictDrwNo)
                ?: return ApiResponse(statusCode = HttpStatus.NOT_FOUND.value(), message = "No result found")
            val predictLottoNumberResult = LottoPredictNumberResult(
                predictDrwNo = lottoPredictResult.predictDrwNo,
                drwtNo1 = lottoPredictResult.drwtNo1,
                drwtNo2 = lottoPredictResult.drwtNo2,
                drwtNo3 = lottoPredictResult.drwtNo3,
                drwtNo4 = lottoPredictResult.drwtNo4,
                drwtNo5 = lottoPredictResult.drwtNo5,
                drwtNo6 = lottoPredictResult.drwtNo6
            )
            return ApiResponse(statusCode = HttpStatus.OK.value(), message = "Success", data = predictLottoNumberResult)
        }
    }

    fun getAnnuityLottery(drwNo: Int): ApiResponse {
        return try {
            val url = "https://dhlottery.co.kr/gameResult.do?method=win720"

            val response = Jsoup.connect(url).data("Round", drwNo.toString()).post()

            val winResultTag = response.selectFirst("div.win_result")

            val roundNumText = winResultTag?.selectFirst("strong")?.text()?.replace("|", "")?.replace("회", "")
            val roundNumQuery = roundNumText?.toInt()

            val drawDateText = winResultTag?.selectFirst("p.desc")?.text()
            val cleanedDateText = drawDateText?.replace("년", "-")?.replace("월", "-")?.replace("일 추첨", "")
                ?.replace("(", "")
                ?.replace(")", "")?.replace(" ", "")?.trim()
            val drawDate = LocalDate.parse(cleanedDateText, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            val win720NumTags = winResultTag?.select("div.win720_num")

            val divGroup = win720NumTags?.get(0)?.selectFirst("div.group")
            val numGroup = divGroup?.select("span")?.get(1)?.text()?.toInt()

            val win720Nums = mutableListOf<Int>()
            for (i in 1..6) {
                val num = win720NumTags?.get(0)?.selectFirst("span.num.al720_color${i}.large span")?.text()?.toInt()
                if (num != null) {
                    win720Nums.add(num)
                }
            }

            val bonusNums = mutableListOf<Int>()
            for (i in 1..6) {
                val num = win720NumTags?.get(1)?.selectFirst("span.num.al720_color${i}.large span")?.text()?.toInt()
                if (num != null) {
                    bonusNums.add(num)
                }
            }

            var result = roundNumQuery?.let {
                AnnuityLotteryResult(
                    roundNum = it,
                    drawDate = drawDate,
                    winNums = AnnuityLotteryWinningNumbers(
                        group = numGroup ?: 0,
                        nums = win720Nums
                    ),
                    bonusNums = bonusNums
                )
            }

            ApiResponse(statusCode = HttpStatus.OK.value(), message = "Success", data = result)
        } catch (e: Exception) {
            ApiResponse(statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value(), message = "Error : ${e.message}")
        }
    }
}