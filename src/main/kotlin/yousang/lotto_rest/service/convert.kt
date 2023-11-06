package yousang.lotto_rest.service

import yousang.lotto_rest.dto.AnnuityLotteryResult
import yousang.lotto_rest.dto.LottoNumberResult
import yousang.lotto_rest.dto.LottoPredictNumberResult
import yousang.lotto_rest.entity.AnnuityLottoPredictResult
import yousang.lotto_rest.entity.AnnuityLottoResult
import yousang.lotto_rest.entity.LottoPredictResult
import yousang.lotto_rest.entity.LottoResult
import java.time.ZoneId
import java.util.*

class ConvertService {
    fun lottoNumberFromEntityToDTO(lottoResult: LottoResult): LottoNumberResult {
        return LottoNumberResult(
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
    }

    fun predictLottoNumberFromEntityToDTO(lottoPredictResults: List<LottoPredictResult>): MutableList<LottoPredictNumberResult> {
        val predictLottoNumberResults = mutableListOf<LottoPredictNumberResult>()

        for (lottoPredictResult in lottoPredictResults) {
            val predictLottoNumberResult = LottoPredictNumberResult(
                predictDrwNo = lottoPredictResult.predictDrwNo,
                drwtNo1 = lottoPredictResult.drwtNo1,
                drwtNo2 = lottoPredictResult.drwtNo2,
                drwtNo3 = lottoPredictResult.drwtNo3,
                drwtNo4 = lottoPredictResult.drwtNo4,
                drwtNo5 = lottoPredictResult.drwtNo5,
                drwtNo6 = lottoPredictResult.drwtNo6,
                predictPer = lottoPredictResult.predictPer,
                predictEpoch = lottoPredictResult.predictEpoch
            )
            predictLottoNumberResults.add(predictLottoNumberResult)
        }

        return predictLottoNumberResults
    }

    fun annuityLottoNumberFromDTOToEntity(annuityLottoResult: AnnuityLotteryResult): AnnuityLottoResult {
        return AnnuityLottoResult(
            drwNo = annuityLottoResult.roundNum,
            drwNoDate =annuityLottoResult.drawDate,
            group = annuityLottoResult.winNums.group,
            drwtNo1 = annuityLottoResult.winNums.nums[0],
            drwtNo2 = annuityLottoResult.winNums.nums[1],
            drwtNo3 = annuityLottoResult.winNums.nums[2],
            drwtNo4 = annuityLottoResult.winNums.nums[3],
            drwtNo5 = annuityLottoResult.winNums.nums[4],
            drwtNo6 = annuityLottoResult.winNums.nums[5],
            bonusNo1 = annuityLottoResult.bonusNums[0],
            bonusNo2 = annuityLottoResult.bonusNums[1],
            bonusNo3 = annuityLottoResult.bonusNums[2],
            bonusNo4 = annuityLottoResult.bonusNums[3],
            bonusNo5 = annuityLottoResult.bonusNums[4],
            bonusNo6 = annuityLottoResult.bonusNums[5],
        )
    }

    fun annuityLottoNumberFromEntityToDTO(annuityLottoResult: AnnuityLottoResult): AnnuityLotteryResult {
        return AnnuityLotteryResult(
            roundNum = annuityLottoResult.drwNo,
            drawDate = annuityLottoResult.drwNoDate,
            winNums = yousang.lotto_rest.dto.AnnuityLotteryWinningNumbers(
                group = annuityLottoResult.group,
                nums = listOf(
                    annuityLottoResult.drwtNo1,
                    annuityLottoResult.drwtNo2,
                    annuityLottoResult.drwtNo3,
                    annuityLottoResult.drwtNo4,
                    annuityLottoResult.drwtNo5,
                    annuityLottoResult.drwtNo6,
                )
            ),
            bonusNums = listOf(
                annuityLottoResult.bonusNo1,
                annuityLottoResult.bonusNo2,
                annuityLottoResult.bonusNo3,
                annuityLottoResult.bonusNo4,
                annuityLottoResult.bonusNo5,
                annuityLottoResult.bonusNo6,
            )
        )
    }

    fun predictAnnuityLottoNumberFromEntityToDTO(lottoPredictResults: List<AnnuityLottoPredictResult>): MutableList<LottoPredictNumberResult> {
        val predictLottoNumberResults = mutableListOf<LottoPredictNumberResult>()

        for (lottoPredictResult in lottoPredictResults) {
            val predictLottoNumberResult = LottoPredictNumberResult(
                predictDrwNo = lottoPredictResult.predictDrwNo,
                drwtNo1 = lottoPredictResult.drwtNo1,
                drwtNo2 = lottoPredictResult.drwtNo2,
                drwtNo3 = lottoPredictResult.drwtNo3,
                drwtNo4 = lottoPredictResult.drwtNo4,
                drwtNo5 = lottoPredictResult.drwtNo5,
                drwtNo6 = lottoPredictResult.drwtNo6,
                predictPer = lottoPredictResult.predictPer,
                predictEpoch = lottoPredictResult.predictEpoch
            )
            predictLottoNumberResults.add(predictLottoNumberResult)
        }

        return predictLottoNumberResults
    }
}