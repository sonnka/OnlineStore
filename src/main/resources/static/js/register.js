$(document).ready(function () {
    var registerButton = $('#registerButton');
    var name = $('#name');
    var surname = $('#surname');
    var email = $('#email');
    var password = $('#password');

    registerButton.click(function (event) {
        event.preventDefault();
        register();
    });


    function register() {
        url = "/register";
        urlSuccess = "/register?success";
        urlError = "/register?error";
        jsonData = {
            "name": name.val(),
            "surname": surname.val(),
            "email": email.val(),
            "password": password.val()
        };

        $.ajax({
            type: "POST",
            url: url,
            data: JSON.stringify(jsonData),
            contentType: 'application/json',
            success: function () {
                window.location = urlSuccess;
            }

        }).fail(function () {
            window.location = urlError;
        });
    }
})
