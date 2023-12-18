package si.fri.rso.simplsrecka.user.lib;

public class LoginResponse {

    private Integer userId;
    private String otp;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public LoginResponse(Integer userId, String otp) {
        this.userId = userId;
        this.otp = otp;
    }


}