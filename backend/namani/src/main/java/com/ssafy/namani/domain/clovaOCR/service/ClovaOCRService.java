package com.ssafy.namani.domain.clovaOCR.service;

import com.ssafy.namani.domain.clovaOCR.dto.response.ClovaOCRResponseDto;
import com.ssafy.namani.domain.s3.dto.response.S3ResponseDto;
import com.ssafy.namani.global.response.BaseException;

public interface ClovaOCRService {

    ClovaOCRResponseDto execute(S3ResponseDto request) throws BaseException;

}
