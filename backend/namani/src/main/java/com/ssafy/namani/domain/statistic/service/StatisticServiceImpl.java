package com.ssafy.namani.domain.statistic.service;

import com.ssafy.namani.domain.category.entity.Category;
import com.ssafy.namani.domain.category.repository.CategoryRepository;
import com.ssafy.namani.domain.member.entity.Member;
import com.ssafy.namani.domain.member.repository.MemberRepository;
import com.ssafy.namani.domain.statistic.dto.response.DailyStatisticAmountByCategoryResponseDto;
import com.ssafy.namani.domain.statistic.dto.response.DailyStatisticDetailByRegDateResponseDto;
import com.ssafy.namani.domain.statistic.dto.response.MonthlyStatisticAmountByCategoryResponseDto;
import com.ssafy.namani.domain.statistic.dto.response.MonthlyStatisticDetailByRegDateResponseDto;
import com.ssafy.namani.domain.statistic.entity.DailyStatistic;
import com.ssafy.namani.domain.statistic.entity.IMonthlyStatisticAvg;
import com.ssafy.namani.domain.statistic.entity.MonthlyStatistic;
import com.ssafy.namani.domain.statistic.repository.DailyStatisticRepository;
import com.ssafy.namani.domain.statistic.repository.MonthlyStatisticRepository;
import com.ssafy.namani.global.response.BaseException;
import com.ssafy.namani.global.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final DailyStatisticRepository dailyStatisticRepository;
    private final MonthlyStatisticRepository monthlyStatisticRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public void checkDailyStatistic(UUID memberId, Timestamp curDate) {
        List<Category> categoryList = categoryRepository.findAll();
        Member member = memberRepository.findById(memberId).get();

        // 월간 통계 정보 체크
        Optional<List<DailyStatistic>> dailyStatisticListOptional = dailyStatisticRepository.findAllByMemberIdRegDate(memberId, curDate);
        if (!dailyStatisticListOptional.isPresent()) {
            for (Category category : categoryList) {
                DailyStatistic dailyStatistic = DailyStatistic.builder()
                        .member(member)
                        .category(category)
                        .amount(0)
                        .regDate(curDate)
                        .build();

                dailyStatisticRepository.save(dailyStatistic);
            }
        } else {
            for (Category category : categoryList) {
                // 사용자 + 카테고리 + 연월로 통계 정보 체크
                Optional<DailyStatistic> dailyStatisticOptional = dailyStatisticRepository.findByMemberIdCategoryIdRegDate(memberId, category.getId(), curDate);
                if (!dailyStatisticOptional.isPresent()) {
                    DailyStatistic dailyStatistic = DailyStatistic.builder()
                            .member(member)
                            .category(category)
                            .amount(0)
                            .regDate(curDate)
                            .build();

                    dailyStatisticRepository.save(dailyStatistic);
                }
            }
        }
    }

    /**
     * 월간 통계 존재 여부 체크
     *
     * @param memberId
     * @param curDate
     */
    @Override
    public void checkMonthlyStatistic(UUID memberId, Timestamp curDate) {
        List<Category> categoryList = categoryRepository.findAll();
        Member member = memberRepository.findById(memberId).get();

        // 월간 통계 정보 체크
        Optional<List<MonthlyStatistic>> monthlyStatisticListOptional = monthlyStatisticRepository.findAllByMemberIdRegDate(memberId, curDate);
        if (!monthlyStatisticListOptional.isPresent()) {
            for (Category category : categoryList) {
                MonthlyStatistic monthlyStatistic = MonthlyStatistic.builder()
                        .member(member)
                        .category(category)
                        .amount(0)
                        .regDate(curDate)
                        .build();

                monthlyStatisticRepository.save(monthlyStatistic);
            }
        } else {
            for (Category category : categoryList) {
                // 사용자 + 카테고리 + 연월로 통계 정보 체크
                Optional<MonthlyStatistic> monthlyStatisticOptional = monthlyStatisticRepository.findByMemberIdCategoryIdRegDate(memberId, category.getId(), curDate);
                if (!monthlyStatisticOptional.isPresent()) {
                    MonthlyStatistic monthlyStatistic = MonthlyStatistic.builder()
                            .member(member)
                            .category(category)
                            .amount(0)
                            .regDate(curDate)
                            .build();

                    monthlyStatisticRepository.save(monthlyStatistic);
                }
            }
        }
    }

    /**
     * 로그인 한 사용자의 아이디 + 특정 날짜에 해당하는 일간 통계 정보 조회
     *
     * @param memberId
     * @param curDate
     * @return DailyStatisticDetailByRegDateResponseDto
     * @throws BaseException
     */
    @Override
    public DailyStatisticDetailByRegDateResponseDto getDailyStatisticByRegDate(UUID memberId, Timestamp curDate) throws BaseException {
        // 사용자 정보 체크
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        if (!memberOptional.isPresent()) {
            throw new BaseException(BaseResponseStatus.INVALID_MEMBER);
        }

        checkDailyStatistic(memberId, curDate);

        // 월간 통계 정보 저장
        List<DailyStatistic> dailyStatisticList = dailyStatisticRepository.findAllByMemberIdRegDate(memberId, curDate).get();
        List<DailyStatisticAmountByCategoryResponseDto> amountByCategory = new ArrayList<>();
        Integer totalAmount = 0;

        for (DailyStatistic dailyStatistic : dailyStatisticList) {
            DailyStatisticAmountByCategoryResponseDto newAmountByCategory
                    = DailyStatisticAmountByCategoryResponseDto.builder()
                    .categoryId(dailyStatistic.getCategory().getId())
                    .categoryName(dailyStatistic.getCategory().getName())
                    .amount(dailyStatistic.getAmount())
                    .build();

            amountByCategory.add(newAmountByCategory);

            totalAmount += dailyStatistic.getAmount();
        }

        DailyStatisticDetailByRegDateResponseDto dailyStatistic
                = DailyStatisticDetailByRegDateResponseDto.builder()
                .amountByCategory(amountByCategory)
                .totalAmount(totalAmount)
                .build();

        return dailyStatistic;
    }

    /**
     * 로그인 한 사용자의 아이디 + 특정 날짜에 해당하는 월간 통계 정보 조회
     *
     * @param memberId
     * @param curDate
     * @return MonthlyStatisticDetailByRegDateResponseDto
     * @throws BaseException
     */
    @Override
    public MonthlyStatisticDetailByRegDateResponseDto getMonthlyStatisticByRegDate(UUID memberId, Timestamp curDate) throws BaseException {
        // 사용자 정보 체크
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        if (!memberOptional.isPresent()) {
            throw new BaseException(BaseResponseStatus.INVALID_MEMBER);
        }

        checkMonthlyStatistic(memberId, curDate);

        // 월간 통계 정보 저장
        List<MonthlyStatistic> monthlyStatisticList = monthlyStatisticRepository.findAllByMemberIdRegDate(memberId, curDate).get();
        List<MonthlyStatisticAmountByCategoryResponseDto> amountByCategory = new ArrayList<>();
        Integer totalAmount = 0;

        for (MonthlyStatistic monthlyStatistic : monthlyStatisticList) {
            MonthlyStatisticAmountByCategoryResponseDto newAmountByCategory
                    = MonthlyStatisticAmountByCategoryResponseDto.builder()
                    .categoryId(monthlyStatistic.getCategory().getId())
                    .categoryName(monthlyStatistic.getCategory().getName())
                    .amount(monthlyStatistic.getAmount())
                    .build();

            amountByCategory.add(newAmountByCategory);

            totalAmount += monthlyStatistic.getAmount();
        }

        MonthlyStatisticDetailByRegDateResponseDto monthlyStatistic
                = MonthlyStatisticDetailByRegDateResponseDto.builder()
                .amountByCategory(amountByCategory)
                .totalAmount(totalAmount)
                .build();

        return monthlyStatistic;
    }

    /**
     * 사용자 + 카테고리 + 특정 연월로 이전 3달간의 통계 정보를 가져오는 메서드
     *
     * @param memberId
     * @param curDate
     * @return List<MonthlyStatisticAmountByCategoryResponseDto>
     * @throws BaseException
     */
    @Override
    public List<MonthlyStatisticAmountByCategoryResponseDto> getMonthlyStatisticAvgForThreeMonth(UUID memberId, Timestamp curDate) throws BaseException {
        List<MonthlyStatisticAmountByCategoryResponseDto> amountByCategoryList = new ArrayList<>();
        List<Category> categoryList = categoryRepository.findAll();

        // 사용자 정보 체크
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        if (!memberOptional.isPresent()) {
            throw new BaseException(BaseResponseStatus.INVALID_MEMBER);
        }

        // 월간 통계 정보 체크
        Optional<List<IMonthlyStatisticAvg>> monthlyStatisticAvgListOptional = monthlyStatisticRepository.findByMemberIdRegDateForThreeMonth(memberId, curDate);
        if (!monthlyStatisticAvgListOptional.isPresent()) { // 모든 카테고리에 대해 0으로 반환
            for (Category category : categoryList) {
                MonthlyStatisticAmountByCategoryResponseDto amountByCategory
                        = MonthlyStatisticAmountByCategoryResponseDto.builder()
                        .categoryId(category.getId())
                        .categoryName(category.getName())
                        .amount(0)
                        .build();

                amountByCategoryList.add(amountByCategory);
            }

            return amountByCategoryList;
        }

        // 카테고리 별로 모두 더해서 반환
        List<IMonthlyStatisticAvg> monthlyStatisticAvgList = monthlyStatisticAvgListOptional.get();
        for (IMonthlyStatisticAvg iMonthlyStatisticAvg : monthlyStatisticAvgList) {
            MonthlyStatisticAmountByCategoryResponseDto amountByCategory
                    = MonthlyStatisticAmountByCategoryResponseDto.builder()
                    .categoryId(iMonthlyStatisticAvg.getCategoryId())
                    .categoryName(iMonthlyStatisticAvg.getCategoryName())
                    .amount(iMonthlyStatisticAvg.getAmount())
                    .build();

            amountByCategoryList.add(amountByCategory);
        }

        return amountByCategoryList;
    }
}
