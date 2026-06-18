const API_HOST = typeof window !== 'undefined'
    ? window.location.hostname
    : 'localhost';

export const API_BASE_URL = `http://${API_HOST}:8080/api`;
