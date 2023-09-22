import React, { useEffect, useState } from 'react';
import './UserTemplate.css';
import { addMonths, subMonths } from 'date-fns';
import CalenderHeader from '../../components/main/calender-header/CalenderHeader';
import CalenderDays from '../../components/main/calender-days/CalenderDays';
import CalenderCells from '../../components/main/calender-cells/CalenderCells';
import DonutChart from '../../components/common/donut-chart/DonutChart';
import GoalBar from '../../components/common/goal-bar/GoalBar';
import DayMonthButton from '../../components/main/day-month-button/DayMonthButton';
import handleCalender from '../../utils/handleCalender';
import { format } from 'date-fns';

//유저 전용 메인 페이지
const UserTemplate = () => {
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const [responseData, setResponseData] = useState([]);
  const [selectedOption, setSelectedOption] = useState('option1'); // 초기값 설정

  useEffect(() => {
    const fetchData = async () => {
      try {
        // yyyy-MM-ddTHH:mm:ss.SSSZ 형식으로 todayDate 값 생성
        // yyyy-MM-ddTHH:mm:ss.SSS+09:00 형식으로 todayDate 값 생성
        const todayDate = format(
          Date.now(),
          "yyyy-MM-dd'T'HH:mm:ss.SSS'+09:00'",
        );
        // yyyy-MM-01T00:00:00.000+09:00 형식으로 todayMonth 값 생성
        const todayMonth = format(
          currentMonth,
          "yyyy-MM-dd'T'HH:mm:ss.SSS'+09:00'",
        );
        const response = await handleCalender(todayDate, todayMonth);
        console.log('res', response.data);
        console.log('complate load');
        setResponseData(response.data); // response 데이터를 상태로 저장
      } catch (error) {
        console.log(error);
      }
    };

    fetchData();
  }, [currentMonth]);
  console.log('usertemplate ' + currentMonth);

  // 달력을 이전달로 넘기는 기능
  const prevMonth = () => {
    setCurrentMonth(subMonths(currentMonth, 1));
  };

  //달력을 다음 달로 넘기는 기능
  const nextMonth = () => {
    setCurrentMonth(addMonths(currentMonth, 1));
  };

  const currentDate = new Date(); // 현재 날짜 가져오기
  const nextMonthDate = new Date(
    currentDate.getFullYear(),
    currentDate.getMonth() + 1,
    1,
  ); // 다음 달의 첫 날 가져오기
  const lastDayOfCurrentMonth = new Date(nextMonthDate - 1); // 현재 월의 마지막 날 가져오기

  // 현재 월의 총 일 수 계산
  const totalDaysInCurrentMonth = lastDayOfCurrentMonth.getDate();

  console.log('현재 월의 총 일 수:', totalDaysInCurrentMonth);

  // DayMonthButton에서 선택된 옵션을 받아와서 상태 업데이트
  const handleOptionChange = (option) => {
    setSelectedOption(option);
  };

  // Chart arguments by selected option
  let chartTitle =
    selectedOption === 'option1' ? '오늘 총 소비액' : '월간 총 소비액';

  let chartData = []; // Donut 차트에 표시할 데이터 배열
  let chartLabel = []; // Donut 차트에 표시할 데이터 제목

  const showLegend = false; // 범례를 표시할지 여부
  const legendFontSize = '12px'; // 범례의 글꼴 크기
  const showLabels = true; // 라벨 표시 여부
  const labelFontSize = '14px'; // 라벨 글꼴 크기
  const labelColor = '#333'; // 라벨 텍스트 색상
  const showValues = true; // 값 표시 여부
  const valueFontSize = '16px'; // 값 글꼴 크기
  const valueColor = '#555'; // 값 텍스트 색상
  const chartColors = [
    '#FAF2E8',
    '#BDECEA',
    '#DAB8F1',
    'rgba(243, 213, 182, 0.63)',
    'rgba(208, 228, 197, 0.42)',
    'rgba(255, 170, 180, 0.50)',
    '#CFE4C5',
    'rgba(189, 236, 235, 0.53)',
  ]; // 차트의 섹션 색상 배열

  let consumption_goal = 0;
  let consumption_amount = 0;

  if (responseData.data) {
    // 오늘
    let monthlyGoalByToday = responseData.data.thisMonthGoalInfo.goalAmount;
    // 오늘 목표 소비 금액
    let dailyGoal = monthlyGoalByToday / totalDaysInCurrentMonth;

    // 월간
    // 이번달 목표
    let monthlyGoalByCalendar = responseData.data.totalGoal.goalAmount;

    let statisticData = [];
    // 일간 소비 내역
    if (selectedOption === 'option1') {
      statisticData = responseData.data.dailyStatistic.amountByCategory;
      consumption_goal = dailyGoal;
      consumption_amount = responseData.data.dailyStatistic.totalAmount;
    } else {
      // 월간 소비 내역
      statisticData = responseData.data.monthlyStatistic.amountByCategory;
      consumption_goal = monthlyGoalByCalendar;
      consumption_amount = responseData.data.monthlyStatistic.totalAmount;
    }

    chartLabel = statisticData.map((item) => item.categoryName);
    chartData = statisticData.map((item) => item.amount);
  }

  console.log('차트 데이터 : ', chartData);
  console.log('소비 금액 : ', consumption_amount);
  console.log('목표 금액 : ', consumption_goal);

  return (
    <div className="user">
      <div className="calender-container">
        <div className="calender-content">
          <CalenderHeader
            currentMonth={currentMonth}
            prevMonth={prevMonth}
            nextMonth={nextMonth}
          />
          <CalenderDays />
          <CalenderCells currentMonth={currentMonth} />
        </div>
      </div>
      <div className="chart-container">
        <div className="chat-flex-content">
          <DayMonthButton onOptionChange={handleOptionChange} />
          <div className="chart-content">
            {responseData.data && (
              <DonutChart
                series={chartData}
                chartLabel={chartLabel}
                title={chartTitle}
                legendShow={showLegend}
                legendFont={legendFontSize}
                labelShow={showLabels}
                labelFont={labelFontSize}
                labelColor={labelColor}
                valueShow={showValues}
                valueFont={valueFontSize}
                valueColor={valueColor}
                colors={chartColors}
              />
            )}
          </div>
        </div>
        <div className="bar-content">
          {consumption_goal !== 0 && (
            <div className="spend">
              <div className="spend-text-blue">현재 소비 금액 /</div>&nbsp;
              <div className="spend-text-black">목표 소비 금액</div>
            </div>
          )}
          {consumption_goal !== 0 && (
            <GoalBar
              color={'#2A4FFA'}
              current={consumption_amount}
              goal={consumption_goal}
            />
          )}
          <br />

          {selectedOption === 'option1' && (
            <div className="spend-text-black">오늘의 고정 지출</div>
          )}
        </div>
      </div>
    </div>
  );
};

export default UserTemplate;
