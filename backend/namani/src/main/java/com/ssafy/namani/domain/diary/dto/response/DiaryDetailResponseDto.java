package com.ssafy.namani.domain.diary.dto.response;

import com.ssafy.namani.domain.diary.entity.Diary;
import com.ssafy.namani.domain.goal.entity.GoalByCategory;
import com.ssafy.namani.domain.statistic.entity.DailyStatistic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryDetailResponseDto {

    @NotNull
    private Diary diary;

    @NotNull
    private DailyStatistic dailyStatistic;

    @NotNull
    private Integer goalAmount;

    @NotNull
    private GoalByCategory goalByCategory;
}
