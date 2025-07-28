// src/api/modules/oauthApi.js

const BASE_URL = 'https://oboeru.me/login/oauth2/code';

const oauthApi = {
  getGoogleAuthUrl() {
    return `${BASE_URL}/google`;
  },

  getFacebookAuthUrl() {
    return `${BASE_URL}/facebook`;
  },
};

export default oauthApi;
