// src/api/modules/oauthApi.js

const BASE_URL = 'https://oboeru.me/oauth2/authorization';

const oauthApi = {
  getGoogleAuthUrl() {
    return `${BASE_URL}/google`;
  },

  getFacebookAuthUrl() {
    return `${BASE_URL}/facebook`;
  },
};

export default oauthApi;
