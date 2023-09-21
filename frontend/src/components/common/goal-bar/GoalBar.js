import React from 'react';
import './GoalBar.css';

const GoalBar = ({ color, current, goal }) => {
  const midpoint = (current / goal) * 100 + '%';
  return (
    <div
      className="goal-bar"
      style={{ '--midpoint': midpoint, '--color': color }}
    >
      <p>{Math.round((current / goal) * 100)}%</p>
    </div>
  );
};

export default GoalBar;
