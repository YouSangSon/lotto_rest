package yousang.lotto_rest.service

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import yousang.lotto_rest.dto.ApiResponse
import yousang.lotto_rest.entity.LottoPredictResult
import yousang.lotto_rest.entity.LottoResult
import java.math.BigDecimal
import java.time.ZoneId
import java.util.*

@Service
class LottoService(
    private val obj: LottoServiceComponent
) {
    suspend fun lottoNumber(drwNo: Int, webClientBuilder: WebClient.Builder): LottoResult? {
        val baseURL = "http://www.dhlottery.co.kr"
        val response: String = webClientBuilder.baseUrl(baseURL)
            .build()
            .get()
            .uri("/common.do?method=getLottoNumber&drwNo=${drwNo}")
            .retrieve()
            .awaitBody<String>()

        return ObjectMapper().readValue(response, LottoResult::class.java)
    }
    suspend fun getLottoNumber(drwNo: Int): ApiResponse {
        return try {
            val lottoResult = withContext(Dispatchers.IO) {
                obj.lottoResultRepository.findByDrwNo(drwNo)
            }
                ?: return ApiResponse(statusCode = HttpStatus.NOT_FOUND.value(), message = "No result found")

            val result = ConvertService().lottoNumberFromEntityToDTO(lottoResult)

            return ApiResponse(statusCode = HttpStatus.OK.value(), message = "Success", data = result)
        } catch (e: Exception) {
            ApiResponse(statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value(), message = "Error : ${e.message}")
        }
    }
//
    suspend fun getLatestDrwNo(): Int {
        return try {
            withContext(Dispatchers.IO) {
                obj.lottoResultRepository.findTopByOrderByDrwNoDesc()
            }?.drwNo ?: 0
        } catch (e: Exception) {
            -1
        }
    }

    suspend fun fetchAndStoreLottoNumber(drwNo: Int, mode: String): ApiResponse {
        return try {
            if (drwNo != 0) {
                if (mode == "last") {
                    val lottoResult = lottoNumber(drwNo, obj.webClientBuilder)

                    if (lottoResult != null) {
                        return if (lottoResult.drwNo == drwNo) {
                            withContext(Dispatchers.IO) {
                                obj.lottoResultRepository.save(lottoResult)
                            }
                            ApiResponse(
                                statusCode = HttpStatus.OK.value(),
                                message = "Success",
                                data = lottoResult.drwNo
                            )
                        } else {
                            ApiResponse(
                                statusCode = HttpStatus.NOT_MODIFIED.value(),
                                message = "No new numbers to fetch and store"
                            )
                        }
                    }
                } else if (mode == "all") {
                    val currentDateTime = Date().toInstant().atZone(ZoneId.of("Asia/Seoul"))
                    var firstDrwNo = 1

                    while (true) {
                        val lottoResult = lottoNumber(firstDrwNo, obj.webClientBuilder)

                        if (lottoResult != null) {
                            if (lottoResult.drwNoDate < Date.from(currentDateTime.toInstant())) {
                                withContext(Dispatchers.IO) {
                                    obj.lottoResultRepository.save(lottoResult)
                                }
                            } else if (lottoResult.drwNoDate > Date.from(currentDateTime.toInstant())) {
                                return ApiResponse(
                                    statusCode = HttpStatus.OK.value(),
                                    message = "Success",
                                    data = lottoResult.drwNoDate
                                )
                            }
                            firstDrwNo++
                        }
                    }
                }
                return ApiResponse(statusCode = HttpStatus.BAD_REQUEST.value(), message = "Invalid mode", data = mode)
            } else {
                return ApiResponse(statusCode = HttpStatus.BAD_REQUEST.value(), message = "Invalid drwNo", data = drwNo)
            }
        } catch (e: Exception) {
            ApiResponse(statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value(), message = "Error : ${e.message}")
        }
    }

    suspend fun getPredictLottoNumber(predictDrwNo: Int): ApiResponse {
        return try {
            val lottoPredictResults = withContext(Dispatchers.IO) {
                obj.lottoPredictResultRepository.findAllByPredictDrwNo(predictDrwNo)
            }
            val result = lottoPredictResults?.let { ConvertService().predictLottoNumberFromEntityToDTO(it) }
                ?: return ApiResponse(statusCode = HttpStatus.NOT_FOUND.value(), message = "No result found")

            return ApiResponse(
                statusCode = HttpStatus.OK.value(),
                message = "Success",
                data = result
            )
        } catch (e: Exception) {
            ApiResponse(statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value(), message = "Error : ${e.message}")
        }
    }

    suspend fun compareLottoWinNumber(): ApiResponse {
        return try {
            val lottoPredictResults = withContext(Dispatchers.IO) {
                obj.lottoPredictResultRepository.findAllByPredictPerIsNull()
            }
            val lottoResults: MutableList<LottoResult> = mutableListOf()

            if (lottoPredictResults != null) {
                val lottoNumbersMap = mutableMapOf<Int, Set<Int>>()
                val lottoPredictNumbersMaps = mutableListOf<Map<Int, Set<Int>>>()

                for (lottoPredictResult in lottoPredictResults) {
                    val predictDrwNo = lottoPredictResult.predictDrwNo

                    val lottoPredictNumbersMap = mutableMapOf<Int, Set<Int>>()
                    lottoPredictNumbersMap[predictDrwNo] = (lottoPredictResult.predictEpoch?.let {
                        setOf(
                            lottoPredictResult.id,
                            lottoPredictResult.drwtNo1,
                            lottoPredictResult.drwtNo2,
                            lottoPredictResult.drwtNo3,
                            lottoPredictResult.drwtNo4,
                            lottoPredictResult.drwtNo5,
                            lottoPredictResult.drwtNo6,
                            it.toInt()
                        )
                    } ?: setOf(
                        lottoPredictResult.id,
                        lottoPredictResult.drwtNo1,
                        lottoPredictResult.drwtNo2,
                        lottoPredictResult.drwtNo3,
                        lottoPredictResult.drwtNo4,
                        lottoPredictResult.drwtNo5,
                        lottoPredictResult.drwtNo6,
                        0
                    )) as Set<Int>
                    lottoPredictNumbersMaps.add(lottoPredictNumbersMap)

                    val lottoResult = withContext(Dispatchers.IO) {
                        obj.lottoResultRepository.findByDrwNo(predictDrwNo)
                    }

                    if (lottoResult != null) {
                        lottoResults.add(lottoResult)
                    }
                }

                for (lottoResult in lottoResults) {
                    lottoNumbersMap[lottoResult.drwNo] = setOf(
                        lottoResult.drwtNo1,
                        lottoResult.drwtNo2,
                        lottoResult.drwtNo3,
                        lottoResult.drwtNo4,
                        lottoResult.drwtNo5,
                        lottoResult.drwtNo6
                    )
                }

                for ((key, value) in lottoNumbersMap) {
                    for (lottoPredictNumbersMap in lottoPredictNumbersMaps) {
                        for ((key1, value1) in lottoPredictNumbersMap) {
                            if (key == key1) {
                                if (value.intersect(value1).isNotEmpty()) {
                                    val intersectionSize = value1.intersect(value).size / 6.0 * 100

                                    val newPredictLottoResult = LottoPredictResult(
                                        id = value1.elementAt(0).toLong(),
                                        predictDrwNo = key,
                                        drwtNo1 = value1.elementAt(1),
                                        drwtNo2 = value1.elementAt(2),
                                        drwtNo3 = value1.elementAt(3),
                                        drwtNo4 = value1.elementAt(4),
                                        drwtNo5 = value1.elementAt(5),
                                        drwtNo6 = value1.elementAt(6),
                                        predictPer = intersectionSize.toBigDecimal(),
                                        predictEpoch = value1.elementAt(7).toBigInteger()
                                    )

                                    withContext(Dispatchers.IO) {
                                        obj.lottoPredictResultRepository.save(newPredictLottoResult)
                                    }
                                } else {
                                    val newPredictLottoResult = LottoPredictResult(
                                        id = value1.elementAt(0).toLong(),
                                        predictDrwNo = key,
                                        drwtNo1 = value1.elementAt(1),
                                        drwtNo2 = value1.elementAt(2),
                                        drwtNo3 = value1.elementAt(3),
                                        drwtNo4 = value1.elementAt(4),
                                        drwtNo5 = value1.elementAt(5),
                                        drwtNo6 = value1.elementAt(6),
                                        predictPer = BigDecimal(0),
                                        predictEpoch = value1.elementAt(7).toBigInteger()
                                    )

                                    withContext(Dispatchers.IO) {
                                        obj.lottoPredictResultRepository.save(newPredictLottoResult)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return ApiResponse(statusCode = HttpStatus.OK.value(), message = "Success")
        } catch (e: Exception) {
            ApiResponse(statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value(), message = "Error : ${e.message}")
        }
    }
}