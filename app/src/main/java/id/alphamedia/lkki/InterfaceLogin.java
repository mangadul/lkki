package id.alphamedia.lkki;

public interface InterfaceLogin {
    String getParamResponseServer();
    void setParamResponseServer(String param);
    void setLoginStatus(boolean islogin);
    void processLogin(String param);
    boolean getLoginStatus();
}