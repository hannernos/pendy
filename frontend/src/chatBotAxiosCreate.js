//baseUrl 설정을 위한 axiosCreate 함수
import axios from 'axios';

const chatBotAxiosCreate = axios.create({
  //baseURL 설정
  baseURL: 'http://localhost:8000',
  withCredentials: true,
});

export default chatBotAxiosCreate;
