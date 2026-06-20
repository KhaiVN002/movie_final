const API_HOST = typeof window !== 'undefined'
    ? (window.location.hostname === 'localhost' ? '127.0.0.1' : window.location.hostname)
    : 'backend';

export const API_BASE_URL = `http://${API_HOST}:8080/api`;

