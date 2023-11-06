package yousang.lotto_rest.repository

import org.springframework.data.jpa.repository.JpaRepository
import yousang.lotto_rest.entity.AnnuityLottoPredictResult
import yousang.lotto_rest.entity.AnnuityLottoResult
import yousang.lotto_rest.entity.LottoPredictResult
import yousang.lotto_rest.entity.LottoResult

interface LottoResultRepository : JpaRepository<LottoResult, Long> {
    fun findTopByOrderByDrwNoDesc(): LottoResult?
    fun findByDrwNo(drwNo: Int): LottoResult?
}

interface LottoPredictResultRepository : JpaRepository<LottoPredictResult, Long> {
    fun findAllByPredictDrwNo(predictDrwNo: Int): List<LottoPredictResult>?
    fun findAllByPredictPerIsNull(): List<LottoPredictResult>?
}

interface AnnuityLottoResultRepository : JpaRepository<AnnuityLottoResult, Long> {
    fun findTopByOrderByDrwNoDesc(): AnnuityLottoResult?
    fun findByDrwNo(drwNo: Int): AnnuityLottoResult?
}

interface AnnuityLottoPredictResultRepository : JpaRepository<AnnuityLottoPredictResult, Long> {
    fun findAllByPredictDrwNo(predictDrwNo: Long): List<AnnuityLottoPredictResult>?
    fun findAllByPredictPerIsNull(): List<AnnuityLottoPredictResult>?
}