package yousang.lotto_rest.repository

import yousang.lotto_rest.entity.*
import org.springframework.data.jpa.repository.JpaRepository

interface LottoResultRepository : JpaRepository<LottoResult, Long> {
    fun findTopByOrderByDrwNoDesc(): LottoResult?
    fun findByDrwNo(drwNo: Int): LottoResult?
}

interface LottoPredictResultRepository : JpaRepository<LottoPredictResult, Long> {
    fun findByPredictDrwNo(predictDrwNo: Int): LottoPredictResult?
    fun findAllBy(): List<LottoPredictResult>
}