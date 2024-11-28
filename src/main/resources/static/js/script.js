$(function() {

// xác thực người dùng

    var $userRegister = $("#userRegister");

    $userRegister.validate({
        rules: {
            name: {
                required: true,
                lettersonly: true  // Tên chứa dấu tiếng Việt và khoảng trắng
            },
            email: {
                required: true,
                email: true
            },
            number: {
                required: true,
                numericOnly: true,
                minlength: 10,
                maxlength: 12
            },
            password: {
                required: true
            },
            confirmpassword: {
                required: true,
                equalTo: '#pass'
            },
            address: {
                required: true,
                all: true
            },
            city: {
                required: true,
                lettersonly: true  // Thành phố chứa dấu tiếng Việt và khoảng trắng
            },
            state: {
                required: true
            },
            pincode: {
                required: true,
                numericOnly: true
            },
            img: {
                required: true
            }
        },
        messages: {
            name: {
                required: 'Vui lòng nhập tên',
                lettersonly: 'Tên không hợp lệ, chỉ chứa chữ cái và dấu'
            },
            email: {
                required: 'Vui lòng nhập email',
                email: 'Email không hợp lệ'
            },
            number: {
                required: 'Vui lòng nhập số điện thoại',
                numericOnly: 'Số điện thoại không hợp lệ',
                minlength: 'Số điện thoại phải có ít nhất 10 chữ số',
                maxlength: 'Số điện thoại không được quá 12 chữ số'
            },
            password: {
                required: 'Vui lòng nhập mật khẩu'
            },
            confirmpassword: {
                required: 'Vui lòng nhập lại mật khẩu',
                equalTo: 'Mật khẩu không khớp'
            },
            address: {
                required: 'Vui lòng nhập địa chỉ',
                all: 'Địa chỉ không hợp lệ'
            },
            city: {
                required: 'Vui lòng nhập thành phố',
                lettersonly: 'Tên thành phố không hợp lệ'
            },
            state: {
                required: 'Vui lòng nhập tỉnh/thành'
            },
            pincode: {
                required: 'Vui lòng nhập mã bưu điện',
                numericOnly: 'Mã bưu điện không hợp lệ'
            },
            img: {
                required: 'Vui lòng tải ảnh lên'
            }
        }
    })
// Xác thực Đơn Hàng

    var $orders = $("#orders");

    $orders.validate({
        rules: {
            firstName: {
                required: true,
                lettersonly: true
            },
            lastName: {
                required: true,
                lettersonly: true
            },
            email: {
                required: true,
                space: true,
                email: true
            },
            number: {
                required: true,
                space: true,
                numericOnly: true,
                minlength: 10,
                maxlength: 12
            },
            address: {
                required: true,
                all: true
            },
            city: {
                required: true,
                space: true
            },
            state: {
                required: true
            },
            pincode: {
                required: true,
                space: true,
                numericOnly: true
            },
            paymentType: {
                required: true
            }
        },
        messages: {
            firstName: {
                required: 'Vui lòng nhập họ',
                lettersonly: 'Tên không hợp lệ'
            },
            lastName: {
                required: 'Vui lòng nhập tên',
                lettersonly: 'Tên không hợp lệ'
            },
            email: {
                required: 'Vui lòng nhập email',
                space: 'Không được chứa khoảng trắng',
                email: 'Email không hợp lệ'
            },
            number: {
                required: 'Vui lòng nhập số điện thoại',
                space: 'Không được chứa khoảng trắng',
                numericOnly: 'Số điện thoại không hợp lệ',
                minlength: 'Số điện thoại phải có ít nhất 10 chữ số',
                maxlength: 'Số điện thoại không được quá 12 chữ số'
            },
            address: {
                required: 'Vui lòng nhập địa chỉ',
                all: 'Địa chỉ không hợp lệ'
            },
            city: {
                required: 'Vui lòng nhập thành phố',
                space: 'Không được chứa khoảng trắng'
            },
            state: {
                required: 'Vui lòng chọn bang'
            },
            pincode: {
                required: 'Vui lòng nhập mã bưu điện',
                space: 'Không được chứa khoảng trắng',
                numericOnly: 'Mã bưu điện không hợp lệ'
            },
            paymentType: {
                required: 'Vui lòng chọn phương thức thanh toán'
            }
        }
    })

// Xác thực Đặt lại Mật Khẩu

    var $resetPassword = $("#resetPassword");

    $resetPassword.validate({
        rules: {
            password: {
                required: true,
                space: true
            },
            confirmPassword: {
                required: true,
                space: true,
                equalTo: '#pass'
            }
        },
        messages: {
            password: {
                required: 'Vui lòng nhập mật khẩu',
                space: 'Không được chứa khoảng trắng'
            },
            confirmPassword: {
                required: 'Vui lòng xác nhận mật khẩu',
                space: 'Không được chứa khoảng trắng',
                equalTo: 'Mật khẩu không khớp'
            }
        }
    })
})
    // Phương thức `lettersonly` cho phép dấu tiếng Việt và khoảng trắng
    jQuery.validator.addMethod('lettersonly', function(value, element) {
        return /^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểễếỄỆỉịọỏốồổỗộớờởỡợụủứừửữựýỳỵỷỹ0-9_,.\s-]+$/.test(value);
    });

    jQuery.validator.addMethod('space', function(value, element) {
        return /^[^-\s]+$/.test(value);
    });

    jQuery.validator.addMethod('all', function(value, element) {
        return /^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểễếỄỆỉịọỏốồổỗộớờởỡợụủứừửữựýỳỵỷỹ0-9_,.\s-]+$/.test(value);
    });

    jQuery.validator.addMethod('numericOnly', function(value, element) {
        return /^[0-9]+$/.test(value);
    });

