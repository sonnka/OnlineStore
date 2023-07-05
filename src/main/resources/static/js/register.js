$(document).ready(function () {
    let registerButton = $('#registerButton');
    let name = $('#name');
    let surname = $('#surname');
    let email = $('#email');
    let password = $('#password');

    registerButton.click(function (event) {
        event.preventDefault();
        register();
    });

    function register() {
        let url = "/register";
        let urlSuccess = "/register?success";
        let urlError = "/register?error";
        let jsonData = {
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
