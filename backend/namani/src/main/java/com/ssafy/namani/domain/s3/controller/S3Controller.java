package com.ssafy.namani.domain.s3.controller;

import com.ssafy.namani.domain.clovaOCR.dto.response.ClovaOCRResponseDto;
import com.ssafy.namani.domain.clovaOCR.service.ClovaOCRService;
import com.ssafy.namani.domain.s3.dto.response.S3ResponseDto;
import com.ssafy.namani.domain.s3.service.S3Service;
import com.ssafy.namani.domain.transactionInfo.service.TransactionInfoService;
import com.ssafy.namani.global.response.BaseException;
import com.ssafy.namani.global.response.BaseResponse;
import com.ssafy.namani.global.response.BaseResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class S3Controller {

    private final S3Service s3Service;
    private final ClovaOCRService clovaOCRService;
    private final BaseResponseService baseResponseService;
    private final TransactionInfoService transactionInfoService;
    
    // Object 바꿔야함
    /**
     * S3 서버에 영수증 저장 -> 사진 Access URL로 Clova OCR 요청 -> 응답 받은 뒤 S3에 해당 사진 삭제 ->
     * 데이터 정제해서 DB로 슛 -> 영수증 OCR 데이터 Front로 응답 보낼지 고민중
     */

    /**
     * @param file
     * @return BaseResponse<Object>
     */
    @PostMapping("/send")
    public BaseResponse<Object> saveAndInferenceImage(@RequestHeader(value="accessToken",required=false) String token,
                                                      @RequestParam("file") MultipartFile file) {

        S3ResponseDto s3ResponseDto;
        ClovaOCRResponseDto clovaResponseDto;
        // S3에 이미지 저장
        try {
            s3ResponseDto = s3Service.saveImage(file);

        } catch (BaseException e) {
            return baseResponseService.getFailureResponse(e.status);
        }


        // Clova OCR에 이미지 텍스트 추출 요청
        try {
            // String 에서 ResponseDto 로 변경해야함, ClovaOCRServiceImpl에서 StringBuilder로 return 하는거 생각해야함
            clovaResponseDto = clovaOCRService.execute(s3ResponseDto);

        } catch (BaseException e) {
            return baseResponseService.getFailureResponse(e.status);
        }
        
        // 영수증 텍스트 추출 데이터 정제해서 DB에 저장
        try {
            transactionInfoService.addReceiptTransaction(token, clovaResponseDto);
        } catch (BaseException e) {
            return baseResponseService.getFailureResponse(e.status);
        }

        // 영수증 텍스트 추출 데이터 Frontend로 Response보낼지 고민
        
        return null;
    }

}
