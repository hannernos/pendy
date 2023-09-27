import React, { useState } from 'react';
import './SettingTemplate.css';
import handleModify from '../../utils/handleModify';
import { useNavigate } from 'react-router-dom';

const SettingTemplate = () => {
  const [state, setState] = useState({
    email: sessionStorage.getItem('email'),
    password: '',
    repassword: '',
    name: sessionStorage.getItem('name'),
    age: sessionStorage.getItem('age'),
    salary: sessionStorage.getItem('salary'),
  });

  const navigate = useNavigate();
  const passwordCheck = state.password === state.repassword;

  const onModifyButtonClick = async (event) => {
    event.preventDefault();
    if (state.email.trim() === '') {
      alert('이메일을 입력해주세요');
      return;
    }
    if (state.password.trim() === '') {
      alert('비밀번호를 입력해주세요');
      return;
    }
    if (state.name.trim() === '') {
      alert('이름을 입력해주세요');
      return;
    }
    if (state.age.trim() === '') {
      alert('나이를 입력해주세요');
      return;
    }
    if (state.salary.trim() === '') {
      alert('연봉을 입력해주세요');
      return;
    }
    if (passwordCheck) {
      try {
        const response = await handleModify(
          state.password,
          state.age,
          state.salary,
        );
        if (response.data.code === 1000) {
          console.log('modify success');
          alert('개인정보 수정에 성공하셨습니다');
          navigate('/', { replace: true });
        } else {
          console.error(response.data.code + ' ' + response.data.message);
          alert('개인정보 수정에 실패하셨습니다');
        }
      } catch (error) {
        console.error('modify failed', error);
        alert('개인정보 수정에 실패하셨습니다');
      }
    } else {
      alert('비밀번호가 일치하지 않습니다');
    }
  };

  return (
    <div className="signup">
      <h1 className="user-setting-title"></h1>

      <div className="setting-container-box">
        {/* 이메일 입력 */}
        <div className="user-setting-input setting-email-input">
          <input
            type="email"
            className="setting-input-box setting-email-box"
            placeholder="이메일"
            variant="outlined"
            value={state.email}
            readOnly
          />
        </div>

        {/* 비밀번호 입력 */}
        <div className="user-setting-input setting-pw-input">
          <input
            type="password"
            className="setting-input-box setting-pw-box"
            placeholder="비밀번호"
            variant="outlined"
            value={state.password}
            onChange={(e) => setState({ ...state, password: e.target.value })}
          />
        </div>

        {/* 비밀번호 확인 */}
        <div className="user-setting-input setting-repw-input">
          <input
            type="password"
            className="setting-input-box setting-repw-box"
            placeholder="비밀번호 확인"
            variant="outlined"
            value={state.repassword}
            onChange={(e) => setState({ ...state, repassword: e.target.value })}
          />
        </div>

        {/* 이름 입력 */}
        <div className="user-setting-input setting-name-input">
          <input
            type="text"
            className="setting-input-box setting-name-box"
            placeholder="이름"
            variant="outlined"
            value={state.name}
            readOnly
          />
        </div>

        {/* 나이 입력 */}
        <div className="user-setting-input">
          <div className="user-setting-input setting-age-input">
            <input
              type="number"
              className="setting-input-box setting-age-box"
              placeholder="나이"
              variant="outlined"
              value={state.age}
              onChange={(e) => setState({ ...state, age: e.target.value })}
            />
          </div>
          {/* 소득 입력 */}
          <div className="user-setting-input setting-salary-input">
            <input
              type="number"
              className="setting-input-box setting-salary-box"
              placeholder="연봉"
              variant="outlined"
              value={state.salary}
              onChange={(e) => setState({ ...state, salary: e.target.value })}
            />
          </div>
        </div>
        <button
          className="setting-button setting-btn"
          onClick={onModifyButtonClick}
        >
          수정하기
        </button>
      </div>
    </div>
  );
};

export default SettingTemplate;
