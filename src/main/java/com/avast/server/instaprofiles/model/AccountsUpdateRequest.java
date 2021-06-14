package com.avast.server.instaprofiles.model;

/**
 * @author Vitasek L.
 */
public class AccountsUpdateRequest {
    private String accounts;
    private String aes256;

    public AccountsUpdateRequest() {
    }

    public AccountsUpdateRequest(String accounts, String aes256) {
        this.accounts = accounts;
        this.aes256 = aes256;
    }

    public String getAccounts() {
        return accounts;
    }

    public void setAccounts(String accounts) {
        this.accounts = accounts;
    }

    public String getAes256() {
        return aes256;
    }

    public void setAes256(String aes256) {
        this.aes256 = aes256;
    }
}
