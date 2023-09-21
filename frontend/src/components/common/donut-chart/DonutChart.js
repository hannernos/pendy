import React, { useState } from 'react';
// , useEffect
import './DonutChart.css';

import ReactApexChart from 'react-apexcharts';

const DonutChart = ({
  series,
  title,
  legendShow,
  legendFont,
  labelShow,
  labelFont,
  labelColor,
  valueFont,
  valueShow,
  valueColor,
  colors,
}) => {
  //eslint-disable-next-line
  const [chartData, setChartData] = useState({
    series: series,
  });

  // console.log(label[0]);

  const options = {
    chart: {
      width: '100%',
    },
    chartOptions: {},
    legend: {
      position: 'bottom',
      show: legendShow,
      fontSize: legendFont,
    },
    plotOptions: {
      pie: {
        donut: {
          labels: {
            show: labelShow,
            total: {
              showAlways: true,
              show: true,
              label: `${title}`,
              fontSize: labelFont,
              color: labelColor,
            },
            value: {
              fontSize: valueFont,
              show: valueShow,
              color: valueColor,
            },
          },
          size: 60,
        },
      },
    },
    labels: [],
    colors: colors,
  };

  return (
    <ReactApexChart options={options} series={chartData.series} type="donut" />
  );
};

export default DonutChart;
