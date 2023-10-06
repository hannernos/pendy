import React, { useState } from 'react';
import './EmotionTemplate.css';
import useTodayList from '../../hooks/useTodayList';
import handleEmotionRegist from '../../utils/handleEmotionRegist';
import { useNavigate } from 'react-router-dom';
import LoadingSpinner from '../../components/common/loading-spinner/LoadingSpinner';

const EmotionTemplate = () => {
  const regDate = new Date();
  regDate.setHours(0, 0, 0, 0);
  const { todayList, todayLoading } = useTodayList(regDate);
  const [loading, setLoading] = useState(false);
  const [selectedOptions, setSelectedOptions] = useState([]);
  const [emotionList, setEmotionList] = useState([]);
  const navigate = useNavigate();
  const emoji = [1, 2, 3, 4, 5];

  console.log('todayList', todayList);

  if (todayLoading) {
    return <div>Loading...</div>;
  }

  if (loading) {
    return (
      <div className="loading-spinner-container">
        <div className="loading-spinner">
          <LoadingSpinner />
        </div>
        <div className="loading-text">나마니가 일기를 생성 중입니다.</div>
      </div>
    );
  }

  if (todayList.data.length === 0) {
    alert('금일 소비내역이 없습니다.');
    navigate('/');
  }
  // emotionList에 데이터 추가
  const addEmotionData = (id, emotionId) => {
    const existingIndex = emotionList.findIndex(
      (item) => item.transactionId === id,
    );
    if (existingIndex !== -1) {
      const updatedEmotionList = [...emotionList];
      updatedEmotionList[existingIndex] = {
        transactionId: id,
        emotionId: emotionId,
      };
      setEmotionList(updatedEmotionList);
    } else {
      const newEmotionData = {
        transactionId: id,
        emotionId: emotionId,
      };
      setEmotionList([...emotionList, newEmotionData]);
    }
  };

  // 각 옵션 변경
  const handleRadioChange = (index, option) => {
    const newSelectedOptions = [...selectedOptions];
    newSelectedOptions[index] = option;
    setSelectedOptions(newSelectedOptions);

    const transactionId = todayList.data[index].id;
    addEmotionData(transactionId, option);
  };

  // 감정 등록 및 일기 생성
  const onEmotionRegistClick = async (event) => {
    event.preventDefault();
    setLoading(true);
    console.log(emotionList);
    if (todayList.data.length === emotionList.length) {
      try {
        const response = await handleEmotionRegist(emotionList);
        console.log(response);
        if (response.data.code === 1000) {
          setLoading(false);
          console.log('emotion regist success');
          navigate('/');
        } else {
          setLoading(false);
          console.error(response.data.code + ' ' + response.data.message);
          alert('등록에 실패하셨습니다');
        }
      } catch (error) {
        setLoading(false);
        console.error('emotion regist failed');
        alert('등록에 실패하셨습니다');
      }
    } else {
      setLoading(false);
      alert('아직 평가하지 않은 항목이 있습니다.');
    }
  };

  return (
    <div className="emotion-template">
      <div className="emotion-container">
        <div className="emotion-title">
          <h1 className="emotion-main-title">소비 자가진단</h1>
          <p className="emotion-sub-title">
            오늘 소비한 내역에 대한 내 감정을 표현해주세요!
          </p>
        </div>
        <div className="emotion-content">
          <div className="emotion-option">
            <div className="emotion-option-left">
              {todayList.data.map(
                (list, index) =>
                  index % 2 === 0 && (
                    <div key={index} className="emotion-list">
                      <div className="emotion-content-container">
                        <div className="emotion-text">{list.name}</div>
                        <div className="emotion-text">
                          {list.transactionAmount}원
                        </div>
                      </div>
                      <div className="emotion-option-button-container">
                        {emoji.map((num) => (
                          <div key={num} className="emotion-option-button">
                            <input
                              type="radio"
                              name={`chart-option-${index}`}
                              value={num}
                              checked={selectedOptions[index] === num}
                              onChange={() => handleRadioChange(index, num)}
                            />
                          </div>
                        ))}
                      </div>
                      <div className="emotion-choose">
                        <img
                          className="face-emoji"
                          src="/emoji-img/emoji-terrible.png"
                          alt="매우나쁨"
                        />
                        <img
                          className="face-emoji"
                          src="/emoji-img/emoji-bad.png"
                          alt="나쁨"
                        />
                        <img
                          className="face-emoji"
                          src="/emoji-img/emoji-soso.png"
                          alt="보통"
                        />
                        <img
                          className="face-emoji"
                          src="/emoji-img/emoji-good.png"
                          alt="좋음"
                        />
                        <img
                          className="face-emoji"
                          src="/emoji-img/emoji-amazing.png"
                          alt="매우좋음"
                        />
                      </div>
                    </div>
                  ),
              )}
            </div>
            <div className="emotion-option-right">
              {todayList.data.map(
                (list, index) =>
                  index % 2 !== 0 && (
                    <div key={index} className="emotion-list">
                      <div className="emotion-content-container">
                        <div className="emotion-text">{list.name}</div>
                        <div className="emotion-text">
                          {list.transactionAmount}원
                        </div>
                      </div>
                      <div className="emotion-option-button-container">
                        {emoji.map((num) => (
                          <div className="emotion-option-button">
                            <input
                              type="radio"
                              name={`chart-option-${index}`}
                              value={num.num}
                              checked={selectedOptions[index] === num}
                              onChange={() => handleRadioChange(index, num)}
                            />
                          </div>
                        ))}
                      </div>
                      <div className="emotion-choose">
                        <img
                          className="face-emoji"
                          src="/emoji-img/emoji-terrible.png"
                          alt="매우나쁨"
                        />
                        <img
                          className="face-emoji"
                          src="/emoji-img/emoji-bad.png"
                          alt="나쁨"
                        />
                        <img
                          className="face-emoji"
                          src="/emoji-img/emoji-soso.png"
                          alt="보통"
                        />
                        <img
                          className="face-emoji"
                          src="/emoji-img/emoji-good.png"
                          alt="좋음"
                        />
                        <img
                          className="face-emoji"
                          src="/emoji-img/emoji-amazing.png"
                          alt="매우좋음"
                        />
                      </div>
                    </div>
                  ),
              )}
            </div>
          </div>
        </div>
        <div className="emotion-button-container">
          <button className="emotion-button" onClick={onEmotionRegistClick}>
            일기 작성
          </button>
        </div>
      </div>
    </div>
  );
};

export default EmotionTemplate;
