package com.ssafy.namani.domain.diary.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.namani.domain.diary.dto.request.*;
import com.ssafy.namani.domain.diary.dto.response.DiaryDetailResponseDto;
import com.ssafy.namani.domain.diary.dto.response.DiaryListResponseDto;
import com.ssafy.namani.domain.diary.dto.response.DiaryMonthlyAnalysisResponseDto;
import com.ssafy.namani.domain.diary.service.DiaryService;
import com.ssafy.namani.domain.jwt.service.JwtService;
import com.ssafy.namani.global.response.BaseException;
import com.ssafy.namani.global.response.BaseResponse;
import com.ssafy.namani.global.response.BaseResponseService;
import com.ssafy.namani.global.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diaries")
public class DiaryController {

    private final DiaryService diaryService;
    private final JwtService jwtService;
    private final BaseResponseService baseResponseService;

    /**
     * 해당 월의 달력 정보를 불러오는 API
     *
     * @param token
     * @param diaryListRequestDto
     * @return BaseResponse<Object> - DiaryListResponseDto
     */
    @PostMapping("/calendar")
    public BaseResponse<Object> getCalendar(@RequestHeader(value = "accessToken", required = false) String token,
                                            @RequestBody DiaryListRequestDto diaryListRequestDto) {
        try {
            // 토큰 정보 체크
            if (token == null || token.equals("")) {
                throw new BaseException(BaseResponseStatus.SESSION_EXPIRATION);
            }

            UUID memberId = jwtService.getMemberIdFromToken(token); // token으로 memberId 조회
            DiaryListResponseDto diaryListResponseDto = diaryService.getCalendar(memberId, diaryListRequestDto);

            return baseResponseService.getSuccessResponse(diaryListResponseDto);
        } catch (BaseException e) {
            return baseResponseService.getFailureResponse(e.status);
        }
    }

    /**
     * 감정 등록 -> 일기 생성 -> 일기 등록을 하는 API
     *
     * @param token
     * @param diaryRegistRequestDtoList
     * @return BaseResponse<Object>
     */
    @PostMapping("/regist")
    public BaseResponse<Object> registDiary(@RequestHeader(value = "accessToken", required = false) String token,
                                            @RequestBody List<DiaryRegistRequestDto> diaryRegistRequestDtoList) {
        try {
            // 토큰 정보 체크
            if (token == null || token.equals("")) {
                throw new BaseException(BaseResponseStatus.SESSION_EXPIRATION);
            }

            UUID memberId = jwtService.getMemberIdFromToken(token); // token으로 memberId 조회
            diaryService.registDiary(memberId, diaryRegistRequestDtoList);

            return baseResponseService.getSuccessNoDataResponse();
        } catch (BaseException e) {
            return baseResponseService.getFailureResponse(e.status);
        } catch (JsonProcessingException e) {
            return baseResponseService.getFailureResponse(BaseResponseStatus.DIARY_JSON_PARSING_ERROR);
        }
    }

    /**
     * 일기를 조회하는 API
     *
     * @param accessToken
     * @param diaryDetailRequestDto
     * @return DiaryDetailResponseDto
     */
    @PostMapping("/after")
    public BaseResponse<Object> detailDiary(@RequestHeader(value = "accessToken") String accessToken,
                                            @RequestBody DiaryDetailRequestDto diaryDetailRequestDto) {
        try {
            DiaryDetailResponseDto diaryDetailResponseDto = diaryService.detailDiary(accessToken, diaryDetailRequestDto);
            return baseResponseService.getSuccessResponse(diaryDetailResponseDto);
        } catch (BaseException e) {
            return baseResponseService.getFailureResponse(e.status);
        }
    }

    /**
     * 일기의 내용을 수정하는 API
     *
     * @param id
     * @param diaryUpdateContentRequestDto
     * @return BaseResponse<Object>
     */
    @PutMapping("/{id}")
    public BaseResponse<Object> updateDiary(@PathVariable("id") Long id,
                                            @RequestBody DiaryUpdateContentRequestDto diaryUpdateContentRequestDto) {
        try {
            diaryService.updateDiary(id, diaryUpdateContentRequestDto);
            return baseResponseService.getSuccessNoDataResponse();
        } catch (BaseException e) {
            return baseResponseService.getFailureResponse(e.status);
        }
    }

    /**
     * 해당 월의 월간 분석 정보를 불러오는 API
     *
     * @param accessToken
     * @param diaryMonthlyAnalysisRequestDto
     * @return DiaryMonthlyAnalysisResponseDto
     */
    @PostMapping("/monthly-analysis")
    public BaseResponse<Object> getMonthlyAnalysis(
            @RequestHeader(value = "accessToken") String accessToken,
            @RequestBody DiaryMonthlyAnalysisRequestDto diaryMonthlyAnalysisRequestDto) {
        UUID memberId = jwtService.getMemberIdFromToken(accessToken);
        try {
            DiaryMonthlyAnalysisResponseDto diaryMonthlyAnalysisResponseDto = diaryService.getMonthlyAnalysis(memberId,
                    diaryMonthlyAnalysisRequestDto);
            return baseResponseService.getSuccessResponse(diaryMonthlyAnalysisResponseDto);
        } catch (BaseException e) {
            return baseResponseService.getFailureResponse(e.status);
        }
    }
}
