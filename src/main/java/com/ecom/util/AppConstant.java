package com.ecom.util;

public class AppConstant {

    // Thời gian tài khoản bị khóa, sau thời gian này tài khoản sẽ được mở khóa
    public static final long UNLOCK_DURATION_TIME = 3000;  // 3 giây
    //1 * 60 * 60 * 1000; ví dụ thực tế có thể là 1 tiếng: 1 * 60 * 60 * 1000

    // Số lần nhập sai tối đa trước khi tài khoản bị khóa
    public static final long ATTEMPT_TIME = 3;
}
