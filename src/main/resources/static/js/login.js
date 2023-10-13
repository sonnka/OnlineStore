$(document).ready(function () {
    let emailField = $('#email');
    let passwordFiled = $('#password');
    let submitButton = $('#submit');

    submitButton.click(function (event) {
        event.preventDefault();
        login();
    });

    function login() {

        let formData = new URLSearchParams();

        formData.append('username', emailField.val());
        formData.append('password', passwordFiled.val());

        formData.forEach(p => console.log(p));

        $.ajax({
            type: 'POST',
            url: "http://localhost:8080/login",
            data: formData.toString(),
            processData: false,
            contentType: "application/x-www-form-urlencoded",
            success: function (response) {
                alert("success");
            },
            error: function (xhr, textStatus, errorThrown) {
                alert("Login error");
                console.log(xhr, textStatus, errorThrown);
            }
        });
    }
})