package yousang.lotto_rest.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import yousang.lotto_rest.dto.AnnuityLotteryResult
import yousang.lotto_rest.dto.AnnuityLotteryWinningNumbers
import yousang.lotto_rest.dto.ApiResponse
import yousang.lotto_rest.entity.AnnuityLottoPredictResult
import yousang.lotto_rest.entity.AnnuityLottoResult
import java.sql.Date
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class AnnuityLottoService(
    private val obj: LottoServiceComponent
) {
    suspend fun annuityLottoNumber(drwNo: Int): AnnuityLotteryResult? {
        val url = "https://dhlottery.co.kr/gameResult.do?method=win720"

        val response = Jsoup.connect(url).data("Round", drwNo.toString()).post()

        val winResultTag = response.selectFirst("div.win_result")

        val roundNumText = winResultTag?.selectFirst("strong")?.text()?.replace("|", "")?.replace("회", "")
        val roundNumQuery = roundNumText?.toInt()

        val drawDateText = winResultTag?.selectFirst("p.desc")?.text()
        val cleanedDateText = drawDateText?.replace("년", "-")?.replace("월", "-")?.replace("일 추첨", "")
            ?.replace("(", "")
            ?.replace(")", "")?.replace(" ", "")?.trim()
        val drawDate = Date.valueOf(LocalDate.parse(cleanedDateText, DateTimeFormatter.ofPattern("yyyy-MM-dd")))

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

        val result = roundNumQuery?.let {
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

        return result
    }

    suspend fun getLatestAnnuityDrwNo(): Int {
        return try {
            val latestLottoResult = withContext(Dispatchers.IO) {
                obj.annuityLottoResultRepository.findTopByOrderByDrwNoDesc()
            }
            latestLottoResult?.drwNo ?: 0
        } catch (e: Exception) {
            -1
        }
    }

    suspend fun fetchAndStoreAnnuityLottoNumber(drwNo: Int, mode: String): ApiResponse {
        return try {
            if (drwNo != 0) {
                if (mode == "last") {
                    val annuityLotteryResult = annuityLottoNumber(drwNo)

                    if (annuityLotteryResult != null) {
                        if (annuityLotteryResult.roundNum == drwNo) {
                            val annuityLottoResult =
                                ConvertService().annuityLottoNumberFromDTOToEntity(annuityLotteryResult)
                            withContext(Dispatchers.IO) {
                                obj.annuityLottoResultRepository.save(annuityLottoResult)
                            }

                            return ApiResponse(
                                statusCode = HttpStatus.OK.value(),
                                message = "Success",
                                data = annuityLotteryResult.roundNum
                            )
                        } else {
                            return ApiResponse(
                                statusCode = HttpStatus.NOT_MODIFIED.value(),
                                message = "No new numbers to fetch and store"
                            )
                        }
                    }
                } else if (mode == "all") {
                    val currentDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant()
                    val currentDate = Date.from(currentDateTime)
                    var firstDrwNo = 1

                    while (true) {
                        val annuityLotteryResult = annuityLottoNumber(firstDrwNo)

                        if (annuityLotteryResult != null) {
                            if (annuityLotteryResult.drawDate < currentDate) {
                                val annuityLottoResult =
                                    ConvertService().annuityLottoNumberFromDTOToEntity(annuityLotteryResult)
                                withContext(Dispatchers.IO) {
                                    obj.annuityLottoResultRepository.save(annuityLottoResult)
                                }
                                firstDrwNo++
                            } else if (annuityLotteryResult.drawDate > currentDate) {
                                return ApiResponse(
                                    statusCode = HttpStatus.OK.value(),
                                    message = "Success",
                                    data = annuityLotteryResult.drawDate
                                )
                            }
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

    suspend fun getAnnuityLottery(drwNo: Int): ApiResponse {
        return try {
            val result = withContext(Dispatchers.IO) {
                obj.annuityLottoResultRepository.findByDrwNo(drwNo)
            }
                ?: return ApiResponse(statusCode = HttpStatus.NOT_FOUND.value(), message = "No result found")
            ApiResponse(statusCode = HttpStatus.OK.value(), message = "Success", data = result)
        } catch (e: Exception) {
            ApiResponse(statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value(), message = "Error : ${e.message}")
        }
    }

    suspend fun getPredictLottoNumber(predictDrwNo: Int): ApiResponse {
        return try {
            val lottoPredictResults = withContext(Dispatchers.IO) {
                obj.annuityLottoPredictResultRepository.findAllByPredictDrwNo(predictDrwNo.toLong())
            }
            val result = lottoPredictResults?.let { ConvertService().predictAnnuityLottoNumberFromEntityToDTO(it) }
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
                obj.annuityLottoPredictResultRepository.findAllByPredictPerIsNull()
            }
            val lottoResults: MutableList<AnnuityLottoResult> = mutableListOf()

            if (lottoPredictResults != null) {
                val lottoNumbersMap = mutableMapOf<Long, List<Int>>()
                val lottoPredictNumbersMaps = mutableListOf<Map<Long?, List<Int>>>()

                for (lottoPredictResult in lottoPredictResults) {
                    val predictId = lottoPredictResult.predictDrwNo?.toLong()

                    val lottoPredictNumbersMap = mutableMapOf<Long?, List<Int>>()
                    lottoPredictNumbersMap[predictId] = (lottoPredictResult.predictEpoch?.let {
                        listOf(
                            lottoPredictResult.id,
                            lottoPredictResult.drwtNo1,
                            lottoPredictResult.drwtNo2,
                            lottoPredictResult.drwtNo3,
                            lottoPredictResult.drwtNo4,
                            lottoPredictResult.drwtNo5,
                            lottoPredictResult.drwtNo6,
                            it
                        )
                    } ?: listOf(
                        lottoPredictResult.id,
                        lottoPredictResult.drwtNo1,
                        lottoPredictResult.drwtNo2,
                        lottoPredictResult.drwtNo3,
                        lottoPredictResult.drwtNo4,
                        lottoPredictResult.drwtNo5,
                        lottoPredictResult.drwtNo6,
                        0
                    )) as List<Int>
                    lottoPredictNumbersMaps.add(lottoPredictNumbersMap)

                    val lottoResult = withContext(Dispatchers.IO) {
                        if (predictId != null) {
                            obj.annuityLottoResultRepository.findByDrwNo(predictId.toInt())
                        } else {
                            null // Explicitly return null if the condition is not met
                        }
                    }

                    if (lottoResult != null) {
                        lottoResults.add(lottoResult)
                    }
                }

                for (lottoResult in lottoResults) {
                    lottoNumbersMap[lottoResult.drwNo.toLong()] = listOf(
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
                                var matchCount = 0
                                for (i in value.indices) {
                                    if (value[i] == value1[i]) {
                                        matchCount++
                                    }
                                }

                                var per = matchCount.toDouble() / value.size.toDouble() * 100
                                per = String.format("%.2f", per).toDouble()

                                val newPredictLottoResult = AnnuityLottoPredictResult(
                                    id = value1.elementAt(0).toLong(),
                                    predictDrwNo = key.toInt(),
                                    drwtNo1 = value1.elementAt(1),
                                    drwtNo2 = value1.elementAt(2),
                                    drwtNo3 = value1.elementAt(3),
                                    drwtNo4 = value1.elementAt(4),
                                    drwtNo5 = value1.elementAt(5),
                                    drwtNo6 = value1.elementAt(6),
                                    predictPer = per.toBigDecimal(),
                                    predictEpoch = value1.elementAt(7).toBigInteger()
                                )

                                withContext(Dispatchers.IO) {
                                    obj.annuityLottoPredictResultRepository.save(newPredictLottoResult)
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