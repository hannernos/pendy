package com.ssafy.namani.domain.emotion.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;

@Entity
@Getter
public class Emotion {
	@Id
	private Integer emotionScore; // 감정 점수
	private String emotionName; // 감정명
}
