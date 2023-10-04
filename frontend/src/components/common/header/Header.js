// 헤더
import React from 'react';
import './Header.css';

import { Link, useNavigate, useLocation } from 'react-router-dom';

const Header = ({ isLoggedIn }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const currentMonth = new Date();

  const isActive = (path) => {
    return location.pathname === path;
  };

  //로그아웃 버튼 동작
  const onClickLogout = () => {
    console.log('logout');
    localStorage.removeItem('accessToken');
    sessionStorage.removeItem('email');
    sessionStorage.removeItem('name');
    sessionStorage.removeItem('age');
    sessionStorage.removeItem('salary');
    sessionStorage.removeItem('accountList');
    navigate('/', { replace: true });
  };

  return (
    <div className="header">
      {/* pendy 로고 */}
      <Link to={'/'} className="header-link-content">
        <img src="/logo.png" alt="로고" className="header-logo" />
      </Link>

      <div className="header-menu-container">
        {/* 로그인 여부에 따른 메뉴 */}
        <div className="menu-list">
          {isLoggedIn ? (
            <div className="sub-menu">
              {/* 월간소비분석 페이지 링크 */}
              <div className="sub-menu-container">
                <Link
                  to={{
                    pathname: '/analysis',
                    search: `?currentMonth=${encodeURIComponent(
                      currentMonth.toISOString(),
                    )}`,
                  }}
                  className={`link-content ${
                    isActive('/analysis') ? 'active-link' : ''
                  }`}
                >
                  월간소비분석
                </Link>
              </div>

              {/* 마이페이지 링크 */}
              <div className="sub-menu-container">
                <Link
                  to={'/setting'}
                  className={`link-content ${
                    isActive('/setting') ? 'active-link' : ''
                  }`}
                >
                  마이페이지
                </Link>
              </div>

              {/* 목표 설정 페이지 */}
              <div className="sub-menu-container">
                <Link
                  to={'/goal'}
                  className={`link-content ${
                    isActive('/goal') ? 'active-link' : ''
                  }`}
                >
                  내 목표
                </Link>
              </div>

              {/* 로그아웃 */}
              <div className="sub-menu-container" onClick={onClickLogout}>
                로그아웃
              </div>
            </div>
          ) : (
            <div className="sub-menu">
              {/* 로그인 */}
              <div className="sub-menu-container">
                <Link
                  to={'/login'}
                  className={`link-content ${
                    isActive('/login') ? 'active-link' : ''
                  }`}
                >
                  로그인
                </Link>
              </div>

              {/* 회원가입 */}
              <div className="sub-menu-container">
                <Link
                  to={'/login/signup'}
                  className={`link-content ${
                    isActive('/login/signup') ? 'active-link' : ''
                  }`}
                >
                  회원가입
                </Link>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Header;
