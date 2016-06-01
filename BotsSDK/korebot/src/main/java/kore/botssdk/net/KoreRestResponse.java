package kore.botssdk.net;


import kore.botssdk.models.Authorization;
import kore.botssdk.models.BotUserInfo;
import kore.botssdk.models.KoreUser;

@SuppressWarnings("serial")
public class KoreRestResponse {

    public class KoreLoginResponse extends KoreUser {
        public String status;
    }
    public class JWTTokenResponse{
        private String jwt;

        /**
         * @return
         * The jwt
         */
        public String getJwt() {
            return jwt;
        }
        /**
         * @param jwt
         * The jwt
         */
        public void setJwt(String jwt) {
            this.jwt = jwt;
        }
    }
    public class BotAuthorization {
        private Authorization authorization;
        private BotUserInfo userInfo;

        /**
         * @return
         * The authorization
         */
        public Authorization getAuthorization() {
            return authorization;
        }

        /**
         * @param authorization
         * The authorization
         */
        public void setAuthorization(Authorization authorization) {
            this.authorization = authorization;
        }

        /**
         * @return
         * The userInfo
         */
        public BotUserInfo getUserInfo() {
            return userInfo;
        }

        /**
         * @param userInfo
         * The userInfo
         */
        public void setUserInfo(BotUserInfo userInfo) {
            this.userInfo = userInfo;
        }

    }
    public class RTMUrl {
        private String url;

        /**
         * @return The url
         */
        public String getUrl() {
            return url;
        }

        /**
         * @param url The url
         */
        public void setUrl(String url) {
            this.url = url;
        }
    }


}
