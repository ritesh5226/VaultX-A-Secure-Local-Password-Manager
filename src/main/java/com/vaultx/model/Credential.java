package com.vaultx.model;

public class Credential {

    private long credentialId;
    private String title;
    private String username;
    private char[] password;
    private String url;
    private String notes;

    public Credential(
            long credentialId,
            String title,
            String username,
            char[] password,
            String url,
            String notes) {

        this.credentialId = credentialId;
        this.title = title;
        this.username = username;
        this.password = password;
        this.url = url;
        this.notes = notes;
    }

    public Credential(
            String title,
            String username,
            char[] password,
            String url,
            String notes) {

        this(
                0,
                title,
                username,
                password,
                url,
                notes
        );
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public long getCredentialId() {
        return credentialId;
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public char[] getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public String getNotes() {
        return notes;
    }

    // =====================================================
    // SETTERS
    // =====================================================

    public void setCredentialId(long credentialId) {
        this.credentialId = credentialId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
