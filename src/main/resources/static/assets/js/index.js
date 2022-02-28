let main = {
    msg: function (msg_box_id, msg_id, i, is_valid) {
        let msg = ""
        switch (i) {
            case 0:
                msg = is_valid == true
                    ? "아이디는 소문자 영문시작 그리고 최소 5자리 이상 19자 이하 영문과 숫자만 입력 가능합니다."
                    : "아이디는 필수 입력입니다."
                break;
            case 1:
                msg = is_valid == true
                    ? "비밀번호는 8자 이상이어야 하며, 숫자/영문자/특수문자를 모두 포함해야 합니다."
                    : "비밀번호는 필수 입력입니다."
                break;
            case 2:
                msg = is_valid == true
                    ? "검증 비밀번호가 서로 다르거나 형식(8자 이상, 숫자/영문자/특수문자를 모두 포함)에 맞지 않습니다."
                    : "비밀번호 체크 동일여부 체크는 필수입니다."
                break;
            case 3:
                msg = is_valid == true
                    ? "닉네임은 1자리부터 10자리까지 영문,한글,숫자만 입력 가능합니다."
                    : "닉네임은 필수 입력입니다."
                break;
            case 4:
                msg = is_valid == true
                    ? "이메일 형식에 맞게 입력해주시기 바랍니다."
                    : "이메일은 필수 입력입니다."
                break;
        }
        $(msg_box_id).text(msg)
        $(msg_id).removeClass('is-valid')
        $(msg_id).addClass('is-invalid')
        if (msg.length > 40) {
            $(msg_id).parent().removeClass('mb-4')
            $(msg_id).parent().addClass('mb-10')
        } else {
            $(msg_id).parent().removeClass('mb-10')
            $(msg_id).parent().addClass('mb-4')
        }
    },
    init: function () {
        let valid_data = [
            /^[A-Za-z]{1}[A-Za-z0-9]{4,19}$/,
            /^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,25}$/,
            /^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,25}$/,
            /^[0-9a-zA-Z가-힣]{3,10}$/,
            /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i
        ];
        let _this = this;
        let input_boolean = false;
        $('#btn-login').on('click', function () {
            let list = $('input[class*=form-control]')
            $.each(list, function (i, value) {
                if ($(this).val().length == 0) {
                    _this.msg('.login-error-msg', value, i, false);
                    $(this).trigger('focus');
                    input_boolean = true
                    return false;
                } else {
                    $(this).removeClass('is-invalid')
                    input_boolean = false;
                }
            });
        });
        $('#btn-sign').on('click', function () {
            let list = $('input[class*=form-control]')
            let count = new Array()
            let checkItem = false;
            $.each(list, function (i, value) {
                if ($(this).val().length == 0) {
                    _this.msg($(this).nextAll('div').children('span'), value, i, false);
                    count.push(i);
                } else {
                    if (valid_data[i].test($(this).val())) {
                        // 나중에 통합해서 함수로 관리. 필요
                        $(this).removeClass('is-invalid')
                        $(this).addClass('is-valid')
                        $(this).parent().removeClass('mb-4')
                        $(this).parent().removeClass('mb-10')
                    } else if ($(this).val().length > 0) {
                        if (list[1].value == list[2].value && valid_data[i].test($(this).val()) && i == 2) {
                            $(this).removeClass('is-invalid')
                            $(this).addClass('is-valid')
                        } else {
                            _this.msg($(this).nextAll('div').children('span'), value, i, true);
                            count.push(i);
                        }
                    }
                }
            });
            $(list[count[0]]).trigger('focus');
            if (count.length == 0) {
                $('#btn-sign').attr('hidden', 'true');
                $('#btn-signon').removeAttr('hidden');
            }
        });
        $('#username, #password, #re-password, #email, #name').on('blur', function () {
            if ($(location).attr('pathname') == "/account/register") {
                let list = $('input[class*=form-control]')
                let _me = this;
                $.each(list, function (i, value) {
                    if (this.value.length == 0) {
                        if (_me == this) {
                            _this.msg($(this).nextAll('div').children('span'), value, i, false);
                        }
                    } else if (this.value.length > 0) {
                        if (_me == this && valid_data[i].test(_me.value) && i != 1 && i != 2) {
                            _this.check(this.value, $(this).attr('id'))
                        } else if (_me == this && !valid_data[i].test(_me.value)) {
                            _this.msg($(this).nextAll('div').children('span'), value, i, true);
                        }
                        if (_me == this && valid_data[i].test(_me.value)) {
                            // 나중에 통합해서 함수로 관리. 필요
                            $(this).removeClass('is-invalid')
                            $(this).addClass('is-valid')
                            $(this).parent().removeClass('mb-4')
                            $(this).parent().removeClass('mb-10')
                        }
                    }
                })
            }
        });
    },
    check: function (v, c) {
        $.ajax({
            type: 'GET',
            url: '/account/find/' + c + '/' + v,
            dataType: 'json',
            contentType: 'application/json; charset=utf-8'
        }).done(function (ok) {
            // 나중에 통합해서 함수로 관리. 필요
            $('#' + c).removeClass('is-invalid')
            $('#' + c).addClass('is-valid')
            $('#' + c).parent().removeClass('mb-4')
        }).fail(function (error) {
            // 나중에 통합해서 함수로 관리. 필요
            $('#' + c).removeClass('is-valid')
            $('#' + c).addClass('is-invalid')
            $('#' + c).parent().addClass('mb-4')
            $('#' + c).nextAll('div').children('span').text(error.responseJSON.message)
        });
    }

};


main.init();
