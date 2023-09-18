package com.ssafy.namani.domain.diary.service;

import com.ssafy.namani.domain.accountInfo.entity.AccountInfo;
import com.ssafy.namani.domain.accountInfo.repository.AccountInfoRepository;
import com.ssafy.namani.domain.diary.dto.response.DiaryCalendarResponseDto;
import com.ssafy.namani.domain.diary.dto.request.*;
import com.ssafy.namani.domain.diary.dto.response.DiaryDetailResponseDto;
import com.ssafy.namani.domain.diary.dto.response.DiaryListResponseDto;
import com.ssafy.namani.domain.diary.dto.response.DiaryMonthlyAnalysisResponseDto;
import com.ssafy.namani.domain.diary.entity.Diary;
import com.ssafy.namani.domain.diary.repository.DiaryRepository;
import com.ssafy.namani.domain.emotion.entity.Emotion;
import com.ssafy.namani.domain.emotion.repository.EmotionRepository;
import com.ssafy.namani.domain.goal.dto.response.GoalByCategoryDetailResponseDto;
import com.ssafy.namani.domain.goal.dto.response.TotalGoalDetailResponseDto;
import com.ssafy.namani.domain.goal.entity.GoalByCategory;
import com.ssafy.namani.domain.goal.entity.TotalGoal;
import com.ssafy.namani.domain.goal.repository.GoalByCategoryRepository;
import com.ssafy.namani.domain.goal.repository.TotalGoalRepository;
import com.ssafy.namani.domain.goal.service.GoalService;
import com.ssafy.namani.domain.jwt.service.JwtService;
import com.ssafy.namani.domain.member.entity.Member;
import com.ssafy.namani.domain.member.repository.MemberRepository;
import com.ssafy.namani.domain.statistic.dto.response.DailyStatisticDetailByRegDateResponseDto;
import com.ssafy.namani.domain.statistic.dto.response.MonthlyStatisticDetailByRegDateResponseDto;
import com.ssafy.namani.domain.statistic.entity.DailyStatistic;
import com.ssafy.namani.domain.statistic.entity.MonthlyStatistic;
import com.ssafy.namani.domain.statistic.repository.DailyStatisticRepository;
import com.ssafy.namani.domain.statistic.repository.MonthlyStatisticRepository;
import com.ssafy.namani.domain.statistic.service.StatisticService;
import com.ssafy.namani.domain.transactionInfo.entity.TransactionInfo;
import com.ssafy.namani.domain.transactionInfo.repository.TransactionInfoRepository;
import com.ssafy.namani.global.response.BaseException;
import com.ssafy.namani.global.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

	private final JwtService jwtService;
	private final MemberRepository memberRepository;
	private final DiaryRepository diaryRepository;
	private final EmotionRepository emotionRepository;
	private final TransactionInfoRepository transactionInfoRepository;
	private final AccountInfoRepository accountInfoRepository;
	private final TotalGoalRepository totalGoalRepository;
	private final GoalByCategoryRepository goalByCategoryRepository;
	private final DailyStatisticRepository dailyStatisticRepository;
	private final MonthlyStatisticRepository monthlyStatisticRepository;

	private final GoalService goalService;
	private final StatisticService statisticService;

	/**
	 * 달력 조회
	 *
	 * @param memberId
	 * @param diaryListRequestDto
	 * @return DiaryListResponseDto
	 * @throws BaseException
	 */
	@Override
	public DiaryListResponseDto getCalendar(UUID memberId, DiaryListRequestDto diaryListRequestDto) throws BaseException {
		// 사용자 정보 체크
		Optional<Member> memberOptional = memberRepository.findById(memberId);
		if (!memberOptional.isPresent()) {
			throw new BaseException(BaseResponseStatus.NOT_FOUND_MEMBER);
		}

		Timestamp todayDate = diaryListRequestDto.getTodayDate(); // 현재 일자 (now)
		Timestamp todayMonth = diaryListRequestDto.getTodayMonth(); // 현재 연월 (화면에 표기되는 연월)

		/* 현재 연월로 일기 목록 가져오기 */
		List<Diary> diaryList = diaryRepository.findAllByMemberIdTodayMonth(memberId, todayMonth).get();
		List<DiaryCalendarResponseDto> diaryCalendarList = new ArrayList<>();
		for (Diary diary : diaryList) {
			DiaryCalendarResponseDto diaryCalendarResponseDto =
				DiaryCalendarResponseDto.builder()
					.id(diary.getId())
					.stampType(diary.getStampType())
					.regDate(diary.getRegDate())
					.build();

			diaryCalendarList.add(diaryCalendarResponseDto);
		}

		/* 월간 목표 조회 */
		TotalGoalDetailResponseDto totalGoal = goalService.getTotalGoal(memberId, todayMonth);

		/* 카테고리 별 목표 조회 */
		List<GoalByCategoryDetailResponseDto> goalByCategoryList = goalService.getGoalByCategoryList(totalGoal.getId());

		/* 특정 일에 해당하는 DailyStatistic 조회 */
		DailyStatisticDetailByRegDateResponseDto dailyStatistic = statisticService.getDailyStatisticByRegDate(memberId, todayDate);

		/* 특정 월에 해당하는 MonthlyStatistic 조회 */
		MonthlyStatisticDetailByRegDateResponseDto monthlyStatistic = statisticService.getMonthlyStatisticByRegDate(memberId, todayMonth);

		// 마지막으로 일기가 작성된 시각 ~ 현재 시각 사이 거래 내역이 있으면 true, 없으면 false
		// 일기 있는지 체크
		boolean newDailyTransaction = false;
		Optional<Diary> diaryOptional = diaryRepository.findByMemberIdTodayDate(memberId, todayDate);
		if (diaryOptional.isPresent()) { // 일기가 생성되어 있는 경우
			Diary diary = diaryOptional.get();
			Timestamp regDate = diary.getRegDate();

			// 계좌 내에서 시각 사이에 거래 내역 존재 체크
			List<AccountInfo> accountInfoList = accountInfoRepository.findByMember_Id(memberId);
			for (AccountInfo accountInfo : accountInfoList) {
				Optional<List<TransactionInfo>> transactionInfoListOptional = transactionInfoRepository.findAllWithdrawalsByAccountNumber(accountInfo.getAccountNumber(), 2, regDate, todayDate);
				if (transactionInfoListOptional.isPresent()) {
					newDailyTransaction = true;
					break;
				}
			}
		}

		/* 일기 목록 전달 */
		DiaryListResponseDto diaryListResponseDto = DiaryListResponseDto.builder()
			.diaryList(diaryCalendarList)
			.totalGoal(totalGoal)
			.goalByCategoryList(goalByCategoryList)
			.monthlyStatistic(monthlyStatistic)
			.dailyStatistic(dailyStatistic)
			.newDailyTransaction(newDailyTransaction)
			.build();

		return diaryListResponseDto;
	}

	/**
	 * 일기 생성
	 *
	 * @param memberId
	 * @param diaryRegistRequestDtoList
	 * @throws BaseException
	 */
	@Override
	public void registDiary(UUID memberId, List<DiaryRegistRequestDto> diaryRegistRequestDtoList) throws BaseException {
		// 사용자 정보 체크
		Optional<Member> memberOptional = memberRepository.findById(memberId);
		if (!memberOptional.isPresent()) {
			throw new BaseException(BaseResponseStatus.NOT_FOUND_MEMBER);
		}

		Member member = memberOptional.get();

		// 감정 등록
		List<TransactionInfo> transactionInfoList = new ArrayList<>();
		for (DiaryRegistRequestDto diaryRegistRequestDto : diaryRegistRequestDtoList) {
			Integer emotionId = diaryRegistRequestDto.getEmotionId();
			Long transactionId = diaryRegistRequestDto.getTransactionId();

			// 거래 내역 존재 체크
			Optional<TransactionInfo> transactionInfoOptional = transactionInfoRepository.findById(transactionId);
			if (!transactionInfoOptional.isPresent()) {
				throw new BaseException(BaseResponseStatus.TRANSACTION_INFO_NOT_FOUND);
			}

			TransactionInfo transactionInfo = transactionInfoOptional.get();

			// 사용자가 가진 계좌에 거래 내역이 있는지 체크
			AccountInfo accountInfo = transactionInfo.getAccountInfo(); // 계좌 정보
			if (!accountInfo.getMember().getId().equals(memberId)) {
				throw new BaseException(BaseResponseStatus.TRANSACTION_INFO_NOT_FOUND_IN_MEMBERS_ACCOUNT);
			}

			Emotion emotion = emotionRepository.findById(emotionId).get(); // 감정 조회

			// 거래 내역에 감정 등록
			TransactionInfo newTransactionInfo = transactionInfo.toBuilder()
				.id(transactionId)
				.emotion(emotion)
				.build();

			transactionInfoList.add(newTransactionInfo);
		}

		transactionInfoRepository.saveAll(transactionInfoList); // 오류가 없으면 거래 내역 전체 저장

		/* -------------- AI에게 일기 생성 요청 -------------- */


		/* -------------- 생성된 일기 데이터 반환 -------------- */


        /* 일기 저장
            Diary diary = Diary.builder()
                    .member(member)
                    .content()
                    .comment()
                    .stampType()
                    .build();

            diaryRepository.save(diary);
         */
	}

	@Override
	public DiaryDetailResponseDto detailDiary(String accessToken, DiaryDetailRequestDto diaryDetailRequestDto) throws BaseException {
		UUID memberId = jwtService.getMemberIdFromToken(accessToken);
		Long diaryId = diaryDetailRequestDto.getId();
		Timestamp regDate = diaryDetailRequestDto.getRegDate();

		Optional<Diary> optionalDiary = diaryRepository.findById(diaryId);
		if (!optionalDiary.isPresent()) {
			throw new BaseException(BaseResponseStatus.DIARY_NOT_FOUND);
		}

		Diary diary = optionalDiary.get();

		/* 월간 목표 조회 */
		TotalGoalDetailResponseDto totalGoal = goalService.getTotalGoal(memberId, regDate);
		Integer goalAmount = totalGoal.getGoalAmount();

		/* 카테고리 별 목표 조회 */
		List<GoalByCategoryDetailResponseDto> goalByCategoryList = goalService.getGoalByCategoryList(totalGoal.getId());

		/* 특정 일에 해당하는 DailyStatistic 조회 */
		DailyStatisticDetailByRegDateResponseDto dailyStatistic = statisticService.getDailyStatisticByRegDate(memberId, regDate);

		return DiaryDetailResponseDto.builder()
			.diary(diary)
			.dailyStatistic(dailyStatistic)
			.goalAmount(goalAmount)
			.goalByCategory(goalByCategoryList)
			.build();
	}

	@Override
	public void updateDiary(Long id, DiaryUpdateContentRequestDto diaryUpdateContentRequestDto) throws BaseException {
		String content = diaryUpdateContentRequestDto.getContent();

		Optional<Diary> optionalDiary = diaryRepository.findById(id);

		if (!optionalDiary.isPresent()) {
			throw new BaseException(BaseResponseStatus.DIARY_NOT_FOUND);
		}
		Diary diary = optionalDiary.get();
		Diary diary1 = diary.toBuilder().
			content(content).
			build();


		diaryRepository.save(diary1);
	}

	@Override
	public DiaryMonthlyAnalysisResponseDto getMonthlyAnalysis(UUID memberId,
		DiaryMonthlyAnalysisRequestDto diaryMonthlyAnalysisRequestDto) throws BaseException {
		// accessToken decode => UUID
		Timestamp curMonth = diaryMonthlyAnalysisRequestDto.getCurMonth();
		log.debug("현재 연월 : " + curMonth);

		// TotalGoal 조회 => UUID와 curMonth로 조회
		Optional<TotalGoal> totalGoal = totalGoalRepository.findByCurDate(memberId, curMonth);
		// 목표 없음 ERROR
		if (totalGoal.isEmpty()) {
			throw new BaseException(BaseResponseStatus.TOTAL_GOAL_NOT_FOUND);
		}

		TotalGoalDetailResponseDto totalGoalDetailResponseDto = TotalGoalDetailResponseDto.builder()
			.id(totalGoal.get().getId())
			.goalAmount(totalGoal.get().getGoalAmount())
			.aiAnlaysis(totalGoal.get().getAiAnalysis())
			.build();
		// GoalByCategory 조회 => totalGoalId로 조회
		// 목표 없음 ERROR
		List<GoalByCategory> goalByCategories = new ArrayList<>();
		Long totalGoalId = totalGoal.get().getId();
		for (int i = 1; i <= 8; i++) {
			Optional<GoalByCategory> goalByCategory = goalByCategoryRepository.findByTotalGoalIdCategoryId(
				totalGoalId, i);
			goalByCategory.ifPresent(goalByCategories::add);
		}

		// MonthlyStatistic 조회 => UUID와 curMonth로 조회
		// 월간 통계 없음 ERROR
		Optional<List<MonthlyStatistic>> allByMemberIdRegDate = monthlyStatisticRepository.findAllByMemberIdRegDate(
			memberId, curMonth);
		if (allByMemberIdRegDate.isEmpty()) {
			throw new BaseException(BaseResponseStatus.MONTHLY_STATISTIC_NOT_FOUND);
		}
		// Query 생성 : select date_sub(curMonth, interval 1 month);
		// 이전 달 목표 체크
		// totalGoalRepository로 이전 달 목표 있는지 체크
		String beforeMonth = totalGoalRepository.findBeforeMonth(memberId, curMonth) + "-01 00:00:00.0";
		log.debug("이전 달 정보:" + beforeMonth);
		Timestamp beforeMonthTimestamp = Timestamp.valueOf(beforeMonth);
		log.debug("이전 달 정보:" + beforeMonthTimestamp);

		Boolean hasBeforeMonthlyGoal = true;
		Optional<TotalGoal> beforeMonthGoal = totalGoalRepository.findByCurDate(memberId, beforeMonthTimestamp);
		// 목표 없음 ERROR
		if (beforeMonthGoal.isEmpty()) {
			hasBeforeMonthlyGoal = false;
		}

		// Query 생성 : select date_add(curMonth, interval 1 month);
		// 다음 달 목표 체크
		// totalGoalRepository로 다음 달 목표 있는지 체크
		String afterMonth = totalGoalRepository.findAfterMonth(memberId, curMonth) + "-01 00:00:00.0";
		Timestamp afterMonthTimestamp = Timestamp.valueOf(afterMonth);
		log.debug("다음 달 정보:" + afterMonth);
		log.debug("다음 달 정보:" + afterMonthTimestamp);

		Boolean hasAfterMonthlyGoal = true;
		Optional<TotalGoal> afterMonthGoal = totalGoalRepository.findByCurDate(memberId, afterMonthTimestamp);
		// 목표 없음 ERROR
		if (afterMonthGoal.isEmpty()) {
			hasAfterMonthlyGoal = false;
		}

		return new DiaryMonthlyAnalysisResponseDto(
			totalGoalDetailResponseDto, goalByCategories,
			allByMemberIdRegDate.get(), hasBeforeMonthlyGoal,
			hasAfterMonthlyGoal); // totalGoal, goalByCategory, DailyStatistic, boolean, boolean
	}
}
