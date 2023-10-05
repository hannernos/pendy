import React from 'react';
import './CalenderCells.css';

import { format } from 'date-fns';
import { startOfMonth, endOfMonth, startOfWeek, endOfWeek } from 'date-fns';
import { isSameMonth, isSameDay, addDays } from 'date-fns';
import { useNavigate } from 'react-router-dom';

// 캘린더 cell
const CalenderCells = ({ currentMonth, diaries, isNewSpend }) => {
  const todayDate = new Date();
  const monthStart = startOfMonth(currentMonth);
  const monthEnd = endOfMonth(monthStart);
  const startDate = startOfWeek(monthStart);
  const endDate = endOfWeek(monthEnd);
  const isNew = isNewSpend;
  const navigate = useNavigate();

  const rows = [];
  let days = [];
  let day = startDate;
  let formattedDate = '';

  //추가 소비 내역이 있을 경우 점 표시
  const getDotStyle = (day) => {
    return isNew && isSameDay(day, todayDate);
  };

  //일기가 있을 경우 스탬프 가져오기
  const getStampType = (day) => {
    const hasDiary = diaries.find((diary) =>
      isSameDay(new Date(diary.regDate), day),
    );
    return hasDiary ? hasDiary.stampType : null;
  };

  // 해당 날짜 일기로 이동
  const onDiaryClick = (day) => {
    const hasDiary = diaries.find((diary) =>
      isSameDay(new Date(diary.regDate), day),
    );
    if (hasDiary) {
      navigate(`diary/${hasDiary.id}/${hasDiary.regDate}`);
    } else if (isSameDay(new Date(), day)) {
      navigate('/emotion');
    } else {
      alert('해당 일에 일기가 존재하지 않습니다.');
    }
  };

  while (day <= endDate) {
    for (let i = 0; i < 7; i++) {
      formattedDate = format(day, 'd');
      const cloneDay = day;
      days.push(
        <div
          className={`col cell ${
            !isSameMonth(day, monthStart)
              ? 'disabled'
              : isSameDay(day, todayDate)
              ? 'selected'
              : format(currentMonth, 'M') !== format(day, 'M')
              ? 'not-valid'
              : 'valid'
          }`}
          key={day}
          onClick={() => onDiaryClick(cloneDay)}
          style={{
            backgroundImage:
              getStampType(day) !== null
                ? `url(/stamp${getStampType(day)}.png)`
                : 'none',
          }}
        >
          <span
            className={
              format(currentMonth, 'M') !== format(day, 'M')
                ? 'text not-valid'
                : ''
            }
          >
            {formattedDate}
          </span>
          {getDotStyle(day) && (
            <div className="calender-dot-container">
              <div className="calender-dot"></div>
            </div>
          )}
        </div>,
      );
      day = addDays(day, 1);
    }
    rows.push(
      <div className="calender-row" key={day}>
        {days}
      </div>,
    );
    days = [];
  }
  return <div className="calender-body">{rows}</div>;
};

export default CalenderCells;
